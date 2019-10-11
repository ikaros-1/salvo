package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.component.LoggerPlayer;
import com.codeoftheweb.salvo.model.*;
import com.codeoftheweb.salvo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class SalvoApplication {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public ActivePlayerStore activeUserStore(){
	    return new ActivePlayerStore();
    }


	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, SalvoRepository salvoRepository,ScoreRepository scoreRepository){
		return (args) ->{

            playerRepository.save(new Player("j.bauer@ctu.gov","1234"));
            playerRepository.save(new Player("c.obrian@ctu.gov","hello"));
            Game game=new Game();
            gameRepository.save(game);
            gamePlayerRepository.save(new GamePlayer(gameRepository.getOne((long)1),playerRepository.getOne((long)1)));
            gamePlayerRepository.save(new GamePlayer(gameRepository.getOne((long)1),playerRepository.getOne((long)2)));
            game=new Game();
            gameRepository.save(game);
            gamePlayerRepository.save(new GamePlayer(gameRepository.getOne((long)2),playerRepository.getOne((long)1)));
            gamePlayerRepository.save(new GamePlayer(gameRepository.getOne((long)2),playerRepository.getOne((long)2)));
            game=new Game();
            gameRepository.save(game);

            List<String> localitation1 = Arrays.asList(new String[]{"H1", "H2", "H3"});

            shipRepository.save(new Ship(TypeShip.BattleShip,gamePlayerRepository.getOne((long)1),localitation1));
//
            localitation1 = Arrays.asList(new String[]{"A2", "B2", "C2"});

            shipRepository.save(new Ship(TypeShip.BattleShip,gamePlayerRepository.getOne((long)1),localitation1));

            localitation1 = Arrays.asList(new String[]{"A1", "A2", "A3"});

            shipRepository.save(new Ship(TypeShip.BattleShip,gamePlayerRepository.getOne((long)2),localitation1));

            localitation1=Arrays.asList(new String[]{"F5","D6","A9"});

            salvoRepository.save(new Salvo(gamePlayerRepository.getOne(1L),1,localitation1));

            localitation1=Arrays.asList(new String[]{"A1","B6","D3"});

            salvoRepository.save(new Salvo(gamePlayerRepository.getOne(2L),1,localitation1));

            localitation1=Arrays.asList(new String[]{"F1","C6","A8"});

            salvoRepository.save(new Salvo(gamePlayerRepository.getOne(2L),2,localitation1));

            scoreRepository.save(new Score(gameRepository.getOne(1L),playerRepository.getOne(1L),  1F));
            scoreRepository.save(new Score(gameRepository.getOne(1L),playerRepository.getOne(2L),0));
            scoreRepository.save(new Score(gameRepository.getOne(2L),playerRepository.getOne(1L),  0.5F));
            scoreRepository.save(new Score(gameRepository.getOne(2L),playerRepository.getOne(2L), 0.5F));
		};
	}


    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(inputName-> {
            Player player = playerRepository.findByUserName(inputName);
            if (player != null) {
                return new User(player.getUserName(), passwordEncoder.encode(player.getPassword()),
                        AuthorityUtils.createAuthorityList("USER"));
            } else {
                throw new UsernameNotFoundException("Unknown user: " + inputName);
            }
        }).passwordEncoder(passwordEncoder());
    }

}

@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    ActivePlayerStore activePlayerStore;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/web/games.html").permitAll()
                .antMatchers("/web/js/**").permitAll()
                .antMatchers("/web/css/**").permitAll()
                .antMatchers("/api/login").permitAll()
                .antMatchers("/api/players").permitAll()
                .antMatchers("/api/games").hasAuthority("USER")
                .antMatchers("/api/game_view").hasAuthority("USER")
                .antMatchers("/api/gameslist").hasAuthority("USER")
                .antMatchers("/api/leaderboard").hasAuthority("USER")
                .antMatchers("/rest/**").hasAuthority("ADMIN")
                //.antMatchers("/rest/**").hasAuthority("ADMIN")
                //.antMatchers("/web/**").permitAll()
                .anyRequest().denyAll()
            .and()
                .formLogin()
                .loginPage("/web/login.html")
                .loginProcessingUrl("/api/login")
                //.successForwardUrl("/web/games.html")
                .permitAll()
            .and()
                .logout()
                .logoutUrl("/api/logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");

        // turn off checking for CSRF tokens
        http.csrf().disable();

        // if user is not authenticated, just send an authentication failure response
        http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if login is successful, just clear the flags asking for authentication
        // Agrego LoggerPlayer como atributo de httpsession para agregarlo a la lista de activos

        http.formLogin().successHandler((req, res, auth) -> {
            HttpSession session= req.getSession(false);
            if(session !=null) {
                 LoggerPlayer loggerPlayer = new LoggerPlayer(auth.getName(), activePlayerStore);
                 session.setAttribute("player", loggerPlayer);
                 session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            }
        });

        // if login fails, just send an authentication failure response
        http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if logout is successful, just send a success response
        // Remuevo el atributo player de la http session para sacarlo de la lista de activos
        http.logout().logoutSuccessHandler((req,res, exc) -> {
            HttpSession session = req.getSession();
            if (session != null) {
                session.removeAttribute("player");

            }
        });


    }


}


package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.model.*;
import com.codeoftheweb.salvo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

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

            List<String> localitation1 = Arrays.asList(new String[]{"H1", "H2", "H3"});

            shipRepository.save(new Ship(TypeShip.Battleship,gamePlayerRepository.getOne((long)1),localitation1));
//
            localitation1 = Arrays.asList(new String[]{"A2", "B2", "C2"});

            shipRepository.save(new Ship(TypeShip.Battleship,gamePlayerRepository.getOne((long)1),localitation1));

            localitation1 = Arrays.asList(new String[]{"A1", "A2", "A3"});

            shipRepository.save(new Ship(TypeShip.Battleship,gamePlayerRepository.getOne((long)2),localitation1));

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





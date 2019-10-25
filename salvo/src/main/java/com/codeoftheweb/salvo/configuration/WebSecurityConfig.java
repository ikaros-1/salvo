package com.codeoftheweb.salvo.configuration;

import com.codeoftheweb.salvo.ActivePlayerStore;
import com.codeoftheweb.salvo.component.LoggerPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    ActivePlayerStore activePlayerStore;

    @Bean("authenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/web/games.html").permitAll()
                .antMatchers("/web/js/**").permitAll()
                .antMatchers("/web/css/**").permitAll()
                .antMatchers("/api/login").permitAll()
                .antMatchers("/api/players").permitAll()
                .antMatchers("/favicon.ico").permitAll()
                .antMatchers("/api/leaderboard").permitAll()
                .antMatchers("/api/guest").permitAll()
                .antMatchers("/api/games").hasAuthority("USER")
                .antMatchers("/api/games/**").hasAuthority("USER")
                .antMatchers("/api/game_view/*").hasAuthority("USER")
                .antMatchers("/api/gameslist").hasAuthority("USER")
                .antMatchers("/web/game.html").hasAuthority("USER")
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
                .logoutUrl("/api/logout");

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
                //    session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
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

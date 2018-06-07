package com.azzgil.coeditor.configs;

import com.azzgil.coeditor.beans.authentication.rest.RESTAuthenticationEntryPoint;
import com.azzgil.coeditor.beans.authentication.rest.RESTAuthenticationFailureHandler;
import com.azzgil.coeditor.beans.authentication.rest.RESTAuthenticationSuccessHandler;
import com.azzgil.coeditor.beans.authentication.rest.RESTAuthenticationSuccessLogoutHandler;
import com.azzgil.coeditor.beans.authentication.users.UserDetailsServiceDefaultImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.annotation.ApplicationScope;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(applicationContext.getBean(DaoAuthenticationProvider.class));
    }

    @Bean
    @ApplicationScope
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                            PasswordEncoder encoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(encoder);
        return authProvider;
    }

    @Bean
    @ApplicationScope
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceDefaultImpl();
    }

    @Bean
    @ApplicationScope
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // for the sake of simplicity disable the csrf-token generation
        http.csrf().disable();

        // make accessible the following paths:
        // * /login.html, /login, /auth/register - for user's signing in/up
        // * /js/**, /css/** - for accessing public resources
        // * /, /index.html - the main page of the app
        //
        // other paths require user to be authenticated
        http.authorizeRequests()
                .antMatchers("/auth/register", "/login", "/login.html", "/js/**",
                        "/css/**", "/", "/index.html").permitAll()
                .anyRequest().authenticated();
        http.exceptionHandling().authenticationEntryPoint(new RESTAuthenticationEntryPoint());
        http.formLogin()
                .successHandler(new RESTAuthenticationSuccessHandler())
                .failureHandler(new RESTAuthenticationFailureHandler());
        http.logout()
                .logoutSuccessHandler(new RESTAuthenticationSuccessLogoutHandler())
                .permitAll();

    }
}

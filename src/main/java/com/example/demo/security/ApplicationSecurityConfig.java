package com.example.demo.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity // adnotacja która określa że klasa konfiguracyjna dotyczy "Security"
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests() // każde żądanie musi być autoryzowane (czyli odpowiednie prawa dostępu do zasobu)
                .antMatchers("/", "index") // co ma być widoczne bez logowania (biała lista)
                .permitAll()
                .anyRequest() // każde żądanie
                .authenticated() // musi prejść autentykację (logi i hasło)
                .and()
                .httpBasic(); // używamy podstawowej autentykacji czyli mechanizmu BasicAuth
    }
}
package com.example.demo.security.config;

import com.example.demo.security.model.MyUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private MyUserDetailService userDetailService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable) //odblokowuje zapytania post (creteUser)
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers("/login", "/register/**").permitAll();
                    registry.requestMatchers("/admin/**").hasRole("ADMIN");
                    registry.requestMatchers("/user/**", "/entry", "/exit", "/saveEntry", "/exitEntry").hasRole("USER");
                    registry.anyRequest().hasRole("ADMIN");

//                    registry.anyRequest().authenticated();
                })
//                .formLogin(AbstractAuthenticationFilterConfigurer::permitAll) // formularz logowania do aplikacji
//                .build();
                .formLogin(httpSecurityFormLoginConfigurer -> {
                    httpSecurityFormLoginConfigurer
                            .loginPage("/login")
                            .successHandler(new AuthenticationSuccessHandler()) // gdzie ma nas przenieść po autoryzacji
                            .permitAll();
                })
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()

                )
                .build();
    }

    //in memory users
//    @Bean
//    public UserDetailsService userDetailsService() {   //dzieki tej metodzie aplikacja nie generuje hasła
//        UserDetails normalUser = User.builder()
//                .username("gc")
//                .password("$2a$12$vW1WA5VzsgMRu68jHjG/VO0N6gfk5djEzCYajLCCi7r/XIAmNadUm") //1234 online bcript encoder
//                .roles("USER")
//                .build();
//
//        UserDetails adminUser = User.builder()
//                .username("admn")
//                .password("$2a$12$xDwY6oLZ2o/aAeZSCHW8VO5swPCPNvx5V0qCh7BJa2bWwyHs.Mkvi") //12345 online bcript encoder
//                .roles("ADMIN", "USER")
//                .build();
//        return new InMemoryUserDetailsManager(normalUser, adminUser);
//    }


    @Bean
    public UserDetailsService userDetailsService() {
        return userDetailService;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
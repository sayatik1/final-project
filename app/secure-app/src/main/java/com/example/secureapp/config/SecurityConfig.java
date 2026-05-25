package com.example.secureapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public InMemoryUserDetailsManager users(PasswordEncoder passwordEncoder) {

        UserDetails viewer = User.builder()
                .username("viewer")
                .password(passwordEncoder.encode("password"))
                .roles("VIEWER")
                .build();

        UserDetails editor = User.builder()
                .username("editor")
                .password(passwordEncoder.encode("password"))
                .roles("EDITOR")
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("password"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(
                viewer,
                editor,
                admin
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/").permitAll()

                        .requestMatchers("/files")
                        .hasAnyRole("VIEWER", "EDITOR", "ADMIN")

                        .requestMatchers("/upload")
                        .hasAnyRole("EDITOR", "ADMIN")

                        .requestMatchers("/admin")
                        .hasRole("ADMIN")

                        .anyRequest()
                        .authenticated()
                )

                .httpBasic(httpBasic -> {});

        return http.build();
    }
}
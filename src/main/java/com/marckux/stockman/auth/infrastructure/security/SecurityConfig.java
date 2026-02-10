package com.marckux.stockman.auth.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  /**
   * Crea una cadena de filtros secuenciales
   * @param http
   * @return
   * @throws Exception
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      // Acceso a rutas
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/api/public/**").permitAll()
        .anyRequest().authenticated()
      )
      .httpBasic(Customizer.withDefaults())
      .formLogin(Customizer.withDefaults())
    ;
    return http.build();
  }

  @Bean
  public UserDetailsService userDetailsService() {
    UserDetails user = User.builder()
      .username("user")
      .password("{noop}user")
      .roles("USER")
      .build()
    ;
    UserDetails admin = User.builder()
      .username("admin")
      .password("{noop}admin")
      .roles("ADMIN")
      .build()
    ;
    return new InMemoryUserDetailsManager(user, admin);
  }
  
}

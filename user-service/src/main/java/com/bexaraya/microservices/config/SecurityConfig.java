package com.bexaraya.microservices.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Configuración de seguridad de la aplicación.
 * 
 * Usa la nueva Lambda DSL introducida en Spring Security 6 (Spring Boot 3.x).
 * 
 * - Define usuarios en memoria.
 * - Configura autenticación y autorización.
 * - Habilita HTTP Basic y login por formulario.
 */
//@Configuration
//@EnableWebSecurity
public class SecurityConfig {

    /**
     * Define un manejador de usuarios en memoria.
     * 
     * Aquí se crean dos usuarios (bexaAdmin y bexaUser) con sus contraseñas y roles.
     * 
     * @return un InMemoryUserDetailsManager con los usuarios registrados.
     */
    /*@Bean
    public InMemoryUserDetailsManager userDetailsService() {
        // Crea un usuario con rol ADMIN
        UserDetails admin = User.withUsername("bexaAdmin")
                // Encripta la contraseña usando BCrypt
                .password(passwordEncoder().encode("abc123"))
                // Asigna el rol ADMIN
                .roles("ADMIN")
                .build();

        // Crea un usuario con rol USER
        UserDetails user = User.withUsername("bexaUser")
                // Encripta la contraseña usando BCrypt
                .password(passwordEncoder().encode("123"))
                // Asigna el rol USER
                .roles("USER")
                .build();

        // Retorna ambos usuarios en un almacenamiento en memoria
        return new InMemoryUserDetailsManager(admin, user);
    }*/

    /**
     * Configura las reglas de seguridad HTTP para la aplicación.
     * 
     * Aquí se define qué rutas están protegidas y quién puede acceder a ellas.
     * También se activan los mecanismos de autenticación (básica y formulario).
     * 
     * @param http objeto HttpSecurity que permite configurar la seguridad web.
     * @return el SecurityFilterChain construido con las reglas aplicadas.
     * @throws Exception si ocurre algún error durante la configuración.
     */
    /*@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        
    	RequestMatcher deleteMatcher = request ->
								        "DELETE".equals(request.getMethod()) ;
								        //&& request.getRequestURI().contains("delete");
    	
    	http
            .csrf(csrf -> csrf.disable()) // opcional segun caso
            // Sección de autorización de solicitudes HTTP
            .authorizeHttpRequests(auth -> auth
                // Solo los usuarios con rol ADMIN pueden acceder a /delete
                .requestMatchers(deleteMatcher).hasAnyRole("ADMIN", "USER")
                //.requestMatchers(deleteMatcher).hasRole("ADMIN")
                // Cualquier otra petición requiere estar autenticado
                .anyRequest().authenticated()
                
                // otras reglas
                //.anyRequest().permitAll()
            )
            // Habilita autenticación básica (popup del navegador)
            .httpBasic(Customizer.withDefaults())
            // Habilita login mediante formulario HTML (Spring Security crea uno por defecto)
            .formLogin(Customizer.withDefaults());

        // Construye y devuelve la cadena de filtros de seguridad
        return http.build();
    }*/

    /**
     * Define el codificador de contraseñas que usará Spring Security.
     * 
     * BCrypt es un algoritmo de hashing seguro y recomendado para almacenar contraseñas.
     * 
     * @return un PasswordEncoder basado en BCrypt.
     */
    /*@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }*/
}
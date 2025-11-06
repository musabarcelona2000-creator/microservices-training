package com.bexaraya.microservices.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@Profile("!test") // No se carga si el perfil activo es 'test'
public class SecurityConfigJWT {

	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		RequestMatcher deleteMatcher = request ->
                      "DELETE".equals(request.getMethod()) 
                      && request.getRequestURI().contains("delete");
		
        http
            // Deshabilita CSRF para APIs REST
            .csrf(csrf -> csrf.disable())

            // Configura autorización
            .authorizeHttpRequests(auth -> auth
                // Endpoints de administración solo para rol ADMIN
            	// para que filtre por roles de oauth, hasAuthority, no hasRol (porque anyade prefijo SCOPE_ y por eso tenemos que anyadir el jwtAuthenticationConverter) 
                //.requestMatchers(deleteMatcher).hasAnyAuthority("ADMIN" /*, "USER"*/)
            	.requestMatchers(HttpMethod.DELETE, "/api/**").hasAuthority("ADMIN")
            	// Otros endpoints requieren autenticación
                .anyRequest().authenticated()
            )

            // Configura Resource Server con JWT
            .oauth2ResourceServer(oauth2 -> oauth2
                //.jwt(Customizer.withDefaults())
            	.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );

        return http.build();
    }
	
	@Bean
	JwtAuthenticationConverter jwtAuthenticationConverter() {
	    JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
	    converter.setAuthoritiesClaimName("realm_access.roles"); // <-- dónde están los roles
	    converter.setAuthorityPrefix(""); // quita "SCOPE_"

	    JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
	    jwtConverter.setJwtGrantedAuthoritiesConverter(converter);
	    return jwtConverter;
	}
}
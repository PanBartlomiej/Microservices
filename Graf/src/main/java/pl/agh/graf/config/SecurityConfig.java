package pl.agh.graf.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

/**
 * Konfiguracja zabezpieczeń Spring Security dla aplikacji obsługującej uwierzytelnianie OAuth 2.0.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthConverter jwtAuthConverter;
    /**
     * Konfiguruje łańcuch filtrów zabezpieczeń dla aplikacji, uwzględniając uwierzytelnianie OAuth 2.0.
     *
     * @param http                      Obiekt konfiguracji zabezpieczeń HTTP.
     * @return Łańcuch filtrów zabezpieczeń dla aplikacji.
     * @throws Exception Rzucany, gdy wystąpi problem podczas konfiguracji zabezpieczeń.
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Konfiguracja zabezpieczeń HTTP
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeRequests()
                .requestMatchers("/actuator/**").permitAll() // Permit access to actuator endpoints
                .anyRequest().authenticated()
                .and()
                .oauth2ResourceServer(oauth ->oauth.jwt(jwt ->jwt.jwtAuthenticationConverter(jwtAuthConverter)))
                .sessionManagement(session ->session.sessionCreationPolicy(STATELESS));
        // Zwróć zbudowany łańcuch filtrów zabezpieczeń
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("**"));
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");

        configuration.addAllowedMethod("OPTIONS");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("DELETE");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", configuration);
        return urlBasedCorsConfigurationSource;
    }
}

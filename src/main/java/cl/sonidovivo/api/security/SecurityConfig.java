package cl.sonidovivo.api.security;

import cl.sonidovivo.api.config.CorsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;
    private final CorsProperties corsProperties;
    private final Environment environment;

    public SecurityConfig(
            JwtService jwtService,
            SecurityErrorWriter securityErrorWriter,
            CorsProperties corsProperties,
            Environment environment) {
        this.corsProperties = corsProperties;
        this.environment = environment;
        this.jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService, securityErrorWriter);
        this.authenticationEntryPoint = new RestAuthenticationEntryPoint(securityErrorWriter);
        this.accessDeniedHandler = new RestAccessDeniedHandler(securityErrorWriter);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/error").permitAll();
                    auth.requestMatchers("/api/health").permitAll();
                    auth.requestMatchers("/api/auth/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/productos", "/api/productos/**").permitAll();
                    auth.requestMatchers(HttpMethod.POST, "/api/contacto").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/contacto").hasRole("ADMIN");
                    auth.requestMatchers(HttpMethod.POST, "/api/productos").hasRole("ADMIN");
                    auth.requestMatchers(HttpMethod.PUT, "/api/productos/**").hasRole("ADMIN");
                    auth.requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("ADMIN");
                    auth.requestMatchers(
                            "/swagger-ui.html",
                            "/swagger-ui/**",
                            "/v3/api-docs",
                            "/v3/api-docs/**").permitAll();
                    if (environment.acceptsProfiles(Profiles.of("dev"))) {
                        auth.requestMatchers("/h2-console/**").permitAll();
                    }
                    auth.anyRequest().permitAll();
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(corsProperties.allowedOrigins());
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of(HttpHeaders.AUTHORIZATION));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
package br.com.one.sentiment_analysis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .headers(headers ->
                headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
            )
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth

                // H2 Console
                .requestMatchers("/h2-console/**").permitAll()

                // Rotas públicas
                .requestMatchers(
                    "/api/v1/public/**",
                    "/actuator/health",
                    "/actuator/info",
                    "/actuator/prometheus"
                ).permitAll()

                .requestMatchers(HttpMethod.GET,
                    "/api/v1/sentiment"
                ).permitAll()

                .requestMatchers(HttpMethod.POST,
                    "/api/v1/sentiment"
                ).permitAll()

                // Rotas protegidas
                .requestMatchers("/api/v1/**").authenticated()

                // Qualquer outra rota
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
            .username("user")
            .password(passwordEncoder().encode("123"))
            .roles("USER")
            .build();

        UserDetails admin = User.builder()
            .username("adm")
            .password(passwordEncoder().encode("adm123"))
            .roles("ADMIN")
            .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

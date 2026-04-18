package com.example.MergeX.Security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class WebSecurityConf {
    private final JwtAuthFilter jwtAuthFilter;
    public WebSecurityConf(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 🌐 CORS
                .cors(Customizer.withDefaults())

                // 🚫 Stateless API
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 🔑 JWT filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                // 🚨 No redirects
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, exx) ->
                                res.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                        )
                )

                .authorizeHttpRequests(auth -> auth
                        // ✅ allow preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ✅ auth endpoints
                        .requestMatchers("/auth/**").permitAll()

                        // 🔐 ADMIN ONLY
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")

                        // 🔐 USER AUTH REQUIRED
                        .requestMatchers("/users/**", "/projects/**").authenticated()

                        // ❌ block everything else
                        .anyRequest().permitAll()
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

package com.attendance.scheduler.infra.config.security;

import com.attendance.scheduler.infra.config.security.jwt.JwtAccessDeniedHandler;
import com.attendance.scheduler.infra.config.security.jwt.JwtAuthenticationEntryPoint;
import com.attendance.scheduler.infra.config.security.jwt.JwtAuthenticationFilter;
import com.attendance.scheduler.infra.config.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    // 기존 Thymeleaf(세션) 화이트리스트 — 도메인 이관이 끝나면 축소/제거
    public static final String[] ENDPOINTS_WHITELIST = {
            "/", "/submit", "/completion",
            "/class/**",
            "/board/**",
            "/join/**",
            "/cert/**",
            "/login/**",
            "/help/**",
            "/comment/**",
            "/css/**",
            "/js/**"
    };

    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    private final JwtProvider jwtProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * REST API 체인 — stateless + JWT. React SPA 가 사용.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {

        http
                .securityMatcher("/api/**")
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login", "/api/auth/refresh").permitAll()
                        .requestMatchers("/api/class/**").permitAll()
                        .requestMatchers("/api/comment/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/board/**").permitAll()
                        .requestMatchers("/api/board/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/manage/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_TEACHER")
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler))
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 기존 웹(Thymeleaf) 체인 — 세션 + 폼 로그인. 도메인 이관이 끝나면 제거 예정.
     */
    @Bean
    public SecurityFilterChain webFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .securityMatcher("/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(ENDPOINTS_WHITELIST).permitAll()
                        .requestMatchers("/admin/**")
                        .hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/manage/*")
                        .hasAnyAuthority("ROLE_ADMIN", "ROLE_TEACHER")
                        .anyRequest().authenticated())

                .formLogin(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer
                        .defaultSuccessUrl("/manage/class", true)
                        .failureHandler(customAuthenticationFailureHandler)
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .permitAll()
                )

                .logout(httpSecurityFormLogoutConfigurer -> httpSecurityFormLogoutConfigurer
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessUrl("/"))

                .sessionManagement(sessionManagement -> sessionManagement
                        .invalidSessionUrl("/login")
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(true)
                        .expiredUrl("/login"));

        return httpSecurity.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.stream(allowedOrigins.split(",")).map(String::trim).toList());
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}

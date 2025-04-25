package com.example.demo.config;

import com.example.demo.service.JWTTokenService;
import com.example.demo.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserService userService;
    private final JWTTokenService jwtTokenService;
    private final JWTAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(UserService userService,  JWTTokenService jwtTokenService, JWTAuthenticationFilter jwtAuthenticationFilter){
        this.userService = userService;
        this.jwtTokenService = jwtTokenService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers("/UrlShortener/auth/**").permitAll() // Реєстрація, логін
                            .requestMatchers(HttpMethod.GET, "/UrlShortener/links/{shortUrl}").permitAll() // Перехід (редирект)
                            .requestMatchers(HttpMethod.GET, "/UrlShortener/links/{shortUrl}/stats").permitAll() // Статистика для всіх

                            // Ендпоїнти, що вимагають автентифікації
                            .requestMatchers(HttpMethod.POST, "/UrlShortener/links/create").authenticated() // Створення
                            .requestMatchers(HttpMethod.GET, "/UrlShortener/links/my").authenticated() // Всі мої посилання
                            .requestMatchers(HttpMethod.GET, "/UrlShortener/links/my/active").authenticated() // Активні мої
                            .requestMatchers(HttpMethod.DELETE, "/UrlShortener/links/{shortUrl}").authenticated() // Видалення мого

                            // Решта ендпоїнтів (якщо є, напр. /users)
                            .requestMatchers("/UrlShortener/users/**").authenticated() // Припустимо, що вони теж захищені

                            // Забороняємо все інше (залежить від політики)
                            .anyRequest().authenticated(); // Або .denyAll() якщо не хочете непередбачених дозволів
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
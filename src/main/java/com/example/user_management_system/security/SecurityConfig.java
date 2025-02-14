package com.example.user_management_system.security;

import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // For testing purposes - enable in production
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(
                                "/login",
                                "/signup",
                                "/favicon.ico",
                                "/css/**",
                                "/js/**"
                        ).permitAll()
                        .requestMatchers("/customerhome", "/customer/**").hasRole("USER")
                        .requestMatchers("/adminhome", "/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler((request, response, authentication) -> {
                            HttpSession session = request.getSession();
                            // Custom success handler to redirect based on role
                            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
                            for (GrantedAuthority authority : authorities) {
                                if (authority.getAuthority().equals("ROLE_ADMIN")) {
                                    session.setAttribute("admin", true);
                                    session.setAttribute("username", authentication.getName());
                                    response.sendRedirect("/adminhome");
                                    return;
                                } else if (authority.getAuthority().equals("ROLE_USER")) {
                                    session.setAttribute("user", true);
                                    session.setAttribute("username", authentication.getName());
                                    response.sendRedirect("/customerhome");

                                    return;
                                }
                            }
                            // Default fallback
                            response.sendRedirect("/");
                        })
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .userDetailsService(userDetailsService);  // Add this line

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

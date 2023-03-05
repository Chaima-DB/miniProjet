package ma.dbibih.miniprojet.security;

import lombok.RequiredArgsConstructor;
import ma.dbibih.miniprojet.security.filters.JwtAuthenticationFilter;
import ma.dbibih.miniprojet.security.filters.JwtAuthorizationFilter;
import ma.dbibih.miniprojet.serviceImpl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true)
public class SecurityConfig {
    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf(AbstractHttpConfigurer::disable)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests(auth -> {
                    auth.antMatchers("/api/users/generate", "/api/users/batch", "/api/users/auth").permitAll();
                    auth.antMatchers("/v3/api-docs/**", "/swagger-resources/**", "/swagger-ui/**").permitAll();
                    auth.antMatchers("/h2-console/**").permitAll();
                    auth.anyRequest().authenticated();
                })
                .userDetailsService(userDetailsService)
                .headers(headers -> headers.frameOptions().sameOrigin());
        http.addFilter(new JwtAuthenticationFilter(authenticationManagerBean(new AuthenticationConfiguration())))
                .addFilterBefore(new JwtAuthorizationFilter(), JwtAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}

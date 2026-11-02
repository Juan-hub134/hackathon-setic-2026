package br.com.hackathon.setic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ConfigSecurity {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/login").permitAll()
                        .requestMatchers("/api/v1/gestor/setor/lista").hasAuthority("SCOPE_GESTOR_RISCO")
                        .requestMatchers("/api/v1/gestor/indicar/{id}").hasAuthority("SCOPE_GESTOR_RISCO")
                        .requestMatchers("/api/v1/gestor/remover/indicacao/{id}").hasAuthority("SCOPE_GESTOR_RISCO")
                        .requestMatchers("/api/v1/categorias").permitAll()
                        .requestMatchers("/api/v1/escalas/probabilidade").permitAll()
                        .requestMatchers("/api/v1/escalas/impacto").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 ->oauth2
                        .jwt(jwt -> {}));
        return http.build();
    }
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

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
                        // Login público
                        .requestMatchers("/api/v1/login").permitAll()

                        // Endpoints de apoio a formulários - públicos
                        .requestMatchers("/api/v1/categorias").permitAll()
                        .requestMatchers("/api/v1/escalas/probabilidade").permitAll()
                        .requestMatchers("/api/v1/escalas/impacto").permitAll()
                        .requestMatchers("/api/v1/tratamentos").permitAll()
                        .requestMatchers("/api/v1/parametros").permitAll()

                        // Painel do Gestor de Risco
                        .requestMatchers("/api/v1/gestor/setor/lista").hasAuthority("SCOPE_GESTOR_RISCO")
                        .requestMatchers("/api/v1/gestor/indicar/{id}").hasAuthority("SCOPE_GESTOR_RISCO")
                        .requestMatchers("/api/v1/gestor/remover/indicacao/{id}").hasAuthority("SCOPE_GESTOR_RISCO")

                        // Riscos
                        .requestMatchers("/api/v1/registrar/risco").hasAuthority("SCOPE_RESPONSAVEL_PRR")
                        .requestMatchers("/api/v1/riscos").hasAnyAuthority("SCOPE_GESTOR_RISCO","SCOPE_RESPONSAVEL_PRR")
                        .requestMatchers("/api/v1/riscos/setores").hasAnyAuthority("SCOPE_CGGE","SCOPE_CSGR")
                        .requestMatchers("/api/v1/risco/filtrar/{id}").hasAnyAuthority("SCOPE_CGGE","SCOPE_CSGR")

                        // Contexto - apenas GESTOR_RISCO cria e edita; CSGR e CGGE visualizam
                        .requestMatchers("/api/v1/contexto").hasAuthority("SCOPE_GESTOR_RISCO")
                        .requestMatchers("/api/v1/contexto/risco/{idRisco}").hasAnyAuthority("SCOPE_GESTOR_RISCO","SCOPE_CSGR","SCOPE_CGGE")
                        .requestMatchers("/api/v1/contexto/etapa/{idEtapa}/concluir").hasAuthority("SCOPE_GESTOR_RISCO")

                        // Plano de Resposta a Riscos (PRR) - GESTOR_RISCO cria e edita; CSGR e CGGE visualizam
                        .requestMatchers("/api/v1/prr").hasAuthority("SCOPE_GESTOR_RISCO")
                        .requestMatchers("/api/v1/prr/{id}").hasAuthority("SCOPE_GESTOR_RISCO")
                        .requestMatchers("/api/v1/prr/contexto/{idContexto}").hasAnyAuthority("SCOPE_GESTOR_RISCO","SCOPE_CSGR","SCOPE_CGGE")
                        .requestMatchers("/api/v1/parecer").hasAuthority("SCOPE_CSGR")
                        .requestMatchers("/api/v1/parecer/prr/{idResposta}").hasAnyAuthority("SCOPE_GESTOR_RISCO","SCOPE_CSGR","SCOPE_CGGE")

                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> {}));
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

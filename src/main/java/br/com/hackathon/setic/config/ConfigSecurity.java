package br.com.hackathon.setic.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;

@Configuration
public class ConfigSecurity {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
<<<<<<< Updated upstream
                        .requestMatchers("/api/v1/login", "/login.html", "/css/**", "/js/**", "/images/**", "/*.html").permitAll()
                        .requestMatchers("/api/v1/gestor/setor/lista").hasAuthority("SCOPE_GESTOR_RISCO")
=======
                        // 1. LIBERAÇÃO PÚBLICA DAS PÁGINAS HTML E RECURSOS ESTÁTICOS (CORREÇÃO DO ERRO 401)
                        .requestMatchers("/login.html", "/index.html", "/gestor.html", "/csgr.html", "/*.html", "/api.js", "/app.css", "/risk-matrix.js", "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()

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
                        .requestMatchers("/api/v1/csgr/solicitacoes").hasAuthority("SCOPE_GESTOR_RISCO")
                        .requestMatchers("/api/v1/csgr/solicitacoes/minhas").hasAuthority("SCOPE_GESTOR_RISCO")
                        .requestMatchers("/api/v1/csgr/solicitacoes/pendentes").hasAuthority("SCOPE_CSGR")

>>>>>>> Stashed changes
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }

    Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter defaultConverter = new JwtGrantedAuthoritiesConverter();
        return jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>(defaultConverter.convert(jwt));
            String scope = normalizarScope(jwt.getClaimAsString("scope"));
            if (Set.of("GESTOR_RISCO", "RESPONSAVEL_PRR", "CSGR", "CGGE", "SERVIDOR", "ASCF").contains(scope)) {
                authorities.add(new SimpleGrantedAuthority("SCOPE_" + scope));
            }
            return new JwtAuthenticationToken(jwt, authorities);
        };
    }

    private String normalizarScope(String scope) {
        if (scope == null || scope.isBlank()) return "";

        String normalizado = Normalizer.normalize(scope, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase(Locale.ROOT)
                .replaceAll("[^A-Z0-9]+", "_")
                .replaceAll("^_|_$", "");

        return switch (normalizado) {
            case "GESTOR_DE_RISCO" -> "GESTOR_RISCO";
            case "RESPONSAVEL_PELO_PRR" -> "RESPONSAVEL_PRR";
            default -> normalizado;
        };
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
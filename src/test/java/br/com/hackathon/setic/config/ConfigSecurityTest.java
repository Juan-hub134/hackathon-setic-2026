package br.com.hackathon.setic.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigSecurityTest {
    private final ConfigSecurity configSecurity = new ConfigSecurity();

    @Test
    void mapsLegacyManagerRoleToExpectedAuthority() {
        var authentication = configSecurity.jwtAuthenticationConverter().convert(jwtWithScope("GESTOR DE RISCO"));

        assertThat(authentication.getAuthorities())
                .contains(new SimpleGrantedAuthority("SCOPE_GESTOR_RISCO"));
    }

    @Test
    void mapsLegacyPrResponsibilityRoleToExpectedAuthority() {
        var authentication = configSecurity.jwtAuthenticationConverter()
                .convert(jwtWithScope("RESPONSÁVEL PELO PRR"));

        assertThat(authentication.getAuthorities())
                .contains(new SimpleGrantedAuthority("SCOPE_RESPONSAVEL_PRR"));
    }

    private Jwt jwtWithScope(String scope) {
        return Jwt.withTokenValue("test-token")
                .header("alg", "none")
                .claim("scope", scope)
                .build();
    }
}

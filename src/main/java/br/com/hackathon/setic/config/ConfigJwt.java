package br.com.hackathon.setic.config;

import br.com.hackathon.setic.service.RsaKeyService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
public class ConfigJwt {
    private final RsaKeyService rsaKeyService;
    public ConfigJwt(RsaKeyService rsaKeyService) {
        this.rsaKeyService = rsaKeyService;
    }
    @Bean
    public RSAPrivateKey getCarregarPrivateKey(){
        return rsaKeyService.carregarPrivateKey();
    }
    @Bean
    public RSAPublicKey getCarregarPublicKey(){
        return rsaKeyService.carregarPublicKey();
    }
    @Bean
    public JwtDecoder jwtDecoder(RSAPublicKey publicKey){
        return NimbusJwtDecoder.withPublicKey(publicKey)
                .build();
    }
}

package br.com.hackathon.setic.service;

import br.com.hackathon.setic.model.Usuario;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.JWT;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
@Service
public class TokenService {
    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;
    public TokenService(RSAPrivateKey privatekey, RSAPublicKey publicKey){
        this.privateKey = privatekey;
        this.publicKey = publicKey;
    }
    public String gerarToken(Usuario usuario){
        Algorithm algorithm = Algorithm.RSA256(publicKey,privateKey);
        return JWT.create()
                .withSubject(String.valueOf(usuario.getIdUsuario()))
                .withClaim("scope", normalizarCargo(usuario.getCargo().getCargo()))
                .withClaim("setor", usuario.getSetor().getUnidade())
                .withExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
                .sign(algorithm);
    }

    private String normalizarCargo(String cargo) {
        String normalizado = Normalizer.normalize(cargo, Normalizer.Form.NFD)
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
}

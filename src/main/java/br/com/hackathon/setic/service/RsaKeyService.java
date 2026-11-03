package br.com.hackathon.setic.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class RsaKeyService {
    private final Path privateKeyPath;
    private final Path publicKeyPath;

    public RsaKeyService(
            @Value("${app.security.jwt.private-key:src/main/resources/keys/private_key.pem}") String privateKeyPath,
            @Value("${app.security.jwt.public-key:src/main/resources/keys/public_key.pem}") String publicKeyPath) {
        this.privateKeyPath = Path.of(privateKeyPath);
        this.publicKeyPath = Path.of(publicKeyPath);
    }

    public RSAPrivateKey carregarPrivateKey() {
        try {
<<<<<<< Updated upstream
            Path path = Paths.get("/home/peter/Documents/Faculdade/4º Período/Desenvolvimento WEB/typescript/hackathon-setic-2026/src/main/resources/keys/private_key.pem");
            String key = Files.readString(path);
=======
            String key = Files.readString(privateKeyPath);
>>>>>>> Stashed changes
            key = key.replaceAll("-----BEGIN[^-]+-----", "")
                    .replaceAll("-----END[^-]+-----", "")
                    .replaceAll("[^A-Za-z0-9+/=]", "");
            byte[] keyBytes = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) keyFactory.generatePrivate(spec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("erro ao carregarPrivateKey: " + e.getMessage(), e);
        }
    }

    public RSAPublicKey carregarPublicKey() {
        try {
<<<<<<< Updated upstream
            Path path = Paths.get("/home/peter/Documents/Faculdade/4º Período/Desenvolvimento WEB/typescript/hackathon-setic-2026/src/main/resources/keys/public_key.pem");
            String key = Files.readString(path);
=======
            String key = Files.readString(publicKeyPath);
>>>>>>> Stashed changes
            key = key.replaceAll("-----BEGIN[^-]+-----", "")
                    .replaceAll("-----END[^-]+-----", "")
                    .replaceAll("[^A-Za-z0-9+/=]", "");
            byte[] keyBytes = Base64.getDecoder().decode(key);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) keyFactory.generatePublic(spec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("erro ao carregarPublicKey: " + e.getMessage(), e);
        }
    }
}
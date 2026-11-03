package br.com.hackathon.setic.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RsaKeyServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void loadsConfiguredPkcs8AndX509PemKeys() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair pair = generator.generateKeyPair();

        Path privateKeyPath = tempDir.resolve("private_key.pem");
        Path publicKeyPath = tempDir.resolve("public_key.pem");
        Files.writeString(privateKeyPath, pem("PRIVATE KEY", pair.getPrivate().getEncoded()));
        Files.writeString(publicKeyPath, pem("PUBLIC KEY", pair.getPublic().getEncoded()));

        RsaKeyService service = new RsaKeyService(
                privateKeyPath.toString(),
                publicKeyPath.toString());
        RSAPrivateKey privateKey = service.carregarPrivateKey();
        RSAPublicKey publicKey = service.carregarPublicKey();

        assertEquals(publicKey.getModulus(), privateKey.getModulus());
    }

    private String pem(String label, byte[] encodedKey) {
        return "-----BEGIN " + label + "-----\n"
                + Base64.getEncoder().encodeToString(encodedKey)
                + "\n-----END " + label + "-----\n";
    }
}

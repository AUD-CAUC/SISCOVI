package br.jus.stj.siscovi.helpers;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import javax.xml.bind.DatatypeConverter;
import br.jus.stj.siscovi.model.APIKey;
import io.jsonwebtoken.*;
import java.util.Date;

public class Token {

    public String tokenGen(String id, String issuer, String subject, long ttlMillis){
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;
        long nowMillis = System.currentTimeMillis();
        APIKey apiKey = new APIKey();
        Date now = new Date(nowMillis);

        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(apiKey.getSecret());
        Key signinKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        JwtBuilder builder = Jwts.builder().setId(id)
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(issuer)
                .signWith(signatureAlgorithm, signinKey);
        if(ttlMillis >= 0){
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }
        return builder.compact();
    }
}
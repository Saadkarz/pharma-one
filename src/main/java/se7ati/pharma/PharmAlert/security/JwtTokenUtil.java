package se7ati.pharma.PharmAlert.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import se7ati.pharma.PharmAlert.Bean.Utilisateur;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

    // Clé secrète générée sécurisée (256 bits minimum)
    private final Key jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    
    @Value("${jwt.expiration:86400000}") // Par défaut 24h (en millisecondes)
    private long jwtExpiration;

    /**
     * Génère un token JWT pour un utilisateur
     * @param utilisateur L'utilisateur pour lequel générer le token
     * @return Le token JWT généré
     */
    public String generateToken(Utilisateur utilisateur) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", utilisateur.getId());
        claims.put("role", utilisateur.getRole().name());
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(utilisateur.getIdentifiant())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(jwtSecret)
                .compact();
    }


    public boolean validateToken(String token, Utilisateur utilisateur) {
        final String username = extractUsername(token);
        return (username.equals(utilisateur.getIdentifiant()) && !isTokenExpired(token));
    }

    /**
     * Extrait le nom d'utilisateur du token JWT
     * @param token Le token JWT
     * @return Le nom d'utilisateur
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrait la date d'expiration du token JWT
     * @param token Le token JWT
     * @return La date d'expiration
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrait une information spécifique du token JWT
     * @param token Le token JWT
     * @param claimsResolver La fonction pour extraire l'information
     * @return L'information extraite
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrait toutes les informations du token JWT
     * @param token Le token JWT
     * @return Les informations extraites
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Vérifie si le token JWT est expiré
     * @param token Le token JWT
     * @return true si le token est expiré, false sinon
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}
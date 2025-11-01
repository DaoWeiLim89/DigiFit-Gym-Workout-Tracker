@Service
public class JwtService {

    private static final String SECRET_KEY = "super-secret-key-change-this";

    public String extractUsername(String token) {
        return Jwts.parser()
                   .setSigningKey(SECRET_KEY)
                   .parseClaimsJws(token)
                   .getBody()
                   .getSubject();
    }

    public boolean isTokenValid(String token, String username) {
        return username.equals(extractUsername(token)) && !isExpired(token);
    }

    private boolean isExpired(String token) {
        Date exp = Jwts.parser()
                       .setSigningKey(SECRET_KEY)
                       .parseClaimsJws(token)
                       .getBody()
                       .getExpiration();
        return exp.before(new Date());
    }

    public String generateToken(String username) {
        return Jwts.builder()
                   .setSubject(username)
                   .setIssuedAt(new Date())
                   .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
                   .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                   .compact();
    }
}


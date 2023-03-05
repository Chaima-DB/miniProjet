package ma.dbibih.miniprojet.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtUtil {

    public static final String SECRET_KEY = "KEYPASSWORD";
    public static final String ROLES_LABEL = "roles";
    public static final String HEADER_TOKEN_SUFFIX = "Bearer ";
    public static final String HTTP_AUTORISATION_HEADER = "Authorization";
    public static final String EMPTY_STRING = "";
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10h


    public static void generateTokenInHttpHeader(AppUserDetails userDetails, HttpServletResponse response) {
        String jwtToken = HEADER_TOKEN_SUFFIX + generateToken(userDetails);
        System.out.println("jwtToken == " + jwtToken);
        response.addHeader(HTTP_AUTORISATION_HEADER, jwtToken);
    }

    public static void generateTokenInResponseBody(AppUserDetails userDetails, HttpServletResponse response) throws IOException {
        Map<String, String> res = new HashMap<>();
        res.put("access-token", generateToken(userDetails));
        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getOutputStream(), res);
    }

    public static String generateToken(AppUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<String, Object>();
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        claims.put(ROLES_LABEL, roles);
        claims.put("email", userDetails.getUser().getEmail());
        long issuedAt = System.currentTimeMillis();
        return Jwts.builder().setClaims(claims).setSubject(userDetails.getUsername()).setIssuedAt(new Date(issuedAt)).setIssuer("localhost:9090")
                .setExpiration(new Date(issuedAt + EXPIRATION_TIME)).signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public static Claims getAllClaimsGeneratedToken(String token) {
        return getAllClaims(token.replace(HEADER_TOKEN_SUFFIX, EMPTY_STRING));
    }

    public static boolean valiateToken(String token, UserDetails userDetails) {
        return getUserName(token).equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public static boolean validateToken(HttpServletRequest request) {
        String token = getToken(request);
        return (token != null && token.startsWith(HEADER_TOKEN_SUFFIX));
    }

    public static String getToken(HttpServletRequest request) {
        return request.getHeader(HTTP_AUTORISATION_HEADER);
    }

    public static Date getExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    public static Boolean isTokenExpired(String token) {
        return getExpiration(token).after(new Date());
    }

    public static String getUserName(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public static List<String> getRoles(Claims claims) {
        return (List<String>) claims.get(ROLES_LABEL);
    }

    private static <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = getAllClaims(token);

        return claimsResolver.apply(claims);
    }

    private static Claims getAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }
}
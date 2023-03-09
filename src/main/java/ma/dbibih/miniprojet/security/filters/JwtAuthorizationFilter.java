package ma.dbibih.miniprojet.security.filters;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.MalformedJwtException;
import ma.dbibih.miniprojet.utilities.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        initResponseHeader(response);
        if (JwtUtil.validateToken(request)) {
            try {
                String token = JwtUtil.getToken(request);
                Claims claims = JwtUtil.getAllClaimsGeneratedToken(token);
                String username = claims.getSubject();
                List<String> roles = JwtUtil.getRoles(claims);
                Collection<GrantedAuthority> authorities = new ArrayList<>();
                roles.forEach(r -> authorities.add(new SimpleGrantedAuthority(r)));
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                        = new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            } catch (MalformedJwtException e) {
                throw new RuntimeException("probleme de signature de token");
            }
        }
        filterChain.doFilter(request, response);
    }


    private void initResponseHeader(HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Headers",
                "Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, "
                        + "Access-Control-Request-Headers, authorization");

        response.addHeader("Access-Control-Expose-Headers", "Access-Control-Allow-Origin, "
                + "Access-Control-Allow-Credentials,authorization");

    }
}
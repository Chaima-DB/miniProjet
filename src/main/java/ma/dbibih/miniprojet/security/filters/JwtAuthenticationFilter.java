package ma.dbibih.miniprojet.security.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.dbibih.miniprojet.entity.LoginDTO;
import ma.dbibih.miniprojet.utilities.AppUserDetails;
import ma.dbibih.miniprojet.utilities.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super();
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        LoginDTO user = null;
        try {
            user = new ObjectMapper().readValue(request.getInputStream(), LoginDTO.class);
        } catch (IOException e) {
            System.out.println("request mal formed");
        }
        assert user != null;
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
    }


    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException {
        AppUserDetails user = (AppUserDetails) authResult.getPrincipal();
        SecurityContextHolder.getContext().setAuthentication(authResult);
        JwtUtil.generateTokenInResponseBody(user, response);
    }


}

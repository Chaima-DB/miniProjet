package ma.dbibih.miniprojet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import ma.dbibih.miniprojet.entity.LoginDTO;
import ma.dbibih.miniprojet.serviceImpl.UserDetailsServiceImpl;
import ma.dbibih.miniprojet.utilities.AppUserDetails;
import ma.dbibih.miniprojet.utilities.JwtResponse;
import ma.dbibih.miniprojet.utilities.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@CrossOrigin("*")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    @Operation(summary = "Authenticate users")
    @PostMapping("/")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User Authenticated! the Token was generated successfully"),
            @ApiResponse(responseCode = "403", description = "The given credentials are not correct"),
            @ApiResponse(responseCode = "500", description = "Internal server error occurred")
    })
    public ResponseEntity<JwtResponse> auth(@RequestBody LoginDTO user) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        AppUserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(user.getUsername());
        String jwt = JwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(jwt));
    }

}

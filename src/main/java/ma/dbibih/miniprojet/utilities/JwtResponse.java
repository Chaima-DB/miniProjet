package ma.dbibih.miniprojet.utilities;

import lombok.Data;

@Data
public class JwtResponse {
    private String accessToken;

    public JwtResponse(String token) {
        super();
        this.accessToken = token;
    }

}
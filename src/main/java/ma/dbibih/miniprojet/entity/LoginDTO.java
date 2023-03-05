

package ma.dbibih.miniprojet.entity;

import lombok.*;


@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class LoginDTO {

    private String username;
    private String password;

}

package ma.dbibih.miniprojet.entity;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor

public class UserDTO {
    private String firstName ;
    private String lastName;
    private Date birthDate;
    private String city;
    private String country;
    private String avatar;
    private String company;
    private String jobPosition;
    private String mobile;
    private String userName;
    private String email;
    private String password;
    private String role;
}

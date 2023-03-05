package ma.dbibih.miniprojet.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "app_users")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "first_name")
    private String firstName ;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "birth_date")
    private Date birthDate;
    @Column(name = "city")
    private String city;
    @Column(name = "country")
    private String country;
    @Column(name = "avatar")
    private String avatar;
    @Column(name = "company")
    private String company;
    @Column(name = "job_position")
    private String jobPosition;
    @Column(name = "mobile")
    private String mobile;

    @NotNull
    @NotEmpty
    @Column(name = "user_name")
    private String userName;

    @NotNull
    @NotEmpty
    @Column(name = "email")
    private String email;

    @NotNull
    @NotEmpty
    @Column(name = "password")
    private String password;

    @NotNull
    @NotEmpty
    @Column(name = "role")
    private String role;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

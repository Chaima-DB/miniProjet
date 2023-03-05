package ma.dbibih.miniprojet.utilities;

import lombok.Getter;
import ma.dbibih.miniprojet.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
public class AppUserDetails implements UserDetails {
    private final User user;

    public AppUserDetails(User user) {
        this.user = user;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new AppGrantedAuthority(user));
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

class AppGrantedAuthority implements GrantedAuthority {
    private final User user;

    AppGrantedAuthority(User user) {
        this.user = user;
    }


    @Override
    public String getAuthority() {
        return user.getRole();
    }
}
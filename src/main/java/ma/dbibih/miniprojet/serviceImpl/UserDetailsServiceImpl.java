package ma.dbibih.miniprojet.serviceImpl;

import lombok.RequiredArgsConstructor;
import ma.dbibih.miniprojet.entity.User;
import ma.dbibih.miniprojet.service.UserService;
import ma.dbibih.miniprojet.utilities.AppUserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    @Override
    public AppUserDetails loadUserByUsername(String username) {
        try {
            User foundedUser = userService.getUserProfile(username);
            return new AppUserDetails(foundedUser);
        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException("User not Found!");
        }

    }
}

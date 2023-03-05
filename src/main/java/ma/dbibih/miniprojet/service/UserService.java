package ma.dbibih.miniprojet.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import ma.dbibih.miniprojet.entity.User;
import ma.dbibih.miniprojet.entity.UserDTO;
import ma.dbibih.miniprojet.utilities.BatchResponse;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> getById (Long id);
    BatchResponse createUsers (List<User> users);
    User createUser (User user);
    List<User> getUsers();
    List<User> getRecentUsers(int count);
    List<UserDTO> generateRandomUsers(int count);
    User getUserProfile(String username);
    List<UserDTO> generateJsonUsersFile(int count);
    User findByUserName(String name);
    User findByEmail(String email);
    Boolean existsByUserName(String username);
    Boolean existsByEmail(String email);
    UserDetails userDetailsFromUser(User user);
}

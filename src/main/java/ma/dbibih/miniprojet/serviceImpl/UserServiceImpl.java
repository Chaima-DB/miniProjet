package ma.dbibih.miniprojet.serviceImpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import ma.dbibih.miniprojet.entity.User;
import ma.dbibih.miniprojet.entity.UserDTO;
import ma.dbibih.miniprojet.repository.UserRepository;
import ma.dbibih.miniprojet.service.UserService;
import ma.dbibih.miniprojet.utilities.BatchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User createUser(User user) {
        if (!this.existsByEmail(user.getEmail()) && !this.existsByUserName(user.getUserName())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(user);
        } else return null;
    }

    @Override
    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public BatchResponse createUsers(List<User> users) {
        BatchResponse response = new BatchResponse(0,0);
        users.forEach(user -> {
            if (!this.existsByEmail(user.getEmail()) && !this.existsByUserName(user.getUserName())) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userRepository.save(user);
                response.setSavedUsers(response.getSavedUsers() + 1);
            }
        });
        response.setNonSavedUsers((users.size() - response.getSavedUsers()));
        return response;
    }

    public String initUsers(MultipartFile file) {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<User>> typeReference = new TypeReference<List<User>>() {
        };
        InputStream inputStream = TypeReference.class.getResourceAsStream(file.getName());
        try {
            List<User> users = mapper.readValue(inputStream, typeReference);
            userRepository.saveAll(users);
            System.out.println("Users Saved!");
        } catch (IOException e) {
            System.out.println("Unable to save users: " + e.getMessage());
        }
        return "saved with success";
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getRecentUsers(int count) {
        return userRepository.findRecentUsers(count);
    }

    public String randomPassword() {
        //random caracter
        int leftLimit = 33; // !
        int rightLimit = 126; // ~
        int targetStringLength = new Random().nextInt(4) + 6;
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int j = 0; j < targetStringLength; j++) {
            int randomLimitedInt = leftLimit + (int)
                    (new Random().nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

    public String randomRole() {
        final String[] randomRole = {"ADMIN", "USER"};
        Random random = new Random();
        int index = random.nextInt(randomRole.length);
        return randomRole[index];
    }

    @Override
    public List<UserDTO> generateRandomUsers(int count) {
        List<UserDTO> randomUsers = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Faker fake = new Faker(new Locale("fr"));
            UserDTO user = new UserDTO();
            user.setFirstName(fake.name().firstName());
            user.setLastName(fake.name().lastName());
            user.setUserName(fake.name().username());
            user.setEmail(fake.internet().emailAddress());
            user.setAvatar(fake.avatar().image());
            user.setCity(fake.address().city());
            user.setBirthDate(fake.date().birthday());
            user.setCompany(fake.company().name());
            user.setCountry(fake.address().country());
            user.setJobPosition(fake.job().position());
            user.setPassword(this.randomPassword());
            user.setMobile(fake.phoneNumber().cellPhone());
            user.setRole(this.randomRole());
            randomUsers.add(user);
        }
        return randomUsers;
    }

    @Override
    public List<UserDTO> generateJsonUsersFile(int count) {
        return this.generateRandomUsers(count);
    }

    @Override
    public User findByUserName(String name) {
        return userRepository.findByUserName(name);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Boolean existsByUserName(String username) {
        return userRepository.existsByUserName(username);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User getUserProfile(String username) {
        User foundedByUserName = this.userRepository.findByUserName(username);
        if (foundedByUserName != null) {
            return foundedByUserName;
        } else {
            User foundedByEmail = this.userRepository.findByEmail(username);
            if (foundedByEmail != null) {
                return foundedByEmail;
            } else throw new UsernameNotFoundException(username);
        }

    }

    public UserDetails userDetailsFromUser(User user) {
        return new org.springframework.security.core.userdetails.
                User(user.getUserName(), user.getPassword(), Collections.singletonList(new SimpleGrantedAuthority(user.getRole())));
    }

}

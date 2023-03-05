package ma.dbibih.miniprojet.service;


import ma.dbibih.miniprojet.entity.User;
import ma.dbibih.miniprojet.entity.UserDTO;
import ma.dbibih.miniprojet.repository.UserRepository;
import ma.dbibih.miniprojet.serviceImpl.UserServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).userName("test.test").email("test@test.com").password("test")
                .firstName("test").lastName("test").avatar("test").country("test").role("test").city("test").jobPosition("test").build();

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void UserService_CreateUser_ReturnUser() {

        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        User saveUser = userService.createUser(user);
        Assertions.assertThat(saveUser).isNotNull();
        Assertions.assertThat(saveUser).isEqualTo(user);

    }

    @Test
    public void UserService_CreateRandomUsers_ReturnRandomUsers() {
        int count = 10;
        List<UserDTO> randomUsers = userService.generateRandomUsers(count);
        Assertions.assertThat(randomUsers).isNotNull();
        Assertions.assertThat(randomUsers.size()).isEqualTo(count);
    }

    @Test
    public void UserService_FindUserById_ReturnUser() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(java.util.Optional.ofNullable(user));
        Optional<User> foundedUser = userService.getById(Mockito.anyLong());
        Assertions.assertThat(foundedUser).isNotNull();
        Assertions.assertThat(foundedUser.get().getUserName()).isEqualTo(user.getUserName());
    }

    @Test
    public void UserService_FindUserById_ReturnNull() {
        when(userRepository.findById(Mockito.anyLong())).thenReturn(null);
        Optional<User> foundedUser = userService.getById(Mockito.anyLong());
        Assertions.assertThat(foundedUser).isNull();
    }


}

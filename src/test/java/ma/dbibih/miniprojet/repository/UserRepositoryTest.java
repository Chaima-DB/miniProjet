package ma.dbibih.miniprojet.repository;

import ma.dbibih.miniprojet.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void UserRepository_Save_ReturnSavedUser() {
        User user = User.builder()
                .userName("test.test")
                .email("test@test.com")
                .password("test")
                .firstName("test")
                .lastName("test")
                .avatar("test")
                .country("test")
                .role("test")
                .city("test")
                .jobPosition("test")
                .build();

        User savedUser = userRepository.save(user);

        Assertions.assertThat(savedUser).isNotNull();
        Assertions.assertThat(savedUser.getId()).isNotNull();
        Assertions.assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void UserRepository_FindAll_ReturnListUser() {

        List<User> users = userRepository.findAll();
        Assertions.assertThat(users).isNotNull();
        Assertions.assertThat(users).isEmpty();

        User user1 = User.builder().userName("test.test").email("test@test.com").password("test").firstName("test")
                .lastName("test").avatar("test").country("test").role("test").city("test").jobPosition("test").build();
        User user2 = User.builder().userName("test.test").email("test@test.com").password("test").firstName("test")
                .lastName("test").avatar("test").country("test").role("test").city("test").jobPosition("test").build();

        userRepository.save(user1);
        userRepository.save(user2);

        users = userRepository.findAll();
        Assertions.assertThat(users).isNotNull();
        Assertions.assertThat(users).isNotEmpty();
        Assertions.assertThat(users.size()).isEqualTo(2);
    }

    @Test
    public void UserRepository_FindByUserName_ReturnUser() {
        String userName = "test.test";
        User user = userRepository.findByUserName(userName);
        Assertions.assertThat(user).isNull();

        User user1 = User.builder().userName("test.test").email("test@test.com").password("test").firstName("test")
                .lastName("test").avatar("test").country("test").role("test").city("test").jobPosition("test").build();


        userRepository.save(user1);

        user = userRepository.findByUserName(userName);
        Assertions.assertThat(user).isNotNull();
        Assertions.assertThat(user.getId()).isGreaterThan(0);
        Assertions.assertThat(user.getUserName()).isEqualTo(userName);
    }

    @Test
    public void UserRepository_ExistByUserName_ReturnBool() {
        String userName = "test.test";
        boolean user = userRepository.existsByUserName(userName);

        Assertions.assertThat(user).isNotNull();
        Assertions.assertThat(user).isFalse();

        User user1 = User.builder().userName("test.test").email("test@test.com").password("test").firstName("test")
                .lastName("test").avatar("test").country("test").role("test").city("test").jobPosition("test").build();


        userRepository.save(user1);

        user = userRepository.existsByUserName(userName);
        Assertions.assertThat(user).isNotNull();
        Assertions.assertThat(user).isTrue();
    }

    @Test
    public void UserRepository_FindById_ReturnUser() {

        User user1 = User.builder().userName("test.test").email("test@test.com").password("test").firstName("test")
                .lastName("test").avatar("test").country("test").role("test").city("test").jobPosition("test").build();

        User savedUser = userRepository.save(user1);

        Optional<User> user = userRepository.findById(savedUser.getId());
        Assertions.assertThat(user).isNotNull();
        Assertions.assertThat(user.isPresent()).isTrue();
        Assertions.assertThat(user.get().getId()).isGreaterThan(0);
        Assertions.assertThat(user.get().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    public void UserRepository_FindRecentUsers_ReturnListUser(){
        int count = 2;
        User user1 = User.builder().userName("test.test").email("test@test.com").password("test").firstName("test")
                .lastName("lastname1").avatar("test").country("test").role("test").city("test").jobPosition("test").build();
        User user2 = User.builder().userName("test.test").email("test@test.com").password("test").firstName("test")
                .lastName("lastname2").avatar("test").country("test").role("test").city("test").jobPosition("test").build();
        User user3 = User.builder().userName("test.test").email("test@test.com").password("test").firstName("test")
                .lastName("lastname3").avatar("test").country("test").role("test").city("test").jobPosition("test").build();
        User user4 = User.builder().userName("test.test").email("test@test.com").password("test").firstName("test")
                .lastName("lastname4").avatar("test").country("test").role("test").city("test").jobPosition("test").build();
        User user5 = User.builder().userName("test.test").email("test@test.com").password("test").firstName("test")
                .lastName("lastname5").avatar("test").country("test").role("test").city("test").jobPosition("test").build();
        User user6 = User.builder().userName("test.test").email("test@test.com").password("test").firstName("test")
                .lastName("lastname6").avatar("test").country("test").role("test").city("test").jobPosition("test").build();

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        userRepository.save(user5);
        userRepository.save(user6);

        List<User> recentUsers =userRepository.findRecentUsers(count);

        Assertions.assertThat(recentUsers).isNotEmpty();
        Assertions.assertThat(recentUsers.size()).isEqualTo(2);
        Assertions.assertThat(recentUsers.contains(user6)).isTrue();
        Assertions.assertThat(recentUsers.contains(user5)).isTrue();
        Assertions.assertThat(recentUsers.contains(user4)).isFalse();

    }

    @Test
    public void UserRepository_findByEmail_returnUser(){
        String email = "test@test.com";
        User user1 = User.builder().userName("test.test").email("test@test.com").password("test").firstName("test")
                .lastName("lastname1").avatar("test").country("test").role("test").city("test").jobPosition("test").build();
         User user2 = User.builder().userName("test2.test2").email("test2@test.com").password("test").firstName("test")
                .lastName("lastname1").avatar("test").country("test").role("test").city("test").jobPosition("test").build();

        userRepository.save(user2);

        User usernotfound = userRepository.findByEmail(email);

        Assertions.assertThat(usernotfound).isNull();
        userRepository.save(user1);

        User userfound = userRepository.findByEmail(email);

        Assertions.assertThat(userfound).isNotNull();
        Assertions.assertThat(userfound.getEmail()).isEqualTo(email);


    }
}

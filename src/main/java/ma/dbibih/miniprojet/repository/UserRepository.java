package ma.dbibih.miniprojet.repository;

import ma.dbibih.miniprojet.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT TOP :count * FROM APP_USERS ORDER BY id DESC", nativeQuery = true)
    List<User> findRecentUsers(@Param("count") int count);
    User findByUserName(String name);
    Boolean existsByUserName(String username);
    Boolean existsByEmail(String email);
    User findByEmail(String email);
}

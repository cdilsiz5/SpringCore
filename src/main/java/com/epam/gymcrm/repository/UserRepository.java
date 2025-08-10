package com.epam.gymcrm.repository;

import com.epam.gymcrm.model.User;
import com.epam.gymcrm.model.enums.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<User,Long> {
     Optional<User> findByUsername(String username);
     boolean existsByUsername(String username);
     @Query("select count(u) from User u where u.userActive = true")
     long countActiveUsers();


}

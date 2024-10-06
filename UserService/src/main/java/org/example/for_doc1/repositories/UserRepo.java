package org.example.for_doc1.repositories;

import org.example.for_doc1.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    User save(User user);

    Optional<User> findByEmail(String Email);

}

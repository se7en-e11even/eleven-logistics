package com.eleven.logistics.auth.domain.repository;

import com.eleven.logistics.auth.domain.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

// Interface 계층에 존재하는 JpaRepository 를 분리하기 위한 Interface
@Repository
public interface UserRepository {
    List<User> findAll();

    User save(User user);

    Optional<User> findByUsername(String username);
}
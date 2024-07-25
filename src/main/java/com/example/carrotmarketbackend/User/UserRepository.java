package com.example.carrotmarketbackend.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email); // 이메일로 조회
    Optional<User> findByUsername(String username); // 사용자 이름으로 조회

}

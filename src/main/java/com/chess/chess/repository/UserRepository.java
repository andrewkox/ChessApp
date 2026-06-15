package com.chess.chess.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.chess.chess.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
     User findByUsername(String username);

}

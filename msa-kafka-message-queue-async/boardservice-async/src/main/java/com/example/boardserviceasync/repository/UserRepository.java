package com.example.boardserviceasync.repository;

import com.example.boardserviceasync.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}

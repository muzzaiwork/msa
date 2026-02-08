package com.example.userserviceasync.repository;

import com.example.userserviceasync.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}

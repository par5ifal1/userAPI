package com.example.userAPI.repository;

import com.example.userAPI.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface UserRepository extends JpaRepository<Users, Long> {
}

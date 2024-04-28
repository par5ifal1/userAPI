package com.example.userAPI;

import com.example.userAPI.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestH2Repository extends JpaRepository<Users, Long> {
}

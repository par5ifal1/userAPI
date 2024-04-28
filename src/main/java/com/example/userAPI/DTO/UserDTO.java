package com.example.userAPI.DTO;

import java.time.LocalDate;

public record UserDTO (
        String email,
        String firstName,
        String lastName,
        LocalDate birthDate,
        String address,
        String phoneNumber
){
}

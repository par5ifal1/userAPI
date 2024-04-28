package com.example.userAPI.DTOMappers;

import com.example.userAPI.DTO.UserDTO;
import com.example.userAPI.models.Users;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class UserDTOMapper implements Function<Users, UserDTO> {

    @Override
    public UserDTO apply(Users users) {
        return new UserDTO(
                users.getEmail(),
                users.getFirstName(),
                users.getLastName(),
                users.getBirthDate(),
                users.getAddress(),
                users.getPhoneNumber()
        );
    }
}

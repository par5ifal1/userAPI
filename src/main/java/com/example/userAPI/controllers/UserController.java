package com.example.userAPI.controllers;

import com.example.userAPI.DTO.UserDTO;
import com.example.userAPI.services.UserService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("/")
@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/newUser")
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid UserDTO userDTO, BindingResult bindingResult){
        if (!bindingResult.hasErrors()){
                UserDTO addedUserDTO = userService.addUser(userDTO);
                return new ResponseEntity<>(addedUserDTO, HttpStatus.CREATED);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/user/{id}/update")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id,
                                              @RequestBody @Valid UserDTO userDTO,
                                              BindingResult bindingResult){
        if (!bindingResult.hasErrors()) {
            UserDTO updatedUserDTO = userService.updateUser(id, userDTO);

            return new ResponseEntity<>(updatedUserDTO, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/user/{id}/delete")
    public ResponseEntity<UserDTO> deleteUser(@PathVariable Long id){
        userService.deleteUserById(id);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/searchUsers")
    public ResponseEntity<List<UserDTO>> findUsersByBirthDate(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate to) {

        List<UserDTO> users = userService.findUsersByBirthDate(from, to);

        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}

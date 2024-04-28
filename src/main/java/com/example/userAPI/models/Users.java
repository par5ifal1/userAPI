package com.example.userAPI.models;

import com.example.userAPI.DTO.UserDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.Optional;

@Entity
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty(message = "Email should not be empty")
    @Pattern(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$", message = "Email should be valid")
    @Column(unique = true)
    private String email;
    @NotBlank(message = "First name should not be empty")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "First name should consist only of letters")
    @Size(min = 2, max = 50, message = "First name should be in range 2 - 50")
    private String firstName;
    @NotBlank(message = "Last name should not be empty")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Last name should consist only of letters")
    @Size(min = 2, max = 50, message = "Last name should be in range 2 - 50")
    private String lastName;
    @NotNull(message = "BirthDate should not be empty")
    private LocalDate birthDate;

    private String address;
    private String phoneNumber;

    public Users(String email, String firstName, String lastName, LocalDate birthDate, String address, String phoneNumber) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public Users() {

    }

    public void resetFields(UserDTO userDTO){
        this.email = Optional.ofNullable(userDTO.email()).orElse(this.email);
        this.firstName = Optional.ofNullable(userDTO.firstName()).orElse(this.firstName);
        this.lastName = Optional.ofNullable(userDTO.lastName()).orElse(this.lastName);
        this.birthDate = Optional.ofNullable(userDTO.birthDate()).orElse(this.birthDate);
        this.address = Optional.ofNullable(userDTO.address()).orElse(this.address);
        this.phoneNumber = Optional.ofNullable(userDTO.phoneNumber()).orElse(this.phoneNumber);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

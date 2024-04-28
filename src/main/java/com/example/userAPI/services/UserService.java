package com.example.userAPI.services;

import com.example.userAPI.DTO.UserDTO;
import com.example.userAPI.DTOMappers.UserDTOMapper;
import com.example.userAPI.exeptions.UserNotExistException;
import com.example.userAPI.models.Users;
import com.example.userAPI.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserDTOMapper userDTOMapper;
    private final String ageLimit;


    public UserService(UserRepository userRepository, UserDTOMapper userDTOMapper, @Value("${age.Limit}") String ageLimit) {
        this.userRepository = userRepository;
        this.userDTOMapper = userDTOMapper;
        this.ageLimit = ageLimit;
    }

    @Transactional
    public List<UserDTO> getAllUsers(){
        return userRepository.findAll().stream().map(userDTOMapper).collect(Collectors.toList());
    }

    @Transactional
    public UserDTO addUser(UserDTO userDTO){
        validateUserAge(userDTO);
        Users user = new Users(userDTO.email(),
                userDTO.firstName(),
                userDTO.lastName(),
                userDTO.birthDate(),
                userDTO.address(),
                userDTO.phoneNumber()
        );

        return userDTOMapper.apply(userRepository.save(user));
    }

    @Transactional
    public List<UserDTO> findUsersByBirthDate(LocalDate from, LocalDate to){
        validateDateRange(from, to);

        return getAllUsers().stream()
                .filter(x -> x.birthDate().isAfter(from)
                        && x.birthDate().isBefore(to)).toList();
    }

    @Transactional
    public UserDTO updateUser(Long useId, UserDTO userDTO){
        if (userDTO.birthDate() != null)
            validateUserAge(userDTO);
        Users user = userRepository.findById(useId).orElseThrow(() -> new UserNotExistException("No such user found"));
        user.resetFields(userDTO);
        userRepository.save(user);
        return userDTOMapper.apply(user);
    }

    @Transactional
    public void deleteUserById(Long userId){
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
        } else {
            throw new UserNotExistException("No such user found");
        }
    }

    private void validateUserAge(UserDTO userDTO) {
        LocalDate birthDate = userDTO.birthDate();
        if (birthDate != null) {
            LocalDate now = LocalDate.now();
            int age = Period.between(birthDate, now).getYears();
            if (age < Integer.parseInt(ageLimit)) {
                throw new DateTimeException("Registration is only available for users over " + ageLimit + " years old");
            }
        }
    }

    private void validateDateRange(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new DateTimeException("Both 'from' and 'to' dates are required");
        }
        if (!from.isBefore(to) || !to.isBefore(LocalDate.now())) {
            throw new DateTimeException("Invalid date range: 'from' should be before 'to', " +
                    "and 'to' should be before the current date");
        }
    }
}

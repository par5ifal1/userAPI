package com.example.userAPI;

import com.example.userAPI.DTO.UserDTO;
import com.example.userAPI.models.Users;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.main.lazy-initialization=true",
        classes = {UserApiApplication.class})
public class UpdateUserTest {

    @LocalServerPort
    private int port;
    private String baseUrl = "http://localhost";
    private static RestTemplate restTemplate;

    @Autowired
    private TestH2Repository testH2Repository;

    @BeforeAll
    public static void init(){
        restTemplate = new RestTemplate();
    }

    @BeforeEach
    public void setBaseUrl(){
        baseUrl = baseUrl + ":" + port + "/";
    }

    @AfterEach
    public void tearDown() {
        testH2Repository.deleteAll();
    }


    @ParameterizedTest
    @MethodSource("correctUsersTest")
    public void testUpdateExistedUsersWithCorrectData(UserDTO userForUpdate) {
        UserDTO user = new UserDTO("test@test.com", "First", "Last",
                LocalDate.parse("2000-01-01"), "Test Address", "1234567890");

        assertNotNull(restTemplate.postForObject(baseUrl + "newUser", user, UserDTO.class));

        Long userId = testH2Repository.findAll().get(0).getId();
        HttpEntity<UserDTO> request = new HttpEntity<>(userForUpdate);

        ResponseEntity<UserDTO> response = restTemplate.exchange(baseUrl + "user/" + userId + "/update",
                HttpMethod.PUT, request, UserDTO.class);

        Users updatedUser = testH2Repository.findAll().get(0);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(Optional.ofNullable(userForUpdate.email()).orElse(user.email()), updatedUser.getEmail());
        assertEquals(Optional.ofNullable(userForUpdate.firstName()).orElse(user.firstName()), updatedUser.getFirstName());
        assertEquals(Optional.ofNullable(userForUpdate.lastName()).orElse(user.lastName()), updatedUser.getLastName());
        assertEquals(Optional.ofNullable(userForUpdate.birthDate()).orElse(user.birthDate()), updatedUser.getBirthDate());
        assertEquals(1, testH2Repository.findAll().size());

        testH2Repository.deleteAll();
    }

    @ParameterizedTest
    @MethodSource("incorrectUsersTest")
    public void testUpdateExistedUsersWithIncorrectData(UserDTO userForUpdate){
        UserDTO user = new UserDTO("test@test.com", "First", "Last",
                LocalDate.parse("2000-01-01"), "Test Address", "1234567890");

        assertNotNull(restTemplate.postForObject(baseUrl + "newUser", user, UserDTO.class));

        Long userId = testH2Repository.findAll().get(0).getId();
        HttpEntity<UserDTO> request = new HttpEntity<>(userForUpdate);

        assertThrows(HttpClientErrorException.class,
                () -> restTemplate.exchange(baseUrl + "user/" + userId + "/update",
                        HttpMethod.PUT, request, UserDTO.class));

        assertEquals(1, testH2Repository.findAll().size());
    }

    @ParameterizedTest
    @MethodSource("correctUsersTest")
    public void testUpdateNotExistedUser(UserDTO userForUpdate){
        HttpEntity<UserDTO> request = new HttpEntity<>(userForUpdate);

        assertThrows(HttpClientErrorException.class,
                () -> restTemplate.exchange(baseUrl + "user/" + 1 + "/update",
                        HttpMethod.PUT, request, UserDTO.class));

        assertEquals(0, testH2Repository.findAll().size());
    }

    static Stream<UserDTO> correctUsersTest() {
        return Stream.of(
                new UserDTO(null, "First", "Last",
                        LocalDate.parse("2000-01-01"), null, "1234567890"),
                new UserDTO("testUpdated@test.com", null, "Last",
                        LocalDate.parse("2000-01-01"), "Test Address", "1234567890"),
                new UserDTO("testUpdated@test.com", null, null,
                        null, null, null),
                new UserDTO("testUpdated@test.com", "First", "Last",
                        LocalDate.parse("2005-01-01"), null, null)
        );
    }

    static Stream<UserDTO> incorrectUsersTest() {
        return Stream.of(
                new UserDTO(null, "Firs1242t", "Last",
                        LocalDate.parse("2000-01-01"), "Test Address", "1234567890"),
                new UserDTO("testUpdated@testcom", null, "Last",
                        LocalDate.parse("2000-01-01"), "Test Address", "1234567890"),
                new UserDTO("testUpdated@test.com", "First", null,
                        LocalDate.parse("2008-01-01"), null, null),
                new UserDTO("testUpdatedtest.com", "First", "L3as_..t",
                        null, null, null)
        );
    }
}

package com.example.userAPI;

import com.example.userAPI.DTO.UserDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.main.lazy-initialization=true",
        classes = {UserApiApplication.class})
public class DeleteUserTest {

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


    @Test
    public void testDeleteExistedUser() {
        UserDTO testUser = new UserDTO("test@test.com", "First", "Last",
                LocalDate.parse("2000-01-01"), "Test Address", "1234567890");
        restTemplate.postForObject(baseUrl + "newUser", testUser, UserDTO.class);

        assertEquals(1, testH2Repository.findAll().size());

        Long userId = testH2Repository.findAll().get(0).getId();
        HttpEntity<Long> request = new HttpEntity<>(userId);

        assertEquals(HttpStatus.NO_CONTENT,
        restTemplate.exchange(baseUrl + "user/" + userId + "/delete",
                HttpMethod.DELETE, request, UserDTO.class).getStatusCode());

        assertEquals(0, testH2Repository.findAll().size());
    }

    @Test
    public void testDeleteNotExistedUser() {
        assertEquals(0, testH2Repository.findAll().size());

        HttpEntity<Long> request = new HttpEntity<>(1L);

        assertThrows(HttpClientErrorException.BadRequest.class,
                () -> restTemplate.exchange(baseUrl + "user/" + 1L + "/delete",
                        HttpMethod.DELETE, request, UserDTO.class).getStatusCode());

        assertEquals(0, testH2Repository.findAll().size());
    }
}

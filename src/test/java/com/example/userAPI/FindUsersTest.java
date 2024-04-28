package com.example.userAPI;

import com.example.userAPI.DTO.UserDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.main.lazy-initialization=true",
        classes = {UserApiApplication.class})
public class FindUsersTest {

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

        List<UserDTO> testUsers = Arrays.asList(
                new UserDTO("test1@test.com", "First", "LastName",
                        LocalDate.parse("1999-01-01"), "Test Address1", "1234567890"),
                new UserDTO("test2@test.com", "Second", "LastName",
                        LocalDate.parse("2001-01-01"), "Test Address2", "1234567891"),
                new UserDTO("test3@test.com", "Third", "LastName",
                        LocalDate.parse("2002-01-01"), "Test Address3", "1234567892")
        );
        testUsers.forEach(user -> restTemplate.postForObject(baseUrl + "newUser", user, UserDTO.class));
    }

    @AfterEach
    public void tearDown() {
        testH2Repository.deleteAll();
    }

    @Test
    public void testFindUsersByDateRange() {
        assertEquals(3, testH2Repository.findAll().size());

        LocalDate from = LocalDate.of(2000, 1, 1);
        LocalDate to = LocalDate.of(2002, 12, 31);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("from", from.toString());
        queryParams.put("to", to.toString());

        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        ResponseEntity<UserDTO[]> responseEntity = restTemplate.exchange(
                baseUrl + "searchUsers?from={from}&to={to}",
                HttpMethod.GET,
                requestEntity,
                UserDTO[].class,
                queryParams
        );

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(2, Objects.requireNonNull(responseEntity.getBody()).length);
        assertEquals("test2@test.com", responseEntity.getBody()[0].email());
        assertEquals("test3@test.com", responseEntity.getBody()[1].email());
    }

    @ParameterizedTest
    @CsvSource({
            "2000-12-31, 2000-01-01",
            "2022-10-01, 2022-09-01",
            "2032-01-01, 2021-12-31",
            "2002-01-01, 2001-01-01"
    })    void testFindUserByBirthDateWithInvalidRange(String fromDate, String toDate) {
        LocalDate from = LocalDate.parse(fromDate);
        LocalDate to = LocalDate.parse(toDate);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("from", from.toString());
        queryParams.put("to", to.toString());

        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        assertThrows(HttpClientErrorException.BadRequest.class, () ->
                restTemplate.exchange(
                        baseUrl + "searchUsers?from={from}&to={to}",
                        HttpMethod.GET,
                        requestEntity,
                        UserDTO[].class,
                        queryParams
                )
        );
    }
}

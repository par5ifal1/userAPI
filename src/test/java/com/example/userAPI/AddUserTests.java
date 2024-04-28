package com.example.userAPI;

import com.example.userAPI.DTO.UserDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.main.lazy-initialization=true",
        classes = {UserApiApplication.class})
public class AddUserTests {

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
    public void testAddNewUsersWithCorrectRequiredData(){
        UserDTO user = new UserDTO("test@test.com", "First", "Last",
                LocalDate.parse("2000-01-01"), null, null);
        UserDTO user2 = new UserDTO("test2@test.com", "First", "Last",
                LocalDate.parse("2000-01-01"), null, null);

        UserDTO response = restTemplate.postForObject(baseUrl + "newUser", user, UserDTO.class);

        assert response != null;

        assertEquals("test@test.com", response.email());
        assertEquals(1, testH2Repository.findAll().size());

        UserDTO response2 = restTemplate.postForObject(baseUrl + "newUser", user2, UserDTO.class);
        assert response2 != null;

        assertEquals("test2@test.com", response2.email());
        assertEquals(2, testH2Repository.findAll().size());
    }

    @Test
    public void testAddNewUsersWithCorrectAllData(){
        UserDTO user = new UserDTO("test@test.com", "First", "Last",
                LocalDate.parse("2000-01-01"), "Test address", "380-000-000-000");
        UserDTO user2 = new UserDTO("test2@test.com", "First", "Last",
                LocalDate.parse("2000-01-01"), "Test address", "380-000-000-000");

        UserDTO response = restTemplate.postForObject(baseUrl + "newUser", user, UserDTO.class);

        assert response != null;

        assertEquals("test@test.com", response.email());
        assertEquals(1, testH2Repository.findAll().size());

        UserDTO response2 = restTemplate.postForObject(baseUrl + "newUser", user2, UserDTO.class);
        assert response2 != null;

        assertEquals("test2@test.com", response2.email());
        assertEquals(2, testH2Repository.findAll().size());
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "", "123", "first1", "\t", "....", "!!!!"})
    public void testAddUserWithIncorrectFirstName(String firstName){
        UserDTO user = new UserDTO("test@test.com", firstName, "Last",
                LocalDate.parse("2000-01-01"), "Test Address", "1234567890");

        assertThrows(HttpClientErrorException.class,
                () -> restTemplate.postForObject(baseUrl + "newUser", user, UserDTO.class));
        assertEquals(0, testH2Repository.findAll().size());
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "", "123", "first1", "\t", "....", "!!!!"})
    public void testAddUserWithIncorrectLastName(String lastName){
        UserDTO user = new UserDTO("test@test.com", "First", lastName,
                LocalDate.parse("2000-01-01"), "Test Address", "1234567890");

        assertThrows(HttpClientErrorException.class,
                () -> restTemplate.postForObject(baseUrl + "newUser", user, UserDTO.class));
        assertEquals(0, testH2Repository.findAll().size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"2008-01-01", "2007-01-01", "2006-12-28"})
    public void testAddUserWithIncorrectBirthDate(String birthDate){
        UserDTO user = new UserDTO("test@test.com", "First", "Last",
                LocalDate.parse(birthDate), "Test Address", "1234567890");

        assertThrows(HttpClientErrorException.class,
                () -> restTemplate.postForObject(baseUrl + "newUser", user, UserDTO.class));
        assertEquals(0, testH2Repository.findAll().size());
    }

    @Test
    public void testAddUserWithTheSameEmail(){
        UserDTO user = new UserDTO("test@test.com", "First", "Last",
                LocalDate.parse("2000-01-01"), "Test Address", "1234567890");

        assertNotNull(restTemplate.postForObject(baseUrl + "newUser", user, UserDTO.class));

        assertThrows(HttpClientErrorException.class,
                () -> restTemplate.postForObject(baseUrl + "newUser", user, UserDTO.class));

        assertEquals(1, testH2Repository.findAll().size());
    }

    @ParameterizedTest
    @MethodSource("testIncorrectEmails")
    public void testAddUserWithIncorrectEmail(String email){
        UserDTO user = new UserDTO(email, "First", "Last",
                LocalDate.parse("2000-01-01"), "Test Address", "1234567890");

        assertThrows(HttpClientErrorException.class,
                () -> restTemplate.postForObject(baseUrl + "newUser", user, UserDTO.class));
        assertEquals(0, testH2Repository.findAll().size());
    }

    static Stream<String> testIncorrectEmails() {
        return Stream.of(    "invalidemail@", "@missingdomain.com", "noat.symbol.com", "missingdomain@.com",
                "incomplete@domain.", "spaces notallowed@example.com", "specialcharacters$%&@example.com",
                "missingusername@.org", "double..dots@example.com", "domainmissing@.com"
        );
    }
}

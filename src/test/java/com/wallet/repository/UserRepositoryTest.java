package com.wallet.repository;

import com.wallet.entity.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserRepositoryTest {
    private static final String EMAIL = "email@teste.com";
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        User user = new User();
        user.setName("User1");
        user.setPassword("12345");
        user.setEmail(EMAIL);

        userRepository.save(user);
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void testSave() {
        User user = new User();
        user.setName("Teste");
        user.setPassword("123456");
        user.setEmail("user@test.com");

        User userResponse = userRepository.save(user);

        assertNotNull(userResponse);
    }

    @Test
    public void testFindByEmail() {
        Optional<User> userResponse = userRepository.findByEmailEquals(EMAIL);
        assertTrue(userResponse.isPresent());
        assertEquals(userResponse.get().getEmail(), EMAIL);
    }
}

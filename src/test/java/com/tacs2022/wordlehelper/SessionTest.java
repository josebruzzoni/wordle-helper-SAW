package com.tacs2022.wordlehelper;

import com.tacs2022.wordlehelper.domain.user.User;
import com.tacs2022.wordlehelper.exceptions.ExistingUserException;
import com.tacs2022.wordlehelper.exceptions.ForbiddenException;
import com.tacs2022.wordlehelper.repos.UserRepository;
import com.tacs2022.wordlehelper.service.SecurityService;
import com.tacs2022.wordlehelper.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class SessionTest {
    @MockBean
    UserRepository userRepoMock;

    @Autowired
    UserService userService;
    @Autowired
    SecurityService securityService;

    User julian;

    @BeforeEach
    public void fixture() {
        SecurityService ss = new SecurityService();
        byte[] salt = ss.getSalt();

        julian = new User("Julian", ss.hash("1234", salt), salt);

    }

    @Test
    public void userPasswordIsIncorrect() {
        Mockito.when(userRepoMock.findByUsername("Julian")).thenReturn(List.of(julian));
        String pass = "sarasa";
        assertFalse(securityService.validatePassword("Julian", pass));
    }

    @Test
    public void userPasswordIsCorrect() {
        Mockito.when(userRepoMock.findByUsername("Julian")).thenReturn(List.of(julian));
        String pass = "1234";
        assertTrue(securityService.validatePassword("Julian", pass));
    }

    @Test
    public void userAlreadyExists() {
        Mockito.when(userRepoMock.findByUsername("Julian")).thenReturn(List.of(julian));
        Assertions
                .assertThatThrownBy ( () -> { userService.save("Julian", "123456"); } )
                .isInstanceOf(ExistingUserException.class);
    }
}
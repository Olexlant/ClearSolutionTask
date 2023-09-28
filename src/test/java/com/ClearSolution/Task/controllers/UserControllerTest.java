package com.ClearSolution.Task.controllers;

import com.ClearSolution.Task.entities.User;
import com.ClearSolution.Task.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    private UserController userController;

    @Mock
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userController = new UserController(userService);
        userController.userService = userService; // Manually inject the mock UserService
    }

    @Test
    public void testCreateUserValidAge() {
        User newUser = new User();
        newUser.setId(1L);
        newUser.setEmail("lantuh.sasha@gmail.com");
        newUser.setFirstName("Olex");
        newUser.setLastName("Biden");
        newUser.setBirthDate(LocalDate.now().minusYears(21)); // 21 years old

        when(userService.createUser(any(User.class))).thenReturn(ResponseEntity.ok(newUser));

        ResponseEntity<User> response = userController.createUser(newUser);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(newUser, response.getBody());
        assertNotNull(response.getBody());
    }

    @Test
    public void testCreateUserInvalidAge() {
        User newUser = new User();
        newUser.setId(1L);
        newUser.setEmail("lantuh.sasha@gmail.com");
        newUser.setFirstName("Olex");
        newUser.setLastName("Biden");
        newUser.setBirthDate(LocalDate.now().minusYears(16)); // 16 years old

        when(userService.createUser(any(User.class))).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "User must be at least 18 years old"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userController.createUser(newUser));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("User must be at least 18 years old", exception.getReason());
    }

    @Test
    public void testUpdateUser() {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setEmail("Nolan228@gmail.com");
        updatedUser.setFirstName("Christopher");
        updatedUser.setLastName("Nolan");

        when(userService.updateUser(anyLong(), any(User.class))).thenReturn(ResponseEntity.ok(updatedUser));

        ResponseEntity<User> response = userController.updateUser(1L, updatedUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Nolan228@gmail.com", response.getBody().getEmail());
        assertEquals("Christopher", response.getBody().getFirstName());
        assertEquals("Nolan", response.getBody().getLastName());
    }

    @Test
    public void testUpdateUserFields() {
        Map<String, Object> fieldsToUpdate = new HashMap<>();
        fieldsToUpdate.put("firstName", "Quentin");
        fieldsToUpdate.put("lastName", "Tarantino");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setFirstName("Quentin");
        updatedUser.setLastName("Tarantino");

        when(userService.updateUserFields(anyLong(), any(Map.class))).thenReturn(ResponseEntity.ok(updatedUser));

        ResponseEntity<User> response = userController.updateUserFields(1L, fieldsToUpdate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(updatedUser.getFirstName(), response.getBody().getFirstName());
        assertEquals(updatedUser.getLastName(), response.getBody().getLastName());
    }

    @Test
    public void testDeleteUser() {
        when(userService.deleteUser(anyLong())).thenReturn(ResponseEntity.ok("Deleted"));
        ResponseEntity<String> response = userController.deleteUser(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Deleted", response.getBody());
    }

    @Test
    public void testSearchUsersByBirthDateRangeValid() {
        LocalDate fromDate = LocalDate.of(1990, 1, 1);
        LocalDate toDate = LocalDate.of(2000, 12, 31);

        User user1 = new User();
        user1.setId(1L);
        user1.setFirstName("User1");
        user1.setBirthDate(LocalDate.of(1995, 5, 5));

        User user2 = new User();
        user2.setId(2L);
        user2.setFirstName("User2");
        user2.setBirthDate(LocalDate.of(1990, 2, 15));

        when(userService.searchUsersByBirthDateRange(eq(fromDate), eq(toDate))).thenReturn(ResponseEntity.ok(List.of(user1, user2)));

        ResponseEntity<?> response = userController.searchUsersByBirthDateRange(fromDate, toDate);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);
        List<User> users = (List<User>) response.getBody();
        assertEquals(2, users.size());
        assertEquals("User1", users.get(0).getFirstName());
        assertEquals("User2", users.get(1).getFirstName());
    }

    @Test
    public void testSearchUsersByInvalidBirthDateRange() {
        LocalDate fromDate = LocalDate.of(2000, 12, 31);
        LocalDate toDate = LocalDate.of(1990, 1, 1);

        when(userService.searchUsersByBirthDateRange(eq(fromDate), eq(toDate)))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "'From' date must be before 'To' date"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userController.searchUsersByBirthDateRange(fromDate, toDate));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("'From' date must be before 'To' date", exception.getReason());
    }
}

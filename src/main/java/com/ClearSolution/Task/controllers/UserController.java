package com.ClearSolution.Task.controllers;

import com.ClearSolution.Task.entities.User;
import com.ClearSolution.Task.services.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {


    @Value("${registration.min-age}")
    private int minAge;
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        LocalDate minBirthDate = LocalDate.now().minusYears(minAge);
        if (user.getBirthDate().isBefore(minBirthDate)) {
            return userService.createUser(user);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("User must be at least "+minAge+" years old");
        }
    }
    @PutMapping("/{userId}/update")
    public ResponseEntity<User> updateUser(@RequestBody User user, @PathVariable Long userId) {
        User response = userService.updateUser(userId, user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/{userId}/update")
    public ResponseEntity<User> updateUserFields(@PathVariable Long userId, @RequestBody Map<String, Object> fieldsToUpdate) {
        User response = userService.updateUserFields(userId, fieldsToUpdate);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/delete")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return new ResponseEntity<>("User delete", HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUsersByBirthDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return userService.searchUsersByBirthDateRange(from, to);
    }
}

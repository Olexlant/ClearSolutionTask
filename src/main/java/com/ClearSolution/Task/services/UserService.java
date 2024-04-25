package com.ClearSolution.Task.services;

import com.ClearSolution.Task.entities.User;
import com.ClearSolution.Task.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class UserService {


    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<User> createUser(User user) {
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    public User updateUser(Long userId, User updatedUser) {
        updatedUser.setId(userId);
        userRepository.save(updatedUser);
        return updatedUser;
    }

    public User updateUserFields(Long userId, Map<String, Object> fieldsToUpdate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
            String field = entry.getKey();
            Object value = entry.getValue();
            switch (field) {
                case "firstName" -> user.setFirstName((String) value);
                case "lastName" -> user.setLastName((String) value);
                case "birthDate" -> {
                    if (value instanceof String) {
                        LocalDate newBirthDate = LocalDate.parse((String) value);
                        user.setBirthDate(newBirthDate);
                    } else if (value instanceof LocalDate) {
                        user.setBirthDate((LocalDate) value);
                    } else {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid birthDate format");
                    }
                }
                case "address" -> user.setAddress((String) value);
                case "phoneNumber" -> user.setPhoneNumber((String) value);
                default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid field: " + field);
            }
        }
        userRepository.save(user);
        return user;
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public ResponseEntity<List<User>> searchUsersByBirthDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate.isBefore(toDate)) {
            return new ResponseEntity<>(userRepository.findByBirthDateBetween(fromDate, toDate),HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'From' date must be before 'To' date");
        }
    }
}


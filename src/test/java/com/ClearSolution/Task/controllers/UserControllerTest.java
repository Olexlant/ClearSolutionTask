package com.ClearSolution.Task.controllers;

import com.ClearSolution.Task.entities.User;
import com.ClearSolution.Task.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUser_ifMoreThan18_shouldReturnOk200() throws Exception {
        User newUser = new User();
        newUser.setEmail("lantuh.sasha@gmail.com");
        newUser.setFirstName("Olex");
        newUser.setLastName("Biden");
        newUser.setBirthDate(LocalDate.now().minusYears(21)); // 21 years old

        given(userService.createUser(newUser)).willAnswer((InvocationOnMock::getArguments));

        ResultActions response = mockMvc.perform(post("/api/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)));

        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void createUser_ifUnder18_shouldReturnError400() throws Exception {
        User newUser = new User();
        newUser.setEmail("lantuh.sasha@gmail.com");
        newUser.setFirstName("Olex");
        newUser.setLastName("Biden");
        newUser.setBirthDate(LocalDate.now().minusYears(11)); // 11 years old

        given(userService.createUser(newUser)).willAnswer((InvocationOnMock::getArguments));

        ResultActions response = mockMvc.perform(post("/api/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)));

        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void updateUser_shouldReturnOk200() throws Exception {
        User newUser = new User();
        newUser.setId(1L);
        newUser.setEmail("lantukh.sasha@gmail.com");
        newUser.setFirstName("Olex");
        newUser.setLastName("Biden");
        newUser.setBirthDate(LocalDate.now().minusYears(21)); // 21 years old

        when(userService.updateUser(newUser.getId(), newUser)).thenReturn(newUser);

        ResultActions response = mockMvc.perform(put("/api/users/1/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)));

        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void updateUserFields_shouldReturnOk200() throws Exception {
        Map<String, Object> fieldsToUpdate = new HashMap<>();
        fieldsToUpdate.put("firstName", "Quentin");
        fieldsToUpdate.put("lastName", "Tarantino");
        User user = new User();
        user.setId(1L);

        when(userService.updateUserFields(user.getId(), fieldsToUpdate)).thenReturn(user);

        ResultActions response = mockMvc.perform(patch("/api/users/1/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));

        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void deleteUser_shouldReturnOk200() throws Exception{
        doNothing().when(userService).deleteUser(1L);

        ResultActions response = mockMvc.perform(delete("/api/users/1/delete")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

}
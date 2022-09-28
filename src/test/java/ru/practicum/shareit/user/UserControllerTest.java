package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;

    private static final long id = 1L;

    @Test
    void addUser() throws Exception {
        UserDto userDto = new UserDto(id, "One", "one@gmail.com");
        when(userService.addUser(any())).thenReturn(userDto);
        mockMvc.perform(
                        post("/users")
                                .content(mapper.writeValueAsString(userDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("One"))
                .andExpect(jsonPath("$.email").value("one@gmail.com"));
    }

    @Test
    void updateUser() throws Exception {
        UserDto userDto = new UserDto(id, "One", "one@gmail.com");
        when(userService.updateUser(anyLong(), any())).thenReturn(userDto);

        mockMvc.perform(
                        patch("/users/{userId}", id)
                                .content(mapper.writeValueAsString(userDto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    void getUser() throws Exception {
        UserDto userDto = new UserDto(1L, "One", "one@gmail.com");
        when(userService.getUser(any())).thenReturn(userDto);

        mockMvc.perform(get("/users/{userId}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    void deleteUser() throws Exception {
        doNothing().when(userService).deleteUser(id);
        mockMvc.perform(delete("/users/{userId}", id))
                .andExpect(status().isOk());
    }

    @Test
    void getAllUsers() throws Exception {
        List<UserDto> users = new ArrayList<>(
                Arrays.asList(new UserDto(),
                        new UserDto()));
        when(userService.getAllUsers()).thenReturn(users);
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(users.size()));
    }
}
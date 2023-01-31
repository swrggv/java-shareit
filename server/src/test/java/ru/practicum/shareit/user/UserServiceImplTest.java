package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

// TODO удалить закомментированный код
@SpringBootTest//(
//properties = "db.name=test",
//webEnvironment = SpringBootTest.WebEnvironment.NONE)
//@RequiredArgsConstructor(onConstructor_ = @Autowired)
//@TestPropertySource(locations = "classpath:/application.properties")
@TestPropertySource(properties = "application.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceImplTest {
    @Autowired
    private UserServiceImpl userService;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .id(1L)
                .name("user")
                .email("user@gmail.com")
                .build();
    }

    @Test
    void addUser() {
        userService.addUser(userDto);
        UserDto result = userService.getUser(1L);
        assertThat(result.getId(), notNullValue());
        assertThat(result, equalTo(userDto));
    }

    @Test
    void addUserWithException() {
        userService.addUser(userDto);
        assertThatThrownBy(() -> userService.addUser(userDto)).hasMessage("User with email already exist");
    }

    @Test
    void updateUser() {
        userService.addUser(userDto);
        userDto.setName("new");
        UserDto result = userService.updateUser(userDto.getId(), userDto);
        assertThat(result, equalTo(userDto));
    }

    @Test
    void updateUserWithException() {
        UserDto secondUser = UserDto.builder()
                .name("second")
                .email("second@gmail.com")
                .build();
        userService.addUser(userDto);
        userService.addUser(secondUser);
        assertThatThrownBy(
                () -> userService.updateUser(userDto.getId(), secondUser)).hasMessage("User with email already exist");
    }

    @Test
    void getUser() {
        userService.addUser(userDto);
        UserDto result = userService.getUser(userDto.getId());
        assertThat(result, equalTo(userDto));
    }

    @Test
    void getUserWithException() {
        assertThatThrownBy(
                () -> userService.getUser(userDto.getId())).hasMessage(String.format("User %d not found", userDto.getId()));
    }

    @Test
    void deleteUser() {
        userService.addUser(userDto);
        userService.deleteUser(userDto.getId());
        assertThat(userService.getAllUsers().size(), equalTo(0));
    }

    @Test
    void getAllUsers() {
        userService.addUser(userDto);
        List<UserDto> result = userService.getAllUsers();
        assertThat(result, equalTo(List.of(userDto)));
    }
}
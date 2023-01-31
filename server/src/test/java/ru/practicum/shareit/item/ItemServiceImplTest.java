package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithDate;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;


@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = "application.properties")
class ItemServiceImplTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    private UserDto owner;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        owner = UserDto.builder()
                .id(1L)
                .name("owner")
                .email("owner@gmail.com")
                .build();
        itemDto = ItemDto.builder()
                .id(1L)
                .name("item 1")
                .description("item 1")
                .available(true)
                .build();
        userService.addUser(owner);
    }

    @Test
    void addItem() {
        ItemDto result = itemService.addItem(itemDto, owner.getId());
        assertThat(result, equalTo(itemDto));
    }

    @Test
    void updateItem() {
        itemService.addItem(itemDto, owner.getId());
        itemDto.setName("updated item");
        ItemDto result = itemService.updateItem(itemDto, itemDto.getId(), owner.getId());
        assertThat(result, equalTo(itemDto));
    }

    @Test
    void updateItemWithException() {
        itemService.addItem(itemDto, owner.getId());
        assertThatThrownBy(() -> itemService.updateItem(itemDto, itemDto.getId(), 100))
                .hasMessage(String.format("Access is forbidden. User %s doesn't have access rights", 100));
    }

    @Test
    void getItemEachUserById() {
        itemService.addItem(itemDto, owner.getId());
        ItemDtoWithDate result = itemService.getItemEachUserById(itemDto.getId(), owner.getId());
        assertThat(result.getName(), equalTo(itemDto.getName()));
    }

    @Test
    void getAllItemsOfOwner() {
        itemService.addItem(itemDto, owner.getId());
        List<ItemDtoWithDate> result = itemService.getAllItemsOfOwner(owner.getId(), 0, 2);
        assertThat(result.size(), equalTo(1));


    }

    @Test
    void getItemsAvailableToRent() {
        itemService.addItem(itemDto, owner.getId());
        List<ItemDto> result = itemService.getItemsAvailableToRent("item", 0, 2);
        assertThat(result, equalTo(List.of(itemDto)));
    }
}
package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithDate;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {
    @MockBean
    private ItemService itemService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mockMvc;
    private ItemDto itemDto1;
    private ItemDtoWithDate itemDtoWithDate;
    private CommentDto commentDto;

    @BeforeEach
    void createItem() {
        itemDto1 = new ItemDto(1L, "itemDto1", "one", true, 1L);
        itemDtoWithDate = new ItemDtoWithDate(1L, "dto", "dto with date", true,
                1L, null, null, null);
        commentDto = new CommentDto(1L, "comment", "Gregory", LocalDateTime.now());
    }

    @Test
    void addItem() throws Exception {
        when(itemService.addItem(any(), anyLong())).thenReturn(itemDto1);

        mockMvc.perform(
                        post("/items")
                                .content(mapper.writeValueAsString(itemDto1))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(itemDto1.getName()))
                .andExpect(jsonPath("$.description").value(itemDto1.getDescription()));
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.updateItem(any(), anyLong(), anyLong())).thenReturn(itemDto1);

        mockMvc.perform(
                patch("/items/{itemId}", itemDto1.getId())
                        .content(mapper.writeValueAsString(itemDto1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(itemDto1.getName()))
                .andExpect(jsonPath("$.description").value(itemDto1.getDescription()));
    }

    @Test
    void getItemEachUser() throws Exception {
        when(itemService.getItemEachUserById(anyLong(), anyLong())).thenReturn(itemDtoWithDate);
        mockMvc.perform(
                        get("/items/{itemId}", 1L)
                                .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(itemDtoWithDate.getName()))
                .andExpect(jsonPath("$.description").value(itemDtoWithDate.getDescription()));
    }

    @Test
    void getItemOwnerUser() throws Exception {
        List<ItemDtoWithDate> items = List.of(itemDtoWithDate);
        when(itemService.getAllItemsOfOwner(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemDtoWithDate));
        mockMvc.perform(
                        get("/items")
                                .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(items.size()));
    }

    @Test
    void getItemAvailableToRenter() throws Exception {
        List<ItemDto> items = List.of(itemDto1);
        when(itemService.getItemsAvailableToRent(anyString(), anyInt(), anyInt())).thenReturn(List.of(itemDto1));

        mockMvc.perform(
                        get("/items/search")
                                .param("text", "one")
                                .param("from", "0")
                                .param("size", "3")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(items.size()));
    }

    @Test
    void addCommentToItem() throws Exception {
        when(itemService.addCommentToItem(1L, 1L, commentDto)).thenReturn(commentDto);

        mockMvc.perform(
                        post("/items/{itemId}/comment", 1L)
                                .content(mapper.writeValueAsString(commentDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1L)
                )
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.text").value("comment"))
                .andExpect(jsonPath("$.authorName").value("Gregory"));
    }
}
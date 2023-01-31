package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithDate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private ItemDto itemDto;
    private ItemDtoWithDate dtoWithDate;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("item")
                .available(true)
                .description("description")
                .build();
        dtoWithDate = ItemDtoWithDate.builder()
                .id(1L)
                .name("item")
                .available(true)
                .description("description")
                .build();
        commentDto = CommentDto.builder()
                .id(1L)
                .authorName("author")
                .created(LocalDateTime.now())
                .text("text")
                .build();
    }


    @Test
    void addItem() throws Exception {
        Mockito.when(itemService.addItem(any(), anyLong())).thenReturn(itemDto);
        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)) //TODO сделать юзера и убрать точное значение
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));
    }

    @Test
    void updateItem() throws Exception {
        Mockito.when(itemService.updateItem(any(), anyLong(), anyLong())).thenReturn(itemDto);
        mvc.perform(patch("/items/{itemId}", itemDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));
    }

    @Test
    void getItemEachUser() throws Exception {
        Mockito.when(itemService.getItemEachUserById(anyLong(), anyLong())).thenReturn(dtoWithDate);
        mvc.perform(get("/items/{itemId}", dtoWithDate.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dtoWithDate.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(dtoWithDate.getName())))
                .andExpect(jsonPath("$.available", is(dtoWithDate.getAvailable())))
                .andExpect(jsonPath("$.description", is(dtoWithDate.getDescription())));
    }

    @Test
    void getItemOwnerUser() throws Exception {
        Mockito.when(itemService.getAllItemsOfOwner(anyLong(), anyInt(), anyInt())).thenReturn(List.of(dtoWithDate));
        mvc.perform(get("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(dtoWithDate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(dtoWithDate.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(dtoWithDate.getName())))
                .andExpect(jsonPath("$.[0].available", is(dtoWithDate.getAvailable())))
                .andExpect(jsonPath("$.[0].description", is(dtoWithDate.getDescription())));
    }

    @Test
    void getItemAvailableToRenter() throws Exception {
        Mockito.when(itemService.getItemsAvailableToRent(anyString(), anyInt(), anyInt())).thenReturn(List.of(itemDto));
        mvc.perform(get("/items/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("text", "text"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$.[0].available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.[0].description", is(itemDto.getDescription())));
    }

    @Test
    void addCommentToItem() throws Exception {
        Mockito.when(itemService.addCommentToItem(anyLong(), anyLong(), any())).thenReturn(commentDto);
        mvc.perform(post("/items/{itemId}/comment", itemDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.text", is(commentDto.getText())));
    }
}
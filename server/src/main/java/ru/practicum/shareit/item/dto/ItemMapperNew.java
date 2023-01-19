package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.springframework.lang.Nullable;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapperNew {
    @Mapping(source = "itemDto.id", target = "id")
    @Mapping(source = "itemDto.name", target = "name")
    @Mapping(source = "itemDto.description", target = "description")
    @Mapping(source = "user", target = "owner")
    @Mapping(source = "request", target = "itemRequest")
    Item toItem(ItemDto itemDto, User user, ItemRequest request);

    @Mapping(source = "item.itemRequest.id", target = "requestId")
    ItemDto toItemDto(Item item);

    @Mapping(source = "item.itemRequest.id", target = "requestId", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    ItemDtoWithDate toItemDtoWithDate(Item item);

    List<ItemDtoWithDate> toListItemDtoWithDate(List<Item> items);

    @Mapping(source = "commentDto.id", target = "id")
    Comment toComment(CommentDto commentDto, Item item, User author);

    @Mapping(source = "comment.author.name", target = "authorName")
    @Mapping(source = "created", target = "created")
    CommentDto toCommentDto(Comment comment);

    List<CommentDto> toListCommentsDto(List<Comment> comments);
}

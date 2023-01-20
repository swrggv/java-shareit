package ru.practicum.shareit.requests.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemMapperNew;
import ru.practicum.shareit.requests.model.Request;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ItemMapperNew.class})
public interface RequestMapper {
    @Mapping(source = "request.requestor.id", target = "requestorId")
    RequestDto toRequestDto(Request request);
    @Mapping(source = "requestDto.id", target = "id")
    Request toRequest(RequestDto requestDto, User requestor);
    List<RequestDto> toRequestDtoList(List<Request> requests);
}

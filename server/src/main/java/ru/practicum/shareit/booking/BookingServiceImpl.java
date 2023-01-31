package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.ModelNotFoundException;
import ru.practicum.shareit.exception.UnknownStateException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private final BookingMapper mapper;

    @Transactional
    @Override
    public BookingDto addBooking(BookItemRequestDto bookItemRequestDto, long userId) {
        Item item = fromOptionalToItem(bookItemRequestDto.getItemId());
        User booker = fromOptionalToUser(userId);
        if (isValid(item, booker)) {
            Booking booking = mapper.toBooking(bookItemRequestDto, item, booker);
            booking.setStatus(Status.WAITING);
            Booking result = bookingRepository.save(booking);
            return mapper.toBookingDto(result);
        } else {
            throw new ValidationException("Validation exception");
        }
    }

    @Transactional
    @Override
    public BookingDto approveBooking(long bookingId, Boolean approved, long userId) {
        Booking booking = checkForApproving(bookingId, approved, userId);
        return mapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingByIdIfOwnerOrBooker(long bookingId, long userId) {
        if (!userRepository.existsById(userId) || !bookingRepository.existsById(bookingId)) {
            throw new ModelNotFoundException("User or booking not found");
        } else {
            Booking booking = fromOptionalToBooking(bookingId);
            if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
                return mapper.toBookingDto(booking);
            } else {
                throw new ModelNotFoundException("Booking not found");
            }
        }
    }

    @Override
    public List<BookingDto>  getBookingByUserSorted(long bookerId, State state, int from, int size) {
        if (!userRepository.existsById(bookerId)) {
            throw new ModelNotFoundException("User not found");
        }
        List<Booking> bookings;
        int page = getPageNumber(from, size);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        switch (state) {
            case ALL:
                bookings = bookingRepository.findBookingByBookerId(bookerId,
                        PageRequest.of(page, size, sort));
                break;
            case CURRENT:
                bookings = bookingRepository.findBookingsCurrentForBooker(bookerId, LocalDateTime.now(),
                        PageRequest.of(page, size, sort));
                break;
            case PAST:
                bookings = bookingRepository.findBookingsPastForBooker(bookerId, LocalDateTime.now(),
                        PageRequest.of(page, size, sort));
                break;
            case FUTURE:
                bookings = bookingRepository.findBookingsFutureForBooker(bookerId, LocalDateTime.now(),
                        PageRequest.of(page, size, sort));
                break;
            case WAITING:
                bookings = bookingRepository.findBookingsByStatusAndBookerId(bookerId, Status.WAITING,
                        PageRequest.of(page, size, sort));
                break;
            case REJECTED:
                bookings = bookingRepository.findBookingsByStatusAndBookerId(bookerId, Status.REJECTED,
                        PageRequest.of(page, size, sort));
                break;
            default:
                throw new UnknownStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        return mapper.toListBookingDto(bookings);
    }

    @Override
    public List<BookingDto> getBookingByItemOwner(long ownerId, State state, int from, int size) {
        if (!userRepository.existsById(ownerId)) {
            throw new ModelNotFoundException("User not found");
        }
        List<Booking> bookings;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllBookingsForItemOwner(ownerId,
                        PageRequest.of(from, size, sort));
                break;
            case CURRENT:
                bookings = bookingRepository.findBookingsCurrentForItemOwner(ownerId, LocalDateTime.now(),
                        PageRequest.of(from, size, sort));
                break;
            case PAST:
                bookings = bookingRepository.findBookingsPastForItemOwner(ownerId, LocalDateTime.now(),
                        PageRequest.of(from, size, sort));
                break;
            case FUTURE:
                bookings = bookingRepository.findBookingsFutureForItemOwner(ownerId, LocalDateTime.now(),
                        PageRequest.of(from, size, sort));
                break;
            case WAITING:
                bookings = bookingRepository.findBookingsByStatusForItemOwner(ownerId, Status.WAITING,
                        PageRequest.of(from, size, sort));
                break;
            case REJECTED:
                bookings = bookingRepository.findBookingsByStatusForItemOwner(ownerId, Status.REJECTED,
                        PageRequest.of(from, size, sort));
                break;
            default:
                throw new UnknownStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        return mapper.toListBookingDto(bookings);
    }

    private Booking checkForApproving(long bookingId, Boolean approved, long userId) {
        Booking booking = fromOptionalToBooking(bookingId);
        Item item;
        item = booking.getItem();
        if (item.getOwner().getId() != userId) {
            throw new ModelNotFoundException("User not found");
        } else if (!item.getAvailable()) {
            throw new ValidationException("Item is not available");
        }
        if (booking.getStatus() == Status.APPROVED) {
            throw new ValidationException("Booking is already approved");
        }
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return booking;
    }

    private int getPageNumber(int from, int size) {
        return from / size;
    }


    /* если чекать пересечение else if (isBooked(start, end, item.get().getId())) {
            throw new ValidationException("Dates is already booked");
        }*/
    private boolean isValid(Item item, User booker) {
        if (!item.getAvailable()) {
            throw new ValidationException(String.format("Item %s is not available", item.getId()));
        } else if (item.getOwner().getId() == booker.getId()) {
            throw new ModelNotFoundException("User can not book his own item");
        } else {
            return true;
        }
    }

    private Item fromOptionalToItem(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ModelNotFoundException(String.format("Item %d not found", itemId)));
    }

    private Booking fromOptionalToBooking(long bookingId) {
        return bookingRepository
                .findById(bookingId)
                .orElseThrow(() -> new ModelNotFoundException(String.format("Booking %d not found", bookingId)));
    }

    private User fromOptionalToUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ModelNotFoundException(String.format("User %s not found", userId)));
    }

    /*private boolean isBooked(LocalDateTime start, LocalDateTime end, long itemId) {
        return bookingRepository.isFree(start, end, itemId);
    }*/
}

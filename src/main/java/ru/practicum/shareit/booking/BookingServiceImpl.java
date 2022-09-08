package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              UserRepository userRepository,
                              ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    @Override
    public BookingDto addBooking(BookItemRequestDto bookItemRequestDto, long userId) {
        Item item = fromOptionalToItem(bookItemRequestDto.getItemId());
        User booker = fromOptionalToUser(userId);
        if (isValid(bookItemRequestDto, item, booker)) {
            Booking booking = BookingMapper.toBooking(bookItemRequestDto, item, booker);
            booking.setStatus(Status.WAITING);
            Booking result = bookingRepository.save(booking);
            return BookingMapper.toBookingDto(result);
        } else {
            throw new ValidationException("Validation exception");
        }
    }

    @Transactional
    @Override
    public BookingDto approveBooking(long bookingId, Boolean approved, long userId) {
        Booking booking = checkForApproving(bookingId, approved, userId);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingByIdIfOwnerOrBooker(long bookingId, long userId) {
        if (!userRepository.existsById(userId) || !bookingRepository.existsById(bookingId)) {
            throw new ModelNotFoundException("User or booking not found");
        } else {
            Booking booking = fromOptionalToBooking(bookingId);
            if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
                return BookingMapper.toBookingDto(booking);
            } else {
                throw new ModelNotFoundException("User not found");
            }
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDto> getBookingByUserSorted(long bookerId, State state) {
        if (!userRepository.existsById(bookerId)) {
            throw new ModelNotFoundException("User not found");
        }
        List<Booking> bookings;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        switch (state) {
            case ALL:
                bookings = bookingRepository.findBookingByBookerIdOrderByStartDesc(bookerId);
                break;
            case CURRENT:
                bookings = bookingRepository.findBookingsCurrentForBooker(bookerId, LocalDateTime.now(), sort);
                break;
            case PAST:
                bookings = bookingRepository.findBookingsPastForBooker(bookerId, LocalDateTime.now(), sort);
                break;
            case FUTURE:
                bookings = bookingRepository.findBookingsFutureForBooker(bookerId, LocalDateTime.now(), sort);
                break;
            case WAITING:
                bookings = bookingRepository.findBookingsByStatusAndBookerId(bookerId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findBookingsByStatusAndBookerId(bookerId, Status.REJECTED);
                break;
            default:
                throw new UnknownStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        return BookingMapper.toListBookingDto(bookings);
    }

    @Override
    public List<BookingDto> getBookingByItemOwner(long ownerId, State state) {
        if (!userRepository.existsById(ownerId)) {
            throw new ModelNotFoundException("User not found");
        }
        List<Booking> bookings;
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllBookingsForItemOwner(ownerId, sort);
                break;
            case CURRENT:
                bookings = bookingRepository.findBookingsCurrentForItemOwner(ownerId, LocalDateTime.now(), sort);
                break;
            case PAST:
                bookings = bookingRepository.findBookingsPastForItemOwner(ownerId, LocalDateTime.now(), sort);
                break;
            case FUTURE:
                bookings = bookingRepository.findBookingsFutureForItemOwner(ownerId, LocalDateTime.now(), sort);
                break;
            case WAITING:
                bookings = bookingRepository.findBookingsByStatusForItemOwner(ownerId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findBookingsByStatusForItemOwner(ownerId, Status.REJECTED);
                break;
            default:
                throw new UnknownStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        return BookingMapper.toListBookingDto(bookings);
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


    /* если чекать пересечение else if (isBooked(start, end, item.get().getId())) {
            throw new ValidationException("Dates is already booked");
        }*/
    private boolean isValid(BookItemRequestDto bookItemRequestDto, Item item, User booker) {
        LocalDateTime start = bookItemRequestDto.getStart();
        LocalDateTime end = bookItemRequestDto.getEnd();
        if (end.isBefore(start) && !start.isEqual(end)) {
            throw new ValidationException("End date should not be before start date");
        } else if (!item.getAvailable()) {
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

    private boolean isBooked(LocalDateTime start, LocalDateTime end, long itemId) {
        return bookingRepository.isBooked(start, end, itemId);
    }
}

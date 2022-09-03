package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOutcome;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
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

    @Override
    public BookingDtoOutcome addBooking(BookingDto bookingDto, long userId) {
        Optional<Item> item = itemRepository.findById(bookingDto.getItemId());
        Optional<User> booker = userRepository.findById(userId);
        if (isValid(bookingDto, item, booker)) {
            Booking booking = BookingMapper.toBooking(bookingDto, item.get(), booker.get());
            booking.setStatus(Status.WAITING);
            Booking result = bookingRepository.save(booking);
            return BookingMapper.toBookingDtoOutcome(result);
        } else {
            throw new ValidationException("Validation exception");
        }
    }

    @Override
    public BookingDtoOutcome approveBooking(long bookingId, Boolean approved, long userId) {
        Booking booking = checkForApproving(bookingId, approved, userId);
        return BookingMapper.toBookingDtoOutcome(bookingRepository.save(booking));
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDtoOutcome getBookingByIdIfOwnerOrBooker(long bookingId, long userId) {
        if (!userRepository.existsById(userId) || !bookingRepository.existsById(bookingId)) {
            throw new ModelNotFoundException("User or booking not found");
        } else {
            Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
            Booking booking = optionalBooking.orElseThrow(() -> new ModelNotFoundException("Booking not found"));
            if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
                return BookingMapper.toBookingDtoOutcome(booking);
            } else {
                throw new ModelNotFoundException("User not found");
            }
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDtoOutcome> getBookingByUserSorted(long bookerId, State state) {
        if (!userRepository.existsById(bookerId)) {
            throw new ModelNotFoundException("User not found");
        }
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findBookingByBookerIdOrderByStartDesc(bookerId);
                break;
            case CURRENT:
                bookings = bookingRepository.findBookingsCurrentForBooker(bookerId, LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findBookingsPastForBooker(bookerId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findBookingsFutureForBooker(bookerId, LocalDateTime.now());
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
        return toBookingDtoOutcomesList(bookings);
    }

    @Override
    public List<BookingDtoOutcome> getBookingByItemOwner(long ownerId, State state) {
        if (!userRepository.existsById(ownerId)) {
            throw new ModelNotFoundException("User not found");
        }
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllBookingsForItemOwner(ownerId);
                break;
            case CURRENT:
                bookings = bookingRepository.findBookingsCurrentForItemOwner(ownerId, LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findBookingsPastForItemOwner(ownerId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findBookingsFutureForItemOwner(ownerId, LocalDateTime.now());
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
        return toBookingDtoOutcomesList(bookings);
    }

    private List<BookingDtoOutcome> toBookingDtoOutcomesList(List<Booking> income) {
        return income.stream()
                .map(BookingMapper::toBookingDtoOutcome)
                .collect(Collectors.toList());
    }

    private Booking checkForApproving(long bookingId, Boolean approved, long userId) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        Booking booking;
        Item item;
        if (optionalBooking.isPresent()) {
            booking = optionalBooking.get();
        } else {
            throw new ModelNotFoundException(String.format("Booking %s not found", bookingId));
        }
        item = optionalBooking.get().getItem();
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

    private boolean isValid(BookingDto bookingDto, Optional<Item> item, Optional<User> booker) {
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        if (end.isBefore(start)) {
            throw new ValidationException("End date should not be before start date");
        } else if (start.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Start date should not be in the past");
        } else if (item.isEmpty()) {
            throw new ModelNotFoundException("Item not found");
        } else if (!item.get().getAvailable()) {
            throw new ValidationException(String.format("Item %s is not available", item.get().getId()));
        } else if (booker.isEmpty()) {
            throw new ModelNotFoundException("Booker not found");
        } else if (item.get().getOwner().getId() == booker.get().getId()) {
            throw new ModelNotFoundException("User can not book his own item");
        } else if (isBooked(start, end, item.get().getId())) {
            throw new ValidationException("Dates is already booked");
        } else {
            return true;
        }

    }

    private boolean isBooked(LocalDateTime start, LocalDateTime end, long itemId) {
        return bookingRepository.isBooked(start, end, itemId);
    }
}

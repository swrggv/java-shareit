package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findBookingByBookerId(long bookerId, Pageable pageable);

    @Query("from Booking b where b.booker.id = :bookerId and :date between b.start and b.end")
    List<Booking> findBookingsCurrentForBooker(@Param("bookerId") long bookerId,
                                               @Param("date") LocalDateTime date, Pageable pageable);

    @Query("from Booking b where b.booker.id = :bookerId and b.end < :date")
    List<Booking> findBookingsPastForBooker(@Param("bookerId") long bookerId, @Param("date") LocalDateTime date,
                                            Pageable pageable);

    @Query("from Booking b where b.booker.id = :bookerId and b.start > :date")
    List<Booking> findBookingsFutureForBooker(@Param("bookerId") long bookerId, @Param("date") LocalDateTime date,
                                              Pageable pageable);

    @Query("from Booking b where b.booker.id = :bookerId and b.status = :status")
    List<Booking> findBookingsByStatusAndBookerId(@Param("bookerId") long bookerId, Status status,
                                                  Pageable pageable);

    @Query("from Booking b where b.item.owner.id = :ownerId")
    List<Booking> findAllBookingsForItemOwner(@Param("ownerId") long ownerId, Pageable pageable);

    @Query("from Booking b where b.item.owner.id = :ownerId and :date between b.start and b.end")
    List<Booking> findBookingsCurrentForItemOwner(@Param("ownerId") long ownerId, @Param("date") LocalDateTime date,
                                                  Pageable pageable);

    @Query("from Booking b where b.item.owner.id = :ownerId and b.end < :date")
    List<Booking> findBookingsPastForItemOwner(@Param("ownerId") long ownerId, @Param("date") LocalDateTime date,
                                               Pageable pageable);

    @Query("from Booking b where b.item.owner.id = :ownerId and b.start > :date")
    List<Booking> findBookingsFutureForItemOwner(@Param("ownerId") long ownerId, @Param("date") LocalDateTime date,
                                                 Pageable pageable);

    @Query("from Booking b where b.item.owner.id = :ownerId and b.status = :status")
    List<Booking> findBookingsByStatusForItemOwner(@Param("ownerId") long ownerId, @Param("status") Status status,
                                                   Pageable pageable);

    @Query(value = "select exists(select * from bookings as b where not " +
            "(:start between b.start_date and b.end_date or " +
            ":end between b.start_date and b.end_date) and " +
            "b.item_id = :itemId)",
            nativeQuery = true)
    boolean isFree(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("itemId") long itemId);

    @Query(value = "select * from bookings b " +
            "where b.item_id = :itemId and b.start_date < :date order by b.start_date limit  1",
            nativeQuery = true)
    Booking findBookingByItemWithDateBefore(@Param("itemId") long itemId, @Param("date") LocalDateTime date);

    @Query(value = "select * from bookings b " +
            "where b.item_id = :itemId and b.start_date > :date order by b.start_date limit  1",
            nativeQuery = true)
    Booking findBookingByItemWithDateAfter(@Param("itemId") long itemId, @Param("date") LocalDateTime date);

    @Query(value = "select exists(select * from bookings b " +
            "where b.booker_id = :userId and b.item_id = :itemId and  b.end_date < :date)",
            nativeQuery = true)
    boolean isExists(@Param("itemId") long itemId,
                     @Param("userId") long userId,
                     @Param("date") LocalDateTime date);
}

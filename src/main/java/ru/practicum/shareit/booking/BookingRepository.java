package ru.practicum.shareit.booking;

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

    List<Booking> findBookingByBookerIdOrderByStartDesc(long bookerId);

    @Query("from Booking b where b.booker.id = :bookerId and b.start <= :date and b.end >= :date order by b.start")
    List<Booking> findBookingsCurrentForBooker(@Param("bookerId") long bookerId, @Param("date") LocalDateTime date);

    @Query("from Booking b where b.booker.id = :bookerId and b.end < :date order by b.start desc")
    List<Booking> findBookingsPastForBooker(@Param("bookerId") long bookerId, @Param("date") LocalDateTime date);

    @Query("from Booking b where b.booker.id = :bookerId and b.start > :date order by b.start desc")
    List<Booking> findBookingsFutureForBooker(@Param("bookerId") long bookerId, @Param("date") LocalDateTime date);

    @Query("from Booking b where b.booker.id = :bookerId and b.status = :status")
    List<Booking> findBookingsByStatusAndBookerId(@Param("bookerId") long bookerId, Status status);

    @Query("from Booking b where b.item.owner.id = :ownerId order by b.start desc")
    List<Booking> findAllBookingsForItemOwner(@Param("ownerId") long ownerId);

    @Query("from Booking b where b.item.owner.id = :ownerId and b.start <= :date and b.end >= :date order by b.start")
    List<Booking> findBookingsCurrentForItemOwner(@Param("ownerId") long ownerId, @Param("date") LocalDateTime date);

    @Query("from Booking b where b.item.owner.id = :ownerId and b.end < :date order by b.start desc")
    List<Booking> findBookingsPastForItemOwner(@Param("ownerId") long ownerId, @Param("date") LocalDateTime date);

    @Query("from Booking b where b.item.owner.id = :ownerId and b.start > :date order by b.start desc")
    List<Booking> findBookingsFutureForItemOwner(@Param("ownerId") long ownerId, @Param("date") LocalDateTime date);

    @Query("from Booking b where b.item.owner.id = :ownerId and b.status = :status")
    List<Booking> findBookingsByStatusForItemOwner(@Param("ownerId") long ownerId, @Param("status") Status status);

    /*@Query(value = "select exists(select * from bookings b " +
            "where (b.start_date, b.end_date) OVERLAPS (:start, :end) and b.item_id = :itemId)",
            nativeQuery = true)*/
    @Query(value = "select exists(select * from bookings as b where not " +
            "(:start between b.start_date and b.end_date or " +
            ":end between b.start_date and b.end_date) and " +
            "b.item_id = :itemId)",
            nativeQuery = true)
    boolean isBooked(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("itemId") long itemId);

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

package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByItemIdIn(List<Long> itemIds);

    List<Booking> findAllByBookerId(long bookerId);

    boolean existsByBookerIdAndItemIdAndEndBefore(long bookerId, long itemId, LocalDateTime end);

    Optional<Booking> findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(long itemId, Status status,
                                                                               LocalDateTime now);

    Optional<Booking> findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(long itemId, Status status,
                                                                             LocalDateTime now);

}

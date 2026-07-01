package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByItemIdIn(List<Long> itemIds);

    List<Booking> findAllByBookerId(long bookerId);

}

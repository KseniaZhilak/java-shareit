package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByRequestorIdOrderByCreatedDesc(long requestorId);

    List<Request> findAllByRequestorIdNotOrderByCreatedDesc(long requestorId);
}

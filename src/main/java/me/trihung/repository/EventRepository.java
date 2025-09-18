package me.trihung.repository;

import me.trihung.entity.Event;
import me.trihung.entity.User;
import me.trihung.enums.EventStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
	Page<Event> findByStatus(EventStatus status, Pageable pageable);

    Page<Event> findByEventNameContainingIgnoreCase(String search, Pageable pageable);

    Page<Event> findByStatusAndEventNameContainingIgnoreCase(EventStatus status, String search, Pageable pageable);

    Page<Event> findByOwner(User owner, Pageable pageable);

    Page<Event> findByOwnerAndStatus(User owner, EventStatus status, Pageable pageable);

    Page<Event> findByOwnerAndEventNameContainingIgnoreCase(User owner, String search, Pageable pageable);

    Page<Event> findByOwnerAndStatusAndEventNameContainingIgnoreCase(User owner, EventStatus status, String search, Pageable pageable);

}

package me.trihung.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import me.trihung.entity.Zone;

import java.util.List;

@Repository
public interface ZoneRepository extends MongoRepository<Zone, String> {
    List<Zone> findByEventId(String eventId);
}

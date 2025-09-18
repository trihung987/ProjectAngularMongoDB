package me.trihung.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.trihung.entity.Zone;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, UUID> {

}

package me.trihung.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import me.trihung.entity.Role;

@Repository
public interface RoleRepository extends MongoRepository<Role, String>{
    Role findByName(String name);
}
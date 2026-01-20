package com.assignment.assignment.repository;

import com.assignment.assignment.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
}

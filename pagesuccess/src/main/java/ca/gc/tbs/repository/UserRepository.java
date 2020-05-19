package ca.gc.tbs.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import ca.gc.tbs.domain.User;

public interface UserRepository extends MongoRepository<User, String> {
    
    User findByEmail(String email);
    
}

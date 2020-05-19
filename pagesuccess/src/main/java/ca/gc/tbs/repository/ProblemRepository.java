package ca.gc.tbs.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import ca.gc.tbs.domain.Problem;


public interface ProblemRepository extends MongoRepository<Problem, String> {
	
}

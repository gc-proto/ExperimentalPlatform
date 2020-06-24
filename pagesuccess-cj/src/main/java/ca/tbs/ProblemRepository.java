package ca.tbs;


import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProblemRepository extends MongoRepository<Problem, String> {
	List<Problem> findByAirTableSync(String syncd);
}

package pl.agh.ewidencja.repository;

import org.springframework.data.mongodb.repository.Query;
import pl.agh.ewidencja.entites.Port;
import pl.agh.ewidencja.entites.Router;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouterRepository extends MongoRepository<Router,String> {
    @Query("todo")
    Router updateProduct(String id, String netID, List<Port> ports);
}

package pl.agh.ewidencja.repository;

import org.springframework.data.mongodb.repository.Query;
import pl.agh.ewidencja.entites.AccessPoint;
import pl.agh.ewidencja.entites.Port;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccessPointRepository extends MongoRepository<AccessPoint,String> {

    @Query("todo")
    AccessPoint updateProduct(String id, String netID, List<Port> ports);
}

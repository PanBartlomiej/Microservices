package pl.agh.ewidencja.repository;

import org.springframework.data.mongodb.repository.Query;
import pl.agh.ewidencja.entites.Port;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

/**
 * Klasa typu Repository. Odpowiedzialna za zapytania do bazy danych związane z węzłami Port.
 */
@Repository
public interface PortRepository extends MongoRepository<Port,String> {

}

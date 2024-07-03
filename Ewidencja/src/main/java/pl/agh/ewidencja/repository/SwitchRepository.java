package pl.agh.ewidencja.repository;

import org.springframework.data.mongodb.repository.Query;
import pl.agh.ewidencja.entites.Port;
import pl.agh.ewidencja.entites.Switch;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Klasa typu Repository.  Odpowiedzialna za zapytania do bazy danych związane z węzłami Urządzenie.
 */
@Repository
public interface SwitchRepository extends MongoRepository<Switch,String> {

}

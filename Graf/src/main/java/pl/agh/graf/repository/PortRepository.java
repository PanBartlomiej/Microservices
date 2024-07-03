package pl.agh.graf.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import pl.agh.graf.entites.Port;

/**
 * Klasa typu Repository. Odpowiedzialna za zapytania do bazy danych związane z węzłami Port.
 */
@Repository
public interface PortRepository extends Neo4jRepository<Port,String> {
    /**
     * metoda odpowiedzialan za zapytanie bazodanowe łączące dwa porty
     * @param portID1
     * @param portID2
     */

    @Query("MATCH (port1:Port {id: $portID1}), (port2:Port {id: $portID2})\n" +
            "CREATE (port1)-[:CONNECTED_TO]->(port2)\n" +
            "CREATE (port2)-[:CONNECTED_TO]->(port1)")
    void connectPorts(@Param("portID1") String portID1,@Param("portID2")String portID2);


    /**
     * metoda odpowiedzialna za zapytanie bazodanowe rozłączające dwa port.
     * @param portID1
     * @param portID2
     * @return
     */
    @Query("MATCH (port1:Port {id: $portID1})-[r:CONNECTED_TO]-(port2:Port {id: $portID2})\n" +
            "DELETE r;")
    ResponseEntity<String> disconnect(@Param("portID1") String portID1,@Param("portID2")String portID2);
}

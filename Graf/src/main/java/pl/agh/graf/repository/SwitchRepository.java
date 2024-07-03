package pl.agh.graf.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.agh.graf.entites.Port;
import pl.agh.graf.entites.Switch;

import java.util.List;
import java.util.Map;

/**
 * Klasa typu Repository.  Odpowiedzialna za zapytania do bazy danych związane z węzłami Urządzenie.
 */
@Repository
public interface SwitchRepository extends Neo4jRepository<Switch,String> {
    /**
     * metoda modyfikuje urządzenie.
     * @param productID
     * @param newNetID
     * @param portsData
     * @return
     */
    @Query("MATCH (product:Switch {id: $productId})\n" +
            "SET product.netID = $newNetID,\n" +
            "    product.x = $x,\n" +
            "    product.y = $y\n" +
            "WITH product\n" +
            "OPTIONAL MATCH (product)-[r:CONNECTED_TO]->(:Port)\n" +
            "DELETE r\n" +
            "FOREACH (portData IN $portsData |\n" +
            "    MERGE (port:Port {id: portData.ID})\n" +
            "    MERGE (product)-[:CONNECTED_TO]->(port)\n" +
            ")\n" +
            "RETURN product;")
    Switch updateProduct(@Param("productId")String productID,@Param("x")double x,@Param("y")double y,
                         @Param("newNetID")String newNetID, @Param("portsData")List<Port> portsData);

    @Query("MATCH (s1:Switch {id: $productID})-[:CONNECTED_TO]-(p1:Port)\n" +
            "MATCH (p1)-[:CONNECTED_TO]-(p2:Port)\n" +
            "MATCH (p2)-[]-(s2:Switch)\n" +
            "RETURN s1, p1,p2,s2;")
    List<Map<String, Object>> getSwitchByIDwithNeighbours(@Param("productID")String productID);

    @Query("UNWIND $switches AS switch\n" +
            "MATCH (s:Switch {id: switch.id})\n" +
            "SET s.x = switch.x, s.y = switch.y")
    void updatePositions(@Param("switches")List<Switch> switches);
}

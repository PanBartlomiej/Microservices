package pl.agh.graf.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.agh.graf.entites.Computer;
import pl.agh.graf.entites.Port;
import pl.agh.graf.entites.Server;

import java.util.List;

@Repository
public interface ComputerRepository extends Neo4jRepository<Computer,String> {
    @Query("MATCH (product:Computer {id: $productId})\n" +
            "SET product.netID = $newNetID\n" +
            "WITH product\n" +
            "OPTIONAL MATCH (product)-[r:CONNECTED_TO]->(:Port)\n" +
            "DELETE r\n" +
            "FOREACH (portData IN $portsData |\n" +
            "    MERGE (port:Port {id: portData.id})\n" +
            "    MERGE (product)-[:CONNECTED_TO]->(port)\n" +
            ")\n" +
            "RETURN product;")
    Server updateProduct(@Param("productId")String productID, @Param("newNetID")String newNetID, @Param("portsData") List<Port> portsData);

}

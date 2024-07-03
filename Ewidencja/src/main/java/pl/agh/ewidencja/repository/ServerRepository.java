package pl.agh.ewidencja.repository;

import org.springframework.data.mongodb.repository.Query;
import pl.agh.ewidencja.entites.Port;
import pl.agh.ewidencja.entites.Server;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServerRepository extends MongoRepository<Server,String> {
    @Query("todo")

    Server updateProduct(@Param("productId")String productID, @Param("newNetID")String newNetID, @Param("portsData")List<Port> portsData);

}

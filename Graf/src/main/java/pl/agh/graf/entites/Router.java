package pl.agh.graf.entites;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Node
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Router {
    @Id
    private String id;
    private double x;
    private double y;
    private String netID;

    private String color="yellow";
    private String type="router";
    @Relationship(type = "CONNECTED_TO",direction = Relationship.Direction.OUTGOING)
    private List<Port> ports;
}

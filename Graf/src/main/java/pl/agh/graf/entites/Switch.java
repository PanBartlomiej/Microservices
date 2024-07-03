package pl.agh.graf.entites;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

/**
 * Klasa reprezentuje encje urządzenia w bazie danych
 * Posiada:
 * productID - id urządzenia
 * netID - id wewnątrz sieci
 * ports - porty urządzenia
 */
@Node
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Switch {
    @Id
    private String id;
    private String netID;
    private double x;
    private double y;
    private String type="switch";
    private String color="green";

    @Relationship(type = "CONNECTED_TO",direction = Relationship.Direction.OUTGOING)
    private List<Port> ports;

    @Override
    public String toString() {
        return "Switch{" +
                "productID='" + id + '\'' +
                ", netID='" + netID + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", ports=" + ports +
                '}';
    }

}

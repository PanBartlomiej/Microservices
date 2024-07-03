package pl.agh.graf.entites;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;
import java.util.Objects;

/**
 * Klasa reprezentująca encję Port w bazie danych
 * Posiada:
 * portID - id portu
 * medium - medium przekazu sygnału
 * transmissionSpeed - prędkość przekazu transmisji
 * ports - Lista protów podłączonych do tego portu
 */
@Node
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Port {
    @Id
    private String id;
    private double x;
    private double y;
    private Medium medium;
    private int transmissionSpeed;

    private String color="blue";
    private String type="port";

    @Relationship(type = "CONNECTED_TO", direction = Relationship.Direction.OUTGOING)
    @JsonIgnoreProperties("ports")
    private List<Port> ports;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Port port = (Port) o;
        return id.equals(port.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
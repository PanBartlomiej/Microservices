package pl.agh.ewidencja.entites;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Klasa reprezentuje encje urządzenia w bazie danych
 * Posiada:
 * productID - id urządzenia
 * netID - id wewnątrz sieci
 * ports - porty urządzenia
 */

@Document(collection = "Switch")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Switch {
    @Id
    private String id;
    private String netID;

    private List<Port> ports;

}

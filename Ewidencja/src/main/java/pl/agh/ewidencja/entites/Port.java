package pl.agh.ewidencja.entites;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import org.springframework.data.annotation.Id;

/**
 * Klasa reprezentująca encję Port w bazie danych
 * Posiada:
 * portID - id portu
 * medium - medium przekazu sygnału
 * transmissionSpeed - prędkość przekazu transmisji
 * ports - Lista protów podłączonych do tego portu
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Port {
    @Id
    private String id;
    private Medium medium;
    private int transmissionSpeed;
    private List<Port> ports;

}
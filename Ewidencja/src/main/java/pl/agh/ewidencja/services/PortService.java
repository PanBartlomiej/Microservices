package pl.agh.ewidencja.services;

import pl.agh.ewidencja.entites.Port;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.agh.ewidencja.repository.PortRepository;

import java.util.List;
import java.util.Optional;

/**
 * Klasa typu Service. Klasa odowiedzialna za funkcje przetwarzające Porty.
 * Funkcje w tej klasie służą do sprawdzania poprawności argumentów funkcji Controllera Portów
 * oraz obiektów zwracanych przez repozytorium Portów.
 */
@Service
public class PortService {
    @Autowired
    PortRepository repository;
    /**
     * Pobiera obiekt Port na podstawie jego unikalnego identyfikatora.
     *
     * @param Id Unikalny identyfikator Port do pobrania.
     * @return Obiekt Port, jeśli zostanie znaleziony; w przeciwnym razie zwraca null.
     */
    public Port getPortById(String Id){
        Optional<Port> port = repository.findById(Id);
        if (port.isPresent())
            return port.get();
        else return null;
    }


    /**
     * Zapisuje nowy obiekt Port do bazy danych.
     *
     * @param port Obiekt Port do zapisania.
     * @return Obiekt zapisanego Portu, jeśli operacja zakończy się pomyślnie.
     * @throws RuntimeException Jeśli baza danych nie może zapisać tego portu.
     */
    public ResponseEntity<String> save(Port port) {

        if(repository.findById(port.getId()).isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"responseText\":\"Port with this Id already exists in the database\"}");
        }
        if(port.getTransmissionSpeed()<=0 )
            return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"responseText\":\"Port cannot have negative transmission speed\"}");

        Port port1 =  repository.save(port);
        if(port.equals(port1))
            return ResponseEntity.ok("{\"responseText\": \"Port added\"}");
        else throw new RuntimeException("Database can't save this Port");
    }

    /**
     * Pobiera listę wszystkich obiektów Port w bazie danych.
     *
     * @return Lista obiektów Port.
     */
    public List<Port> getAllPorts() {
        List<Port>ports= repository.findAll();
        return  ports;
    }

    /**
     * Usuwa obiekt Port z bazy danych na podstawie jego ID.
     *
     * @param portID ID Portu do usunięcia.
     * @return ResponseEntity z komunikatem sukcesu, jeśli usunięcie jest udane.
     */
    public ResponseEntity<String> removePort(String portID){
        try {

            if(repository.findById(portID).isPresent()) {
                repository.delete(repository.findById(portID).get());
                return ResponseEntity.ok("{\"responseText\": \"Port removed\"}");
            }
            else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"responseText\":\"There is no Port with this ID\"}");
            }
        }catch (NumberFormatException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"responseText\":\"ID should be number\"}");
        }

    }
}

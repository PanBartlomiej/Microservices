package pl.agh.graf.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.agh.graf.entites.Port;
import pl.agh.graf.repository.PortRepository;

import java.util.*;

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
     * Łączy dwa obiekty Port.
     *
     * @param port1 Pierwszy obiekt Port do połączenia.
     * @param port2 Drugi obiekt Port do połączenia.
     * @return ResponseEntity z komunikatem sukcesu, jeśli połączenie jest udane.
     */
    public ResponseEntity<String> connectPorts(Port port1, Port port2) {
        if(Objects.equals(port1.getId(), port2.getId()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"responseText\":\"Port cannot be connected to itself\"}");

        else if(repository.findById(port1.getId()).isPresent() && repository.findById(port2.getId()).isPresent()) {

            if (port1.getPorts().stream().anyMatch(port-> Objects.equals(port.getId(), port2.getId())))
                return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"responseText\":\"Port1 is already connected with Port2\"}");
            if (port2.getPorts().stream().anyMatch(port-> Objects.equals(port.getId(), port1.getId())))
                return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"responseText\":\"Port2 is already connected with Port1\"}");

            repository.connectPorts(port1.getId(), port2.getId());
            return ResponseEntity.ok("{\"responseText\": \"Ports connected successfully\"}");
        }
        else  if(repository.findById(port1.getId()).isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"responseText\":\"Port1 not found\"}");
        else if(repository.findById(port2.getId()).isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"responseText\":\"Port2 not found\"}");

        //logicznie ta sytuacja nie ma prawa zaistnienia:
        return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body("{\"responseText\":\"I am a Teapot\"}");
    }

    /**
     * Rozłącza dwa obiekty Port.
     *
     * @param port1 Pierwszy obiekt Port do rozłączenia.
     * @param port2 Drugi obiekt Port do rozłączenia.
     * @return ResponseEntity z komunikatem sukcesu, jeśli rozłączenie jest udane.
     */
    public ResponseEntity<String> disconnectPorts(String port1, String port2) {
        if(Objects.equals(port1, port2))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"responseText\":\"Port cannot be connected to itself\"}");

        if(repository.findById(port1).isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"responseText\":\"Port1 not found\"}");
        else if(repository.findById(port2).isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"responseText\":\"Port2 not found\"}");


        if(repository.findById(port1).isPresent() && repository.findById(port2).isPresent()) {
        //sprawdzane jest czy oba porty są ze sobą połączone, bo taka jest jedyna możliwość względem logiki
        //łączenie polega na połączeniu portu1 z portem2 oraz portu2 z portem1
        //oba porty muszą być połączone w przeciwnnym razie można porty rozłączyć.
        // czyli jak jest sytuacja że tylko jeden jest podłączony to trzeba odłączyć bo to jest zły stan
            System.out.println("DDDDDDDDDDDDDDDDD:   "+repository.findById(port1).get().getPorts().contains(repository.findById(port2).get()));
            if(repository.findById(port1).get().getPorts().contains(repository.findById(port2).get())
                    || repository.findById(port2).get().getPorts().contains(repository.findById(port1).get()))
            {

                repository.disconnect(port1, port2);
                repository.disconnect(port2, port1);

                return ResponseEntity.ok("{\"responseText\": \"Ports disconnected successfully\"}");
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"responseText\":\"Ports are not connected\"}");
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"responseText\":\"Unexpeted Error\"}");
        }
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

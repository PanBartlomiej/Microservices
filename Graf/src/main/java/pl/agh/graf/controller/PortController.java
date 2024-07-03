package pl.agh.graf.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.agh.graf.entites.Port;
import pl.agh.graf.services.PortService;

import java.util.List;


/**
 * @author Bartłomiej Leśnicki
 * Klasa typu Controller. PortController udostępnia endpointy do:
 * dodawania, pobierania, zmieniania i usuwania portów.
 * Zmiana portów odbywa się wyłącznie w zakresie łączenia i rozłączania portów.
 * Endpointy znajdyują się pod adresem /api/port
 */
@RestController
@RequestMapping("/api/port")
//@CrossOrigin
public class PortController {

    @Autowired
    PortService service;

    /**
     * metoda zwracająca port o zadanym Id
     * @param Id Id szukanego portu
     * @return zwraca port o zadanym id
     */
    @GetMapping("GetOne/{Id}")
    public Port getPortById(@PathVariable String Id){
        return service.getPortById(Id);
    }

    /**
     * metoda zwraca wszystkie porty dostępne w bazie danych
     * @return
     */
    @GetMapping("GetAll")
    public List<Port> getAllPorts(HttpServletRequest request) {
        return service.getAllPorts();
    }

    /**
     * metoda służy do dodawania portów do bazy danych
     * @param port
     * @return zwraca kod http wraz z odopwiednim komunikatem
     */
    @PostMapping("Add")
    public ResponseEntity<String> addPort(@RequestBody Port port){
        return service.save(port);
    }

    /**
     * metoda służy do łączenia portów między sobą. Przyjmuje jako parametr lister portów.
     * I łączy dwa pierwsze porty w liście ze sobą.
     * @param ports
     * @return zwraca kod http wraz z odopwiednim komunikatem
     */
    @PostMapping("connect")
    public ResponseEntity<String> connectPorts(@RequestBody List<Port> ports ){
        return service.connectPorts(ports.get(0),ports.get(1));
    }

    /**
     * metoda służy do rozłączania dwóch portów.
     * Jako argumenty przyjmuje dwa porty które mają być rozłączone.
     * @param port1
     * @param port2
     * @return zwraca kod http wraz z odopwiednim komunikatem
     */
    @DeleteMapping("disconnect/{port1}/{port2}")
    public ResponseEntity<String> disconnectPorts(@PathVariable String port1,@PathVariable String port2) {
        return service.disconnectPorts(port1,port2);
    }

    /**
     * metoda do usuwania portu
     * @param portID Id portu do usunięcia.
     * @return zwraca kod http wraz z odopwiednim komunikatem
     */
    @DeleteMapping("{portID}")
    public ResponseEntity<String> removePort(@PathVariable String portID){
        return service.removePort(portID);
    }
}

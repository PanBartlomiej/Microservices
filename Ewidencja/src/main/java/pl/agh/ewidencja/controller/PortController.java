package pl.agh.ewidencja.controller;

import pl.agh.ewidencja.entites.Port;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.agh.ewidencja.services.PortService;

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
     * metoda do usuwania portu
     * @param portID Id portu do usunięcia.
     * @return zwraca kod http wraz z odopwiednim komunikatem
     */
    @DeleteMapping("{portID}")
    public ResponseEntity<String> removePort(@PathVariable String portID){
        return service.removePort(portID);
    }
}

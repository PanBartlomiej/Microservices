package pl.agh.ewidencja.controller;


import pl.agh.ewidencja.entites.Switch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.agh.ewidencja.services.SwitchService;

import java.util.List;

/**
 * Klasa typu Controller. Obsługuje endpointy http odopowiedzialne za:
 * pobieranie, dodawanie, usuwanie i omdyfikacje urządzeń.
 */
@RestController
@RequestMapping("/api/switch/")
//@CrossOrigin
public class SwitchController {

    @Autowired
    SwitchService service;

    /**
     * metoda zwraca urządzenie o zadanym ID
     * @param Id
     * @return
     */
    @GetMapping("GetOne/{Id}")
    public Switch getSwitchByID(@PathVariable String Id){
        return service.getSwitchByID(Id);
    }

//    @GetMapping("GetSwitchAndNeighbours/{productID}")
//    public Map<String, HashSet<Object>> getSwitchAndNeighbours(@PathVariable String productID) { return  service.getSwitchAndNeighbours(productID);}
//
//    @GetMapping("GetSwitchWithCOnnectedPorts")
//    public Map<String, HashSet<Object>> getAllSwitchesWithConnectedPorts() { return  service.getAllSwitchesWithConnectedPorts();}

    /**
     * Metoda zwraca Liste wszystkich urządzeń
     * @return
     */
    @GetMapping("GetAll")
    public List<Switch> getAllSwitches(){
        return service.getAllSwitches();
    }

    /**
     * Metoda dodaje urządzenie do bazy danych
     * @param aSwitch
     * @return
     */
    @PostMapping("add")
    public ResponseEntity<String> addSwitch(@RequestBody Switch aSwitch){
        return service.addSwitch(aSwitch);
    }

    /**
     * metoda usuwa urządzenie o zadanym ID
     * @param productID
     * @return
     */
    @DeleteMapping("{productID}")
    public ResponseEntity<String> deleteSwitchByID(@PathVariable String productID){
        return service.deleteSwitch(productID);
    }

    /**
     * Metoda modyfikuje urządzenie o zadanym ID
     * @param aSwitch
     * @return
     */
    @PutMapping("{productID}")
    public ResponseEntity<String> updateSwitchByID(@PathVariable("productID")String productID, @RequestBody Switch aSwitch){
        return service.updateSwitch(productID, aSwitch);
    }
//    @PutMapping("updatePosition/{productID}")
//    public ResponseEntity<String> updateSwitchPosition(@PathVariable("productID")String productID, @RequestBody List<Switch> switches){
//        return service.updateSwitchesPostions(switches);
//    }
}

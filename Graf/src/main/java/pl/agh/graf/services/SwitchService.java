package pl.agh.graf.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.agh.graf.entites.Port;
import pl.agh.graf.entites.Switch;
import pl.agh.graf.repository.SwitchRepository;

import java.util.*;

/**
 * Klasa typu Service. Klasa odowiedzialna za funckje przetwarzające Urządzenia.
 * Funkcje w tej klasie służą do sprawdzania poprawności argumentów funkcji Controllera Urządzeń
 * oraz obiektów zwracanych przez repozytorium Urządzeń.
 */
@Service
public class SwitchService {
    @Autowired
    SwitchRepository switchRepository;
    @Autowired
    PortService portService;

    /**
     * Pobiera obiekt Switch na podstawie jego unikalnego identyfikatora.
     *
     * @param Id Unikalny identyfikator Switch do pobrania.
     * @return Obiekt Switch, jeśli zostanie znaleziony.
     * @throws NoSuchElementException Jeśli nie zostanie znaleziony Switch o określonym ID.
     */
    public Switch getSwitchByID(String Id){
        Optional<Switch> optionalSwitch = switchRepository.findById(Id);
        if(optionalSwitch.isPresent()) {
            return optionalSwitch.get();
        }
        else return null;
    }

    /**
     * Pobiera listę wszystkich obiektów Switch w bazie danych.
     *
     * @return Lista obiektów Switch, jeśli są dostępne.
     * @throws NoSuchElementException Jeśli nie zostaną znalezione żadne obiekty Switch w bazie danych.
     */
    public List<Switch> getAllSwitches(){
        List<Switch> switches = switchRepository.findAll();
        return  switches;

    }


    /**
     * Dodaje nowy obiekt Switch do bazy danych.
     *
     * @param aSwitch Obiekt Switch do dodania.
     * @return ResponseEntity z komunikatem sukcesu, jeśli operacja zakończy się pomyślnie.
     * @throws RuntimeException Jeśli baza danych nie może zapisać tego produktu.
     */
    public ResponseEntity<String> addSwitch(Switch aSwitch){
        if(switchRepository.findById(aSwitch.getId()).isPresent())
            return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"responseText\":\"Product with Id= "+aSwitch.getId() +" already exists in the database\"}");

        List<Port> ports =aSwitch.getPorts();
        for(Port port:ports){
            if(portService.getPortById(port.getId())!= null)
                return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"responseText\":\"Port with  Id"+port.getId() +" is assign to another device\"}");
        }
        Switch switchTmp = switchRepository.save(aSwitch);
        if((switchTmp != null )&&(switchTmp.equals(aSwitch)))
            return ResponseEntity.ok("{\"responseText\": \"Product added\"}");
        else throw new RuntimeException("Database can't save this product");
    }

    /**
     * Usuwa obiekt urządzenia z bazy danych na podstawie jego ID produktu.
     *
     * @param productID ID produktu urządzenia do usunięcia.
     * @return ResponseEntity z komunikatem sukcesu, jeśli usunięcie jest udane.
     */
    public ResponseEntity<String> deleteSwitch(String productID) {
        if(switchRepository.findById(productID).isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"responseText\":\"Product not found\"}");
        }
        else {
            for(Port port:switchRepository.findById(productID).get().getPorts())
            {
                portService.removePort(port.getId());
            }
            switchRepository.delete(switchRepository.findById(productID).get());
            return ResponseEntity.ok("{\"responseText\": \"Product deleted successfully\"}");
        }
    }


    public Optional<Switch> updateDevice(String id, Switch newDeviceData) {
        return switchRepository.findById(id)
                .map(device -> {
                    device.setId(newDeviceData.getId());
                    device.setNetID(newDeviceData.getNetID());
                    device.setPorts(newDeviceData.getPorts());
                    return switchRepository.save(device);
                });
    }
    /**
     * Aktualizuje istniejący obiekt Urządzenia w bazie danych.
     *
     * @param aSwitch Obiekt urządzenia z zaktualizowanymi informacjami.
     * @return ResponseEntity z komunikatem sukcesu, jeśli aktualizacja jest udana.
     */
    public ResponseEntity<String> updateSwitch(String productID, Switch aSwitch) {
        if(switchRepository.findById(aSwitch.getId()).isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"responseText\":\"Product not found\"}");
        }
        else {
            if(this.updateDevice(productID,aSwitch).isPresent())
                return ResponseEntity.ok("{\"responseText\": \"Product updated successfully\"}");
            else
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("{\"responseText\":\"Server can't update product\"}");
        }
    }
}
package pl.agh.ewidencja.services;

import pl.agh.ewidencja.entites.Port;
import pl.agh.ewidencja.entites.Switch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.agh.ewidencja.repository.SwitchRepository;

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
        else throw new NoSuchElementException("There is no Switch with ID="+Id);
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
            return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"responseText\":\"Product with this Id already exists in the database\"}");

        List<Port> ports =aSwitch.getPorts();
        for(Port port:ports){
            if(portService.getPortById(port.getId())!= null)
                return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"responseText\":\"Port with this Id is assign to another Switch\"}");
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
//
//    @Value("${spring.neo4j.authentication.password}")
//    String password;
//    @Value("${spring.neo4j.authentication.username}")
//    String username;
//
//    public Map<String,HashSet<Object>> getAllSwitchesWithConnectedPorts(){
//        Driver driver = GraphDatabase
//                .driver("neo4j://localhost:7687", AuthTokens.basic(username, password));
//
//        Neo4jClient client = Neo4jClient.create(driver);
//        List<Map<String, Object>> lista = client.query(
//                        "MATCH (s1:Switch )-[:CONNECTED_TO]-(p1:Port)\n" +
//                                "MATCH (p1)-[:CONNECTED_TO]-(p2:Port)\n" +
//                                "MATCH (p2)-[]-(s2:Switch)\n" +
//                                "RETURN  p1,p2,s1,s2;")
//                        .fetch().all().stream().toList();
//        HashMap<String,Switch> switches = new HashMap<>();
//        HashMap<Integer,Port> ports = new HashMap<>();
//        for(int i=0; i<lista.size();i++){
//            InternalNode p1 = (InternalNode) ( lista.get(i).get("p1"));
//            InternalNode p2 = (InternalNode) ( lista.get(i).get("p2"));
//            InternalNode s1 = (InternalNode) ( lista.get(i).get("s1"));
//            InternalNode s2 = (InternalNode) ( lista.get(i).get("s2"));
//            switches.put(s1.get("ID").asString(),this.getSwitchByID(s1.get("ID").asString()));
//            switches.put(s2.get("ID").asString(),this.getSwitchByID(s2.get("ID").asString()));
//            ports.put(p1.get("ID").asInt(), portService.getPortById(p1.get("ID").asString()));
//            ports.put(p2.get("ID").asInt(), portService.getPortById(p2.get("ID").asString()));
//        }
//        HashSet<Object> objects1 =  new HashSet<>(switches.values());
//        HashSet<Object> objects2 =  new HashSet<>(ports.values());
//        Map<String,HashSet<Object>> results = new HashMap<>();
//        results.put("Switches",objects1);
//        results.put("Ports",objects2);
//        return results;
//    }
//    public Map<String,HashSet<Object>> getSwitchAndNeighbours(String productID) {
//        Driver driver = GraphDatabase
//                .driver("neo4j://localhost:7687", AuthTokens.basic(username, password));
//
//        Neo4jClient client = Neo4jClient.create(driver);
//        List<Map<String, Object>> lista = client.query(
//                "MATCH (s1:Switch {ID: $productID})-[:CONNECTED_TO]-(p1:Port)\n" +
//                        "MATCH (p1)-[:CONNECTED_TO]-(p2:Port)\n" +
//                        "MATCH (p2)-[]-(s2:Switch)\n" + //jak chcemy wszystkich sąsiadów a nie tylko sąsiadów typu switch
//                        "RETURN  p1,p2,s1,s2;")         // to wtedy trzeba wywalić to Switch obok s2: i zostawić samo s2
//                .bind(productID).to("productID").
//                fetch().all().stream().toList();
//        HashMap<String,Switch> switches = new HashMap<>();
//        HashMap<Integer,Port> ports = new HashMap<>();
//        for(int i=0; i<lista.size();i++){
//            InternalNode p1 = (InternalNode) ( lista.get(i).get("p1"));
//            InternalNode p2 = (InternalNode) ( lista.get(i).get("p2"));
//            InternalNode s1 = (InternalNode) ( lista.get(i).get("s1"));
//            InternalNode s2 = (InternalNode) ( lista.get(i).get("s2"));
//            switches.put(s1.get("ID").asString(),this.getSwitchByID(s1.get("ID").asString()));
//            switches.put(s2.get("ID").asString(),this.getSwitchByID(s2.get("ID").asString()));
//            ports.put(p1.get("ID").asInt(), portService.getPortById(p1.get("ID").asString()));
//            ports.put(p2.get("ID").asInt(), portService.getPortById(p2.get("ID").asString()));
//      }
//        HashSet<Object> objects1 =  new HashSet<>(switches.values());
//        HashSet<Object> objects2 =  new HashSet<>(ports.values());
//        Map<String,HashSet<Object>> results = new HashMap<>();
//        results.put("Switches",objects1);
//        results.put("Ports",objects2);
//        return results;
//    }
//
//    public ResponseEntity<String> updateSwitchesPostions(List<Switch> switches) {
//        Driver driver = GraphDatabase
//                .driver("neo4j://localhost:7687", AuthTokens.basic(username, password));
//
//        Neo4jClient client = Neo4jClient.create(driver);
//        ArrayList<String> ids= new ArrayList<>();
//        ArrayList<Double> x = new ArrayList<>();
//        ArrayList<Double> y = new ArrayList<>();
//        ArrayList<Map<String,Object>> data = new ArrayList<>();
//        for(int i=0; i< switches.size(); i++){
//            HashMap<String,Object> map= new HashMap<>();
//            map.put("id",switches.get(i).getId());
//            map.put("x",switches.get(i).getX());
//            map.put("y",switches.get(i).getY());
//            data.add(map);
//        }
//
//
//        List<Map<String, Object>> lista = client.query(
//                        "UNWIND $data AS switch\n" +
//                                "MATCH (s {id: switch.id})\n" +
//                                "SET s.x = switch.x, s.y = switch.y")         // to wtedy trzeba wywalić to Switch obok s2: i zostawić samo s2
//                .bind(data).to("data").
//                fetch().all().stream().toList();
//        for(int i=0; i<lista.size();i++)
//                System.out.println(lista.get(i).keySet());
//        return ResponseEntity.ok(lista.toString());
//    }
}
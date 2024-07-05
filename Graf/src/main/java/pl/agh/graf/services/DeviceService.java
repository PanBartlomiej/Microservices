package pl.agh.graf.services;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.internal.InternalNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.agh.graf.entites.*;

import java.util.*;
@Service
public class DeviceService {

    @Value("${spring.neo4j.authentication.password}")
    String password;
    @Value("${spring.neo4j.authentication.username}")
    String username;
    @Value("${spring.neo4j.hostname}")
    String hostname;

    @Autowired
    SwitchService switchService;
    @Autowired
    ServerService serverService;
    @Autowired
    RouterService routerService;
    @Autowired
    ComputerService computerService;
    @Autowired
    AccessPointService accessPointService;
    @Autowired
    PortService portService;

    public List<Object> getDeviceByID(String productID) {

        List<Object> list = new ArrayList<>();

        Object object;
        try {
            object = switchService.getSwitchByID(productID);
            if (object != null) {
                list.add(object);
                return list;
            }
        }catch (Exception e) {System.out.println(e.getMessage());}
        try {
            object = serverService.getServerByID(productID);
            if (object != null) {
                list.add(object);
                return list;
            }
        }catch (Exception e) {System.out.println(e.getMessage());}
        try {
            object = routerService.getRouterByID(productID);
            if (object != null) {
                list.add(object);
                return list;
            }
        }catch (Exception e) {System.out.println(e.getMessage());}

        try {
            object = computerService.getComputerByID(productID);
            if (object != null) {
                list.add(object);
                return list;
            }
        }catch (Exception e) {System.out.println(e.getMessage());}

        try {
            object = accessPointService.getAccessPointByID(productID);
            if (object != null) {
                list.add(object);
                return list;
            }
        }catch (Exception e) {System.out.println(e.getMessage());}

        return list;
    }

    public List<Object> getAllDevices() {
        List<Object> list = new ArrayList<>();
        List<Switch> switchList = switchService.getAllSwitches();
        List<Server> serverList = serverService.getAllServers();
        List<Router> routerList = routerService.getAllRouters();
        List<Computer> computerList = computerService.getAllComputers();
        List<AccessPoint> accessPointList = accessPointService.getAllAccessPoints();

        for ( Switch switchObj : switchList ) {
            list.add(switchObj);
        }
        for ( Server serverObj : serverList ) {
            list.add(serverObj);
        }
        for ( Router routerObj : routerList ) {
            list.add(routerObj);
        }
        for ( Computer computerObj : computerList ) {
            list.add(computerObj);
        }
        for ( AccessPoint accessPointObj : accessPointList ) {
            list.add(accessPointObj);
        }
        return list;
    }

    public Map<String, HashSet<Object>> getAllDevicesWithConnectedPorts(){
        Driver driver = GraphDatabase
                .driver("neo4j://"+hostname, AuthTokens.basic(username, password));

        Neo4jClient client = Neo4jClient.create(driver);
        List<Map<String, Object>> lista = client.query(
                        "MATCH (s1 )-[:CONNECTED_TO]-(p1:Port)\n" +
                                "MATCH (p1)-[:CONNECTED_TO]-(p2:Port)\n" +
                                "MATCH (p2)-[]-(s2)\n" +
                                "WHERE NOT s2:Port\n"+
                                "RETURN  p1,p2,s1,s2;")
                .fetch().all().stream().toList();
        HashMap<String, Object> products = new HashMap<>();
        HashMap<String, Port> ports = new HashMap<>();
        for(int i=0; i<lista.size();i++){
            InternalNode p1 = (InternalNode) (lista.get(i).get("p1"));
            InternalNode p2 = (InternalNode) (lista.get(i).get("p2"));
            InternalNode s1 = (InternalNode) (lista.get(i).get("s1"));
            InternalNode s2 = (InternalNode) (lista.get(i).get("s2"));

            if (s1 != null && s1.containsKey("id")) {
                products.put(s1.get("id").asString(), this.getDeviceByID(s1.get("id").asString()).get(0));
            }

            if (s2 != null && s2.containsKey("id")) {
                products.put(s2.get("id").asString(), this.getDeviceByID(s2.get("id").asString()).get(0));
            }

            if (p1 != null && p1.containsKey("id")) {
                ports.put(p1.get("id").asString(), portService.getPortById(p1.get("id").asString()));
            }

            if (p2 != null && p2.containsKey("id")) {
                ports.put(p2.get("id").asString(), portService.getPortById(p2.get("id").asString()));
            }
        }
        HashSet<Object> objects1 =  new HashSet<>(products.values());
        HashSet<Object> objects2 =  new HashSet<>(ports.values());
        Map<String,HashSet<Object>> results = new HashMap<>();
        results.put("Switches",objects1);
        results.put("Ports",objects2);
        return results;
    }
    public Map<String,HashSet<Object>> getDeviceAndNeighbours(String productID) {
        Driver driver = GraphDatabase
                .driver("neo4j://"+hostname, AuthTokens.basic(username, password));

        Neo4jClient client = Neo4jClient.create(driver);
        List<Map<String, Object>> lista = client.query(
                        "MATCH (s1 {id: $productID})-[:CONNECTED_TO]-(p1:Port)\n" +
                                "OPTIONAL MATCH (p1)-[:CONNECTED_TO]-(p2:Port)\n" + //tutaj ten optional dla portów jak wywalimy
                                // to pokaże nam tylko porty połączone.
                                //Optional dla ostatniego musi być bo mogą być urządzenia nie podłączone np urządzenie i porty niepodłączone do niczeogo
                                "OPTIONAL MATCH (p2)-[]-(s2:!Port)\n" + //jak chcemy wszystkich sąsiadów a nie tylko sąsiadów typu switch
                                "WHERE NOT s2:Port\n"+
                                "RETURN  p1,p2,s1,s2;")         // to wtedy trzeba wywalić to Switch obok s2: i zostawić samo s2
                .bind(productID).to("productID").
                fetch().all().stream().toList();
        HashMap<String,Object> products = new HashMap<>();
        HashMap<String,Port> ports = new HashMap<>();
        for(int i=0; i<lista.size();i++){
            InternalNode p1 = (InternalNode) (lista.get(i).get("p1"));
            InternalNode p2 = (InternalNode) (lista.get(i).get("p2"));
            InternalNode s1 = (InternalNode) (lista.get(i).get("s1"));
            InternalNode s2 = (InternalNode) (lista.get(i).get("s2"));

            if (s1 != null && s1.containsKey("id")) {
                products.put(s1.get("id").asString(), this.getDeviceByID(s1.get("id").asString()).get(0));
            }

            if (s2 != null && s2.containsKey("id")) {
                products.put(s2.get("id").asString(), this.getDeviceByID(s2.get("id").asString()).get(0));
            }

            if (p1 != null && p1.containsKey("id")) {
                ports.put(p1.get("id").asString(), portService.getPortById(p1.get("id").asString()));
            }

            if (p2 != null && p2.containsKey("id")) {
                ports.put(p2.get("id").asString(), portService.getPortById(p2.get("id").asString()));
            }
        }
        HashSet<Object> objects1 =  new HashSet<>(products.values());
        HashSet<Object> objects2 =  new HashSet<>(ports.values());
        Map<String,HashSet<Object>> results = new HashMap<>();
        results.put("Devices",objects1);
        results.put("Ports",objects2);
        return results;
    }

    public ResponseEntity<String> updateDevicesPositions(List<Switch> products) {
        Driver driver = GraphDatabase
                .driver("neo4j://"+hostname, AuthTokens.basic(username, password));

        Neo4jClient client = Neo4jClient.create(driver);
        ArrayList<String> ids= new ArrayList<>();
        ArrayList<Double> x = new ArrayList<>();
        ArrayList<Double> y = new ArrayList<>();
        ArrayList<Map<String,Object>> data = new ArrayList<>();
        for(int i=0; i< products.size(); i++){
            HashMap<String,Object> map= new HashMap<>();
            map.put("id",products.get(i).getId());
            map.put("x",products.get(i).getX());
            map.put("y",products.get(i).getY());
            data.add(map);
        }


        List<Map<String, Object>> lista = client.query(
                        "UNWIND $data AS device\n" +
                                "MATCH (s {id: device.id})\n" +
                                "SET s.x = device.x, s.y = device.y")   // to wtedy trzeba wywalić to Switch obok s2:
                                                                        // i zostawić samo s2
                .bind(data).to("data").
                fetch().all().stream().toList();
        for(int i=0; i<lista.size();i++)
            System.out.println(lista.get(i).keySet());
        return ResponseEntity.ok(lista.toString());
    }


    public ResponseEntity<String> deleteDeviceByID(String productID) {
        return new ResponseEntity<>(HttpStatusCode.valueOf(200));
    }
}

package pl.agh.graf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.agh.graf.entites.Switch;
import pl.agh.graf.services.DeviceService;

import java.util.HashSet;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/all/")
public class AllDeviceController {


    @Autowired
    DeviceService deviceService;

    @GetMapping("GetOne/{productID}")
    public List<Object> getDeviceByID(@PathVariable() String productID) throws IllegalAccessException {
       return deviceService.getDeviceByID(productID);
    }

    @GetMapping("GetAll")
    public List<Object> getAllDevices() throws IllegalAccessException {
        return deviceService.getAllDevices();
    }
    // Jako listy urządzeń przyjmujemy Switch, ponieważ każde urządzenie posiada takie same pola,
    @PutMapping("updatePosition/{productID}")
    public ResponseEntity<String> updateDevicesPosition(@PathVariable("productID")String productID, @RequestBody List<Switch> switches){
        return deviceService.updateDevicesPositions(switches);
    }

    @GetMapping("GetDeviceAndNeighbours/{productID}")
    public Map<String, HashSet<Object>> getDeviceAndNeighbours(@PathVariable String productID) { return  deviceService.getDeviceAndNeighbours(productID);}

    @GetMapping("GetDevicesWithConnectedPorts")
    public Map<String, HashSet<Object>> getAllSwitchesWithConnectedPorts() { return  deviceService.getAllDevicesWithConnectedPorts();}

    @DeleteMapping("{productID}")
    public ResponseEntity<String> deleteDeviceByID(@PathVariable("productID")String productID) {
        return deviceService.deleteDeviceByID(productID);
    }
}

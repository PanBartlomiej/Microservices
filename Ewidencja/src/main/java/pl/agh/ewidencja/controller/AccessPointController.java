package pl.agh.ewidencja.controller;

import pl.agh.ewidencja.entites.AccessPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.agh.ewidencja.services.AccessPointService;

import java.util.List;

@RestController
@RequestMapping("/api/accesspoint/")
public class AccessPointController {
    final
    AccessPointService service;

    public AccessPointController(AccessPointService service) {
        this.service = service;
    }

    @GetMapping("GetOne/{Id}")
    public AccessPoint getServerByID(@PathVariable String Id){
        return service.getAccessPointByID(Id);
    }

    @GetMapping("GetAll")
    public List<AccessPoint> getAllServers(){
        return service.getAllAccessPoints();
    }
    @PostMapping("add")
    public ResponseEntity<String> addComputer(@RequestBody AccessPoint accessPoint){
        return service.addAccessPoint(accessPoint);
    }
    @DeleteMapping("{productID}")
    public ResponseEntity<String> deleteComputerByID(@PathVariable String productID){
        return service.deleteAccessPoint(productID);
    }
    @PutMapping("{productID}")
    public ResponseEntity<String> updateAccessPointByID(@PathVariable("productID")String productID,@RequestBody AccessPoint accessPoint){
        return service.updateAccessPoint(productID,accessPoint);
    }
}

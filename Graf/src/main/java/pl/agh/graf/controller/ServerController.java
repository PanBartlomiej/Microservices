package pl.agh.graf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.agh.graf.entites.Server;
import pl.agh.graf.services.ServerService;

import java.util.List;

@RestController
@RequestMapping("/api/server/")
public class ServerController {
    @Autowired
    ServerService service;
    @GetMapping("GetOne/{Id}")
    public Server getServerByID(@PathVariable String Id){
        return service.getServerByID(Id);
    }

    @GetMapping("GetAll")
    public List<Server> getAllServers(){
        return service.getAllServers();
    }
    @PostMapping("add")
    public ResponseEntity<String> addServer(@RequestBody Server server){
        return service.addServer(server);
    }
    @DeleteMapping("{productID}")
    public ResponseEntity<String> deleteServerByID(@PathVariable String productID){
        return service.deleteServer(productID);
    }
    @PutMapping("{productID}")
    public ResponseEntity<String> updateServerByID(@PathVariable("productID")String productID,@RequestBody Server server){
        return service.updateServer(productID,server);
    }
}

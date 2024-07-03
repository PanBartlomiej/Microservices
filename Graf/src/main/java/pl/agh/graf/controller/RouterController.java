package pl.agh.graf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.agh.graf.entites.Router;
import pl.agh.graf.services.RouterService;

import java.util.List;

@RestController
@RequestMapping("/api/router/")
public class RouterController {
    @Autowired
    RouterService service;
    @GetMapping("GetOne/{Id}")
    public Router getServerByID(@PathVariable String Id){
        return service.getRouterByID(Id);
    }

    @GetMapping("GetAll")
    public List<Router> getAllServers(){
        return service.getAllRouters();
    }
    @PostMapping("add")
    public ResponseEntity<String> addRouter(@RequestBody Router router){
        return service.addRouter(router);
    }
    @DeleteMapping("{productID}")
    public ResponseEntity<String> deleteServerByID(@PathVariable String productID){
        return service.deleteRouter(productID);
    }
    @PutMapping("{productID}")
    public ResponseEntity<String> updateRouterByID(@PathVariable("productID")String productID,@RequestBody Router router){
        return service.updateRouter(productID,router);
    }
}

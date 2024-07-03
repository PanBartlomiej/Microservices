package pl.agh.graf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.agh.graf.entites.Computer;
import pl.agh.graf.services.ComputerService;

import java.util.List;

@RestController
@RequestMapping("/api/computer/")
public class ComputerController {
    @Autowired
    ComputerService service;
    @GetMapping("GetOne/{Id}")
    public Computer getServerByID(@PathVariable String Id){
        return service.getComputerByID(Id);
    }

    @GetMapping("GetAll")
    public List<Computer> getAllServers(){
        return service.getAllComputers();
    }
    @PostMapping("add")
    public ResponseEntity<String> addComputer(@RequestBody Computer computer){
        return service.addComputer(computer);
    }
    @DeleteMapping("{productID}")
    public ResponseEntity<String> deleteComputerByID(@PathVariable String productID){
        return service.deleteComputer(productID);
    }
    @PutMapping("{productID}")
    public ResponseEntity<String> updateComputerByID(@PathVariable("productID")String productID,@RequestBody Computer computer){
        return service.updateComputer(productID,computer);
    }
}

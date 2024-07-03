package pl.agh.ewidencja.services;

import pl.agh.ewidencja.entites.Computer;
import pl.agh.ewidencja.entites.Port;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.agh.ewidencja.entites.Router;
import pl.agh.ewidencja.repository.ComputerRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ComputerService {
    @Autowired
    ComputerRepository computerRepository;
    @Autowired
    PortService portService;
    public Computer getComputerByID(String id) {
        Optional<Computer> optional = computerRepository.findById(id);
        if(optional.isPresent()) {
            return optional.get();
        }
        else return null;
    }

    public List<Computer> getAllComputers() {
        return computerRepository.findAll();
    }

    public ResponseEntity<String> addComputer(Computer computer){
        if(computerRepository.findById(computer.getId()).isPresent())
            return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"responseText\":\"Product with this Id already exists in the database\"}");

        List<Port> ports =computer.getPorts();
        for(Port port:ports){
            if(portService.getPortById(port.getId())!= null)
                return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"responseText\":\"Port with this Id is assign to another Product\"}");
        }
        Computer computer1 = computerRepository.save(computer);
        if((computer1 != null )&&(computer1.equals(computer)))
            return ResponseEntity.ok("{\"responseText\": \"Product added\"}");
        else throw new RuntimeException("Database can't save this product");
    }

    public ResponseEntity<String> deleteComputer(String productID) {
        if(computerRepository.findById(productID).isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"responseText\":\"Product not found\"}");
        }
        else {
            for(Port port: computerRepository.findById(productID).get().getPorts())
            {
                portService.removePort(port.getId());
            }
            computerRepository.delete(computerRepository.findById(productID).get());
            return ResponseEntity.ok("{\"responseText\": \"Product deleted successfully\"}");
        }
    }

    public Optional<Computer> updateDevice(String id, Computer newDeviceData) {
        return computerRepository.findById(id)
                .map(device -> {
                    device.setId(newDeviceData.getId());
                    device.setNetID(newDeviceData.getNetID());
                    device.setPorts(newDeviceData.getPorts());
                    return computerRepository.save(device);
                });
    }
    public ResponseEntity<String> updateComputer(String productID,Computer computer) {
        if(computerRepository.findById(computer.getId()).isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"responseText\":\"Product not found\"}");
        }
        else {
            if(this.updateDevice(productID,computer).isPresent())
                return ResponseEntity.ok("{\"responseText\": \"Product updated successfully\"}");
            else
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("{\"responseText\":\"Can't update product\"}");
        }
    }
}

package pl.agh.ewidencja.services;

import pl.agh.ewidencja.entites.Port;
import pl.agh.ewidencja.entites.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.agh.ewidencja.repository.ServerRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ServerService {
    @Autowired
    ServerRepository serverRepository;
    @Autowired
    PortService portService;
    public Server getServerByID(String id) {
        Optional<Server> optional = serverRepository.findById(id);
        if(optional.isPresent()) {
            return optional.get();
        }
        else return null;
    }

    public List<Server> getAllServers() {
        return serverRepository.findAll();
    }

    public ResponseEntity<String> addServer(Server server){
        if(serverRepository.findById(server.getId()).isPresent())
            return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"responseText\":\"Product with this Id already exists in the database\"}");

        List<Port> ports =server.getPorts();
        for(Port port:ports){
            if(portService.getPortById(port.getId())!= null)
                return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"responseText\":\"Port with this Id is assign to another Product\"}");
        }
        Server serverTmp = serverRepository.save(server);
        if((serverTmp != null )&&(serverTmp.equals(server)))
            return ResponseEntity.ok("{\"responseText\": \"Product added\"}");
        else throw new RuntimeException("Database can't save this product");
    }

    public ResponseEntity<String> deleteServer(String productID) {
        if(serverRepository.findById(productID).isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"responseText\":\"Product not found\"}");
        }
        else {
            for(Port port:serverRepository.findById(productID).get().getPorts())
            {
                portService.removePort(port.getId());
            }
            serverRepository.delete(serverRepository.findById(productID).get());
            return ResponseEntity.ok("{\"responseText\": \"Product deleted successfully\"}");
        }
    }

    public Optional<Server> updateDevice(String id, Server newDeviceData) {
        return serverRepository.findById(id)
                .map(device -> {
                    device.setId(newDeviceData.getId());
                    device.setNetID(newDeviceData.getNetID());
                    device.setPorts(newDeviceData.getPorts());
                    return serverRepository.save(device);
                });
    }
    public ResponseEntity<String> updateServer(String productID,Server server) {
        if(serverRepository.findById(server.getId()).isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"responseText\":\"Product not found\"}");
        }
        else {
            if(this.updateDevice(productID,server).isPresent())
                return ResponseEntity.ok("{\"responseText\": \"Product updated successfully\"}");
            else
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("{\"responseText\":\"Server can't update product\"}");
        }
    }
}

package pl.agh.graf.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.agh.graf.entites.Port;
import pl.agh.graf.entites.Router;
import pl.agh.graf.repository.RouterRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RouterService {
    @Autowired
    RouterRepository routerRepository;
    @Autowired
    PortService portService;
    public Router getRouterByID(String id) {
        Optional<Router> optional = routerRepository.findById(id);
        if(optional.isPresent()) {
            return optional.get();
        }
        else return null;
    }

    public List<Router> getAllRouters() {
        return routerRepository.findAll();
    }

    public ResponseEntity<String> addRouter(Router router){
        if(routerRepository.findById(router.getId()).isPresent())
            return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"responseText\":\"Product with Id= "+router.getId()+" already exists in the database\"}");

        List<Port> ports =router.getPorts();
        for(Port port:ports){
            if(portService.getPortById(port.getId())!= null)
                return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"responseText\":\"Port with  Id"+port.getId() +" is assign to another device\"}");
        }
        Router router1 = routerRepository.save(router);
        if((router1 != null )&&(router1.equals(router)))
            return ResponseEntity.ok("{\"responseText\": \"Product added\"}");
        else throw new RuntimeException("Database can't save this product");
    }

    public ResponseEntity<String> deleteRouter(String productID) {
        if(routerRepository.findById(productID).isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"responseText\":\"Product not found\"}");
        }
        else {
            for(Port port:routerRepository.findById(productID).get().getPorts())
            {
                portService.removePort(port.getId());
            }
            routerRepository.delete(routerRepository.findById(productID).get());
            return ResponseEntity.ok("{\"responseText\": \"Product deleted successfully\"}");
        }
    }
    public Optional<Router> updateDevice(String id, Router newDeviceData) {
        return routerRepository.findById(id)
                .map(device -> {
                    device.setId(newDeviceData.getId());
                    device.setNetID(newDeviceData.getNetID());
                    device.setPorts(newDeviceData.getPorts());
                    return routerRepository.save(device);
                });
    }
    public ResponseEntity<String> updateRouter(String productID,Router router) {
        if(routerRepository.findById(router.getId()).isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"responseText\":\"Product not found\"}");
        }
        else {
            if(this.updateDevice(productID, router).isPresent())
                return ResponseEntity.ok("{\"responseText\": \"Product updated successfully\"}");
            else
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("{\"responseText\":\"Server can't update product\"}");
        }
    }

}

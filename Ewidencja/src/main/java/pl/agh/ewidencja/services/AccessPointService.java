package pl.agh.ewidencja.services;

import pl.agh.ewidencja.entites.AccessPoint;
import pl.agh.ewidencja.entites.Port;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.agh.ewidencja.entites.Router;
import pl.agh.ewidencja.repository.AccessPointRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class AccessPointService {

    @Autowired
    AccessPointRepository accessPointRepository;
    @Autowired
    PortService portService;
    public AccessPoint getAccessPointByID(String id) {
        Optional<AccessPoint> optional = accessPointRepository.findById(id);
        if(optional.isPresent()) {
            return optional.get();
        }
        else return null;
    }

    public List<AccessPoint> getAllAccessPoints() {
        return accessPointRepository.findAll();
    }

    public ResponseEntity<String> addAccessPoint(AccessPoint accessPoint){
        if(accessPointRepository.findById(accessPoint.getId()).isPresent())
            return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"responseText\":\"Product with this Id already exists in the database\"}");

        List<Port> ports =accessPoint.getPorts();
        for(Port port:ports){
            if(portService.getPortById(port.getId())!= null)
                return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"responseText\":\"Port with this Id is assign to another Product\"}");
        }
        AccessPoint accessPoint1 = accessPointRepository.save(accessPoint);
        if((accessPoint1 != null )&&(accessPoint1.equals(accessPoint)))
            return ResponseEntity.ok("{\"responseText\": \"Product added\"}");
        else throw new RuntimeException("Database can't save this product");
    }

    public ResponseEntity<String> deleteAccessPoint(String productID) {
        if(accessPointRepository.findById(productID).isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"responseText\":\"Product not found\"}");
        }
        else {
            for(Port port: accessPointRepository.findById(productID).get().getPorts())
            {
                portService.removePort(port.getId());
            }
            accessPointRepository.delete(accessPointRepository.findById(productID).get());
            return ResponseEntity.ok("{\"responseText\": \"Product deleted successfully\"}");
        }
    }

    public Optional<AccessPoint> updateDevice(String id, AccessPoint newDeviceData) {
        return accessPointRepository.findById(id)
                .map(device -> {
                    device.setId(newDeviceData.getId());
                    device.setNetID(newDeviceData.getNetID());
                    device.setPorts(newDeviceData.getPorts());
                    return accessPointRepository.save(device);
                });
    }
    public ResponseEntity<String> updateAccessPoint(String productID,AccessPoint accessPoint) {
        if(accessPointRepository.findById(accessPoint.getId()).isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"responseText\":\"Product not found\"}");
        }
        else {
            if(this.updateDevice(productID,accessPoint).isPresent())
                return ResponseEntity.ok("{\"responseText\": \"Product updated successfully\"}");
            else
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("{\"responseText\":\"Can't update product\"}");
        }
    }
}

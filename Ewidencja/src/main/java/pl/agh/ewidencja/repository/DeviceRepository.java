package pl.agh.ewidencja.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.agh.ewidencja.entites.Device;

public interface DeviceRepository extends MongoRepository<Device, String> {
}
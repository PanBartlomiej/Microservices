package pl.agh.ewidencja.entites;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "devices")
public class Device {
    @Id
    private String id;
    private String name;
    private List<String> ports;

    // Getters and setters
}

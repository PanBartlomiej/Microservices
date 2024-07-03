package pl.agh.ewidencja.entites;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "Server")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Server {
    @Id
    private String id;

    private String netID;

    private List<Port> ports;
}

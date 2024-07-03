package pl.agh.ewidencja.entites;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Computer")
public class Computer {
    @Id
    private String id;
    private String netID;

    private List<Port> ports;
}

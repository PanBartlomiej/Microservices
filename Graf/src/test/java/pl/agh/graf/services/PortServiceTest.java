package pl.agh.graf.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.agh.graf.entites.Port;
import pl.agh.graf.repository.PortRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PortServiceTest {

    @Mock
    private PortRepository portRepository;

    @InjectMocks
    private PortService portService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetPortById_Success() {
        Port port = new Port();
        port.setId("1");

        when(portRepository.findById("1")).thenReturn(Optional.of(port));

        Port result = portService.getPortById("1");
        assertNotNull(result);
        assertEquals("1", result.getId());
    }

    @Test
    public void testGetPortById_NotFound() {
        when(portRepository.findById("1")).thenReturn(Optional.empty());

        Port result = portService.getPortById("1");
        assertNull(result);
    }

    @Test
    public void testSave_PortSuccess() {
        Port port = new Port();
        port.setId("1");
        port.setTransmissionSpeed(100);

        when(portRepository.findById("1")).thenReturn(Optional.empty());
        when(portRepository.save(port)).thenReturn(port);

        ResponseEntity<String> response = portService.save(port);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"responseText\": \"Port added\"}", response.getBody());
    }

    @Test
    public void testSave_PortExists() {
        Port port = new Port();
        port.setId("1");

        when(portRepository.findById("1")).thenReturn(Optional.of(port));

        ResponseEntity<String> response = portService.save(port);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("{\"responseText\":\"Port with this Id already exists in the database\"}", response.getBody());
    }

    @Test
    public void testSave_NegativeTransmissionSpeed() {
        Port port = new Port();
        port.setId("1");
        port.setTransmissionSpeed(-100);

        ResponseEntity<String> response = portService.save(port);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("{\"responseText\":\"Port cannot have negative transmission speed\"}", response.getBody());
    }

    @Test
    public void testGetAllPorts() {
        List<Port> ports = new ArrayList<>();
        ports.add(new Port());

        when(portRepository.findAll()).thenReturn(ports);

        List<Port> result = portService.getAllPorts();
        assertEquals(1, result.size());
        assertEquals(ports, result);
    }

    @Test
    public void testRemovePort_Success() {
        Port port = new Port();
        port.setId("1");

        when(portRepository.findById("1")).thenReturn(Optional.of(port));

        ResponseEntity<String> response = portService.removePort("1");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"responseText\": \"Port removed\"}", response.getBody());

        verify(portRepository, times(1)).delete(port);
    }

    @Test
    public void testRemovePort_NotFound() {
        when(portRepository.findById("1")).thenReturn(Optional.empty());

        ResponseEntity<String> response = portService.removePort("1");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("{\"responseText\":\"There is no Port with this ID\"}", response.getBody());
    }
}

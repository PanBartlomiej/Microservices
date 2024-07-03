package pl.agh.ewidencja.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.agh.ewidencja.entites.Medium;
import pl.agh.ewidencja.entites.Port;
import pl.agh.ewidencja.entites.Switch;
import pl.agh.ewidencja.repository.SwitchRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SwitchServiceTest {

    @Mock
    private SwitchRepository switchRepository;

    @Mock
    private PortService portService;

    @InjectMocks
    private SwitchService switchService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetSwitchByID_Success() {
        Switch aSwitch = new Switch();
        aSwitch.setId("1");

        when(switchRepository.findById("1")).thenReturn(Optional.of(aSwitch));

        Switch result = switchService.getSwitchByID("1");
        assertNotNull(result);
        assertEquals("1", result.getId());
    }

    @Test
    public void testGetSwitchByID_NotFound() {
        when(switchRepository.findById("1")).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            switchService.getSwitchByID("1");
        });

        String expectedMessage = "There is no Switch with ID=1";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testGetAllSwitches() {
        List<Switch> switches = new ArrayList<>();
        switches.add(new Switch());

        when(switchRepository.findAll()).thenReturn(switches);

        List<Switch> result = switchService.getAllSwitches();
        assertEquals(1, result.size());
        assertEquals(switches, result);
    }

    @Test
    public void testAddSwitch_Success() {
        Switch aSwitch = new Switch();
        aSwitch.setId("1");
        aSwitch.setPorts(new ArrayList<>());

        when(switchRepository.findById("1")).thenReturn(Optional.empty());
        when(switchRepository.save(aSwitch)).thenReturn(aSwitch);

        ResponseEntity<String> response = switchService.addSwitch(aSwitch);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"responseText\": \"Product added\"}", response.getBody());
    }

    @Test
    public void testAddSwitch_AlreadyExists() {
        Switch aSwitch = new Switch();
        aSwitch.setId("1");

        when(switchRepository.findById("1")).thenReturn(Optional.of(aSwitch));

        ResponseEntity<String> response = switchService.addSwitch(aSwitch);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("{\"responseText\":\"Product with this Id already exists in the database\"}", response.getBody());
    }

    @Test
    public void testAddSwitch_PortConflict() {
        Switch aSwitch = new Switch();
        aSwitch.setId("1");
        Port port = new Port();
        port.setId("1");
        aSwitch.setPorts(List.of(port));

        when(switchRepository.findById("1")).thenReturn(Optional.empty());
        when(portService.getPortById("1")).thenReturn(new Port());

        ResponseEntity<String> response = switchService.addSwitch(aSwitch);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("{\"responseText\":\"Port with this Id is assign to another Switch\"}", response.getBody());
    }

    @Test
    public void testDeleteSwitch_Success() {
        Switch aSwitch = new Switch();
        aSwitch.setId("1");
        ArrayList ports = new ArrayList<>();
        ports.add(new Port("port1", Medium.wifi, 100,new ArrayList<>()));
        ports.add(new Port("port2", Medium.wifi, 100,new ArrayList<>()));
        ports.add(new Port("port3", Medium.wifi, 100,new ArrayList<>()));
        aSwitch.setPorts(ports);

        when(switchRepository.findById("1")).thenReturn(Optional.of(aSwitch));

        doNothing().when(switchRepository).delete(aSwitch);
        when(portService.removePort(anyString())).thenReturn(new ResponseEntity<>("{\"responseText\": \"Port removed\"}", HttpStatus.OK));

        ResponseEntity<String> response = switchService.deleteSwitch("1");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"responseText\": \"Product deleted successfully\"}", response.getBody());
    }

    @Test
    public void testDeleteSwitch_NotFound() {
        when(switchRepository.findById("1")).thenReturn(Optional.empty());

        ResponseEntity<String> response = switchService.deleteSwitch("1");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("{\"responseText\":\"Product not found\"}", response.getBody());
    }

    @Test
    public void testUpdateSwitch_Success() {
        Switch aSwitch = new Switch();
        aSwitch.setId("1");

        Switch newSwitch = new Switch();
        newSwitch.setId("1");
        newSwitch.setNetID("2");
        newSwitch.setPorts(new ArrayList<>());

        when(switchRepository.findById("1")).thenReturn(Optional.of(aSwitch));
        when(switchRepository.save(any(Switch.class))).thenReturn(newSwitch);

        ResponseEntity<String> response = switchService.updateSwitch("1", newSwitch);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"responseText\": \"Product updated successfully\"}", response.getBody());
    }

    @Test
    public void testUpdateSwitch_NotFound() {
        Switch newSwitch = new Switch();
        newSwitch.setId("1");

        when(switchRepository.findById("1")).thenReturn(Optional.empty());

        ResponseEntity<String> response = switchService.updateSwitch("1", newSwitch);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("{\"responseText\":\"Product not found\"}", response.getBody());
    }

    @Test
    public void testUpdateSwitch_Unprocessable() {
        Switch aSwitch = new Switch();
        aSwitch.setId("1");

        Switch newSwitch = new Switch();
        newSwitch.setId("1");
        newSwitch.setNetID("2");
        newSwitch.setPorts(new ArrayList<>());

        when(switchRepository.findById("1")).thenReturn(Optional.of(aSwitch));

        ResponseEntity<String> response = switchService.updateSwitch("1", newSwitch);
        assertEquals("{\"responseText\":\"Server can't update product\"}", response.getBody());
    }
}

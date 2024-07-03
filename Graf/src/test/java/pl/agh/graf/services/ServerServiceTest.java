package pl.agh.graf.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.agh.graf.entites.Medium;
import pl.agh.graf.entites.Port;
import pl.agh.graf.entites.Server;
import pl.agh.graf.repository.ServerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServerServiceTest {

    @Mock
    private ServerRepository serverRepository;

    @Mock
    private PortService portService;

    @InjectMocks
    private ServerService serverService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetServerByID_Success() {
        Server server = new Server();
        server.setId("1");

        when(serverRepository.findById("1")).thenReturn(Optional.of(server));

        Server result = serverService.getServerByID("1");
        assertNotNull(result);
        assertEquals("1", result.getId());
    }

    @Test
    public void testGetServerByID_NotFound() {
        when(serverRepository.findById("1")).thenReturn(Optional.empty());

        Server result = serverService.getServerByID("1");
        assertNull(result);
    }

    @Test
    public void testGetAllServers() {
        List<Server> servers = new ArrayList<>();
        servers.add(new Server());

        when(serverRepository.findAll()).thenReturn(servers);

        List<Server> result = serverService.getAllServers();
        assertEquals(1, result.size());
        assertEquals(servers, result);
    }

    @Test
    public void testAddServer_Success() {
        Server server = new Server();
        server.setId("1");
        server.setPorts(new ArrayList<>());

        when(serverRepository.findById("1")).thenReturn(Optional.empty());
        when(serverRepository.save(server)).thenReturn(server);

        ResponseEntity<String> response = serverService.addServer(server);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"responseText\": \"Product added\"}", response.getBody());
    }

    @Test
    public void testAddServer_AlreadyExists() {
        Server server = new Server();
        server.setId("1");

        when(serverRepository.findById("1")).thenReturn(Optional.of(server));

        ResponseEntity<String> response = serverService.addServer(server);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("{\"responseText\":\"Product with Id= "+server.getId()+" already exists in the database\"}", response.getBody());
    }

    @Test
    public void testAddServer_PortConflict() {
        Server server = new Server();
        server.setId("1");
        Port port = new Port();
        port.setId("1");
        server.setPorts(List.of(port));

        when(serverRepository.findById("1")).thenReturn(Optional.empty());
        when(portService.getPortById("1")).thenReturn(new Port());

        ResponseEntity<String> response = serverService.addServer(server);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("{\"responseText\":\"Port with  Id"+port.getId() +" is assign to another device\"}", response.getBody());
    }

    @Test
    public void testDeleteServer_Success() {
        Server server = new Server();
        server.setId("1");
        ArrayList ports = new ArrayList<>();
        ports.add(new Port("port2",10,10, Medium.wifi, 100,"port","blue",new ArrayList<>()));
        ports.add(new Port("port3",10,10, Medium.wifi, 100,"port","blue",new ArrayList<>()));
        server.setPorts(ports);

        when(serverRepository.findById("1")).thenReturn(Optional.of(server));
        doNothing().when(serverRepository).delete(server);
        when(portService.removePort(anyString())).thenReturn(new ResponseEntity<>("{\"responseText\": \"Port removed\"}", HttpStatus.OK));

        ResponseEntity<String> response = serverService.deleteServer("1");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"responseText\": \"Product deleted successfully\"}", response.getBody());
    }

    @Test
    public void testDeleteServer_NotFound() {
        when(serverRepository.findById("1")).thenReturn(Optional.empty());

        ResponseEntity<String> response = serverService.deleteServer("1");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("{\"responseText\":\"Product not found\"}", response.getBody());
    }

    @Test
    public void testUpdateServer_Success() {
        Server server = new Server();
        server.setId("1");

        Server newServer = new Server();
        newServer.setId("1");
        newServer.setNetID("2");
        newServer.setPorts(new ArrayList<>());

        when(serverRepository.findById("1")).thenReturn(Optional.of(server));
        when(serverRepository.save(any(Server.class))).thenReturn(newServer);

        ResponseEntity<String> response = serverService.updateServer("1", newServer);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"responseText\": \"Product updated successfully\"}", response.getBody());
    }

    @Test
    public void testUpdateServer_NotFound() {
        Server newServer = new Server();
        newServer.setId("1");

        when(serverRepository.findById("1")).thenReturn(Optional.empty());

        ResponseEntity<String> response = serverService.updateServer("1", newServer);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("{\"responseText\":\"Product not found\"}", response.getBody());
    }

    @Test
    public void testUpdateServer_Unprocessable() {
        Server server = new Server();
        server.setId("1");

        Server newServer = new Server();
        newServer.setId("1");
        newServer.setNetID("2");
        newServer.setPorts(new ArrayList<>());

        when(serverRepository.findById("1")).thenReturn(Optional.of(server));

        ResponseEntity<String> response = serverService.updateServer("1", newServer);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("{\"responseText\":\"Server can't update product\"}", response.getBody());
    }
}

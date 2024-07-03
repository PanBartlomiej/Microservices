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
import pl.agh.ewidencja.entites.Router;
import pl.agh.ewidencja.repository.RouterRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RouterServiceTest {

    @Mock
    private RouterRepository routerRepository;

    @Mock
    private PortService portService;

    @InjectMocks
    private RouterService routerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetRouterByID_Success() {
        Router router = new Router();
        router.setId("1");

        when(routerRepository.findById("1")).thenReturn(Optional.of(router));

        Router result = routerService.getRouterByID("1");
        assertNotNull(result);
        assertEquals("1", result.getId());
    }

    @Test
    public void testGetRouterByID_NotFound() {
        when(routerRepository.findById("1")).thenReturn(Optional.empty());

        Router result = routerService.getRouterByID("1");
        assertNull(result);
    }

    @Test
    public void testGetAllRouters() {
        List<Router> routers = new ArrayList<>();
        routers.add(new Router());

        when(routerRepository.findAll()).thenReturn(routers);

        List<Router> result = routerService.getAllRouters();
        assertEquals(1, result.size());
        assertEquals(routers, result);
    }

    @Test
    public void testAddRouter_Success() {
        Router router = new Router();
        router.setId("1");
        router.setPorts(new ArrayList<>());

        when(routerRepository.findById("1")).thenReturn(Optional.empty());
        when(routerRepository.save(router)).thenReturn(router);

        ResponseEntity<String> response = routerService.addRouter(router);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"responseText\": \"Product added\"}", response.getBody());
    }

    @Test
    public void testAddRouter_AlreadyExists() {
        Router router = new Router();
        router.setId("1");

        when(routerRepository.findById("1")).thenReturn(Optional.of(router));

        ResponseEntity<String> response = routerService.addRouter(router);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("{\"responseText\":\"Product with this Id already exists in the database\"}", response.getBody());
    }

    @Test
    public void testAddRouter_PortConflict() {
        Router router = new Router();
        router.setId("1");
        Port port = new Port();
        port.setId("1");
        router.setPorts(List.of(port));

        when(routerRepository.findById("1")).thenReturn(Optional.empty());
        when(portService.getPortById("1")).thenReturn(new Port());

        ResponseEntity<String> response = routerService.addRouter(router);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("{\"responseText\":\"Port with this Id is assign to another Product\"}", response.getBody());
    }

    @Test
    public void testDeleteRouter_Success() {
        Router router = new Router();
        router.setId("1");
        ArrayList ports = new ArrayList<>();
        ports.add(new Port("port1", Medium.wifi, 100,new ArrayList<>()));
        ports.add(new Port("port2", Medium.wifi, 100,new ArrayList<>()));
        ports.add(new Port("port3", Medium.wifi, 100,new ArrayList<>()));
        router.setPorts(ports);
        when(routerRepository.findById("1")).thenReturn(Optional.of(router));
        doNothing().when(routerRepository).delete(router);
        when(portService.removePort(anyString())).thenReturn(new ResponseEntity<>("{\"responseText\": \"Port removed\"}", HttpStatus.OK));

        ResponseEntity<String> response = routerService.deleteRouter("1");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"responseText\": \"Product deleted successfully\"}", response.getBody());
    }

    @Test
    public void testDeleteRouter_NotFound() {
        when(routerRepository.findById("1")).thenReturn(Optional.empty());

        ResponseEntity<String> response = routerService.deleteRouter("1");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("{\"responseText\":\"Product not found\"}", response.getBody());
    }

    @Test
    public void testUpdateRouter_Success() {
        Router router = new Router();
        router.setId("1");

        Router newRouter = new Router();
        newRouter.setId("1");
        newRouter.setNetID("2");
        newRouter.setPorts(new ArrayList<>());

        when(routerRepository.findById("1")).thenReturn(Optional.of(router));
        when(routerRepository.save(any(Router.class))).thenReturn(newRouter);

        ResponseEntity<String> response = routerService.updateRouter("1", newRouter);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"responseText\": \"Product updated successfully\"}", response.getBody());
    }

    @Test
    public void testUpdateRouter_NotFound() {
        Router newRouter = new Router();
        newRouter.setId("1");

        when(routerRepository.findById("1")).thenReturn(Optional.empty());

        ResponseEntity<String> response = routerService.updateRouter("1", newRouter);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("{\"responseText\":\"Product not found\"}", response.getBody());
    }

    @Test
    public void testUpdateRouter_Unprocessable() {
        Router router = new Router();
        router.setId("1");

        Router newRouter = new Router();
        newRouter.setId("1");
        newRouter.setNetID("2");
        newRouter.setPorts(new ArrayList<>());

        when(routerRepository.findById("1")).thenReturn(Optional.of(router));

        ResponseEntity<String> response = routerService.updateRouter("1", newRouter);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("{\"responseText\":\"Server can't update product\"}", response.getBody());
    }
}

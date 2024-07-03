package pl.agh.ewidencja.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.agh.ewidencja.entites.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DeviceServiceTest {

    @Mock
    private SwitchService switchService;

    @Mock
    private ServerService serverService;

    @Mock
    private RouterService routerService;

    @Mock
    private ComputerService computerService;

    @Mock
    private AccessPointService accessPointService;

    @Mock
    private PortService portService;

    @InjectMocks
    private DeviceService deviceService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetDeviceByID_Success() {
        Switch switchDevice = new Switch();
        switchDevice.setId("1");

        when(switchService.getSwitchByID("1")).thenReturn(switchDevice);
        when(serverService.getServerByID("1")).thenReturn(null);
        when(routerService.getRouterByID("1")).thenReturn(null);
        when(computerService.getComputerByID("1")).thenReturn(null);
        when(accessPointService.getAccessPointByID("1")).thenReturn(null);

        List<Object> result = deviceService.getDeviceByID("1");
        assertEquals(1, result.size());
        assertEquals(switchDevice, result.get(0));
    }

    @Test
    public void testGetDeviceByID_NotFound() {
        when(switchService.getSwitchByID("1")).thenReturn(null);
        when(serverService.getServerByID("1")).thenReturn(null);
        when(routerService.getRouterByID("1")).thenReturn(null);
        when(computerService.getComputerByID("1")).thenReturn(null);
        when(accessPointService.getAccessPointByID("1")).thenReturn(null);

        List<Object> result = deviceService.getDeviceByID("1");
        assertEquals(0, result.size());
    }

    @Test
    public void testGetAllDevices() {
        List<Switch> switches = Collections.singletonList(new Switch());
        List<Server> servers = Collections.singletonList(new Server());
        List<Router> routers = Collections.singletonList(new Router());
        List<Computer> computers = Collections.singletonList(new Computer());
        List<AccessPoint> accessPoints = Collections.singletonList(new AccessPoint());

        when(switchService.getAllSwitches()).thenReturn(switches);
        when(serverService.getAllServers()).thenReturn(servers);
        when(routerService.getAllRouters()).thenReturn(routers);
        when(computerService.getAllComputers()).thenReturn(computers);
        when(accessPointService.getAllAccessPoints()).thenReturn(accessPoints);

        List<Object> result = deviceService.getAllDevices();
        assertEquals(5, result.size());
        assertTrue(result.containsAll(switches));
        assertTrue(result.containsAll(servers));
        assertTrue(result.containsAll(routers));
        assertTrue(result.containsAll(computers));
        assertTrue(result.containsAll(accessPoints));
    }

    @Test
    public void testDeleteDeviceByID_SwitchSuccess() {
        ResponseEntity<String> responseEntity = new ResponseEntity<>("Product deleted successfully", HttpStatus.OK);

        when(switchService.deleteSwitch("1")).thenReturn(responseEntity);

        ResponseEntity<String> response = deviceService.deleteDeviceByID("1");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product deleted successfully", response.getBody());
    }

    @Test
    public void testDeleteDeviceByID_ServerSuccess() {
        ResponseEntity<String> responseEntity = new ResponseEntity<>("Product deleted successfully", HttpStatus.OK);

        when(switchService.deleteSwitch("1")).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        when(serverService.deleteServer("1")).thenReturn(responseEntity);

        ResponseEntity<String> response = deviceService.deleteDeviceByID("1");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product deleted successfully", response.getBody());
    }

    @Test
    public void testDeleteDeviceByID_RouterSuccess() {
        ResponseEntity<String> responseEntity = new ResponseEntity<>("Product deleted successfully", HttpStatus.OK);

        when(switchService.deleteSwitch("1")).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        when(serverService.deleteServer("1")).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        when(routerService.deleteRouter("1")).thenReturn(responseEntity);

        ResponseEntity<String> response = deviceService.deleteDeviceByID("1");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product deleted successfully", response.getBody());
    }

    @Test
    public void testDeleteDeviceByID_ComputerSuccess() {
        ResponseEntity<String> responseEntity = new ResponseEntity<>("Product deleted successfully", HttpStatus.OK);

        when(switchService.deleteSwitch("1")).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        when(serverService.deleteServer("1")).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        when(routerService.deleteRouter("1")).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        when(computerService.deleteComputer("1")).thenReturn(responseEntity);

        ResponseEntity<String> response = deviceService.deleteDeviceByID("1");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product deleted successfully", response.getBody());
    }

    @Test
    public void testDeleteDeviceByID_AccessPointSuccess() {
        ResponseEntity<String> responseEntity = new ResponseEntity<>("Product deleted successfully", HttpStatus.OK);

        when(switchService.deleteSwitch("1")).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        when(serverService.deleteServer("1")).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        when(routerService.deleteRouter("1")).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        when(computerService.deleteComputer("1")).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        when(accessPointService.deleteAccessPoint("1")).thenReturn(responseEntity);

        ResponseEntity<String> response = deviceService.deleteDeviceByID("1");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Product deleted successfully", response.getBody());
    }

    @Test
    public void testDeleteDeviceByID_NotFound() {
        when(switchService.deleteSwitch("1")).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        when(serverService.deleteServer("1")).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        when(routerService.deleteRouter("1")).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        when(computerService.deleteComputer("1")).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        when(accessPointService.deleteAccessPoint("1")).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        ResponseEntity<String> response = deviceService.deleteDeviceByID("1");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Can't find and delete device with ID = 1", response.getBody());
    }
}

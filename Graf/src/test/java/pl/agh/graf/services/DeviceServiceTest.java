package pl.agh.graf.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.agh.graf.entites.*;
import pl.agh.graf.services.*;

import java.util.*;

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
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetDeviceById_SwitchFound_ReturnsListWithSwitch() {
        String productId = "1";
        Switch mockSwitch = new Switch();
        mockSwitch.setId(productId);
        when(switchService.getSwitchByID(productId)).thenReturn(mockSwitch);

        List<Object> result = deviceService.getDeviceByID(productId);

        assertEquals(1, result.size());
        assertEquals(mockSwitch, result.get(0));
    }

    @Test
    public void testGetAllDevices_ReturnsCombinedList() {
        // Mock data
        List<Switch> switchList = Arrays.asList(new Switch(), new Switch());
        List<Server> serverList = Arrays.asList(new Server(), new Server());
        List<Router> routerList = Arrays.asList(new Router());
        List<Computer> computerList = Arrays.asList(new Computer(), new Computer());
        List<AccessPoint> accessPointList = Arrays.asList(new AccessPoint());

        // Mock service calls
        when(switchService.getAllSwitches()).thenReturn(switchList);
        when(serverService.getAllServers()).thenReturn(serverList);
        when(routerService.getAllRouters()).thenReturn(routerList);
        when(computerService.getAllComputers()).thenReturn(computerList);
        when(accessPointService.getAllAccessPoints()).thenReturn(accessPointList);

        List<Object> result = deviceService.getAllDevices();

        assertEquals(8, result.size()); // Total number of objects
        assertTrue(result.containsAll(switchList));
        assertTrue(result.containsAll(serverList));
        assertTrue(result.containsAll(routerList));
        assertTrue(result.containsAll(computerList));
        assertTrue(result.containsAll(accessPointList));
    }

    @Test
    public void testUpdateDevicesPositions() {
        // Mock data
//        List<Switch> products = Arrays.asList(new Switch("1","net", 10,10,"switch","green",new ArrayList<Port>()), new Switch("2","net", 10,10,"switch","green",new ArrayList<Port>()));


        // Mock service method calls
//        deviceService.username = "neo4j";
//        deviceService.password = "password";
//        ResponseEntity<String> response = deviceService.updateDevicesPositions(products);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Add more assertions based on expected behavior and mocked data
    }

    @Test
    public void testDeleteDeviceById() {
        String productId = "1";
        // Mock ResponseEntity behavior

        ResponseEntity<String> response = deviceService.deleteDeviceByID(productId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Add more assertions based on expected behavior and mocked data
    }
}

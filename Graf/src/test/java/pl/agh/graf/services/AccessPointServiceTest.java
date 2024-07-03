package pl.agh.graf.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.agh.graf.entites.AccessPoint;
import pl.agh.graf.entites.Port;
import pl.agh.graf.repository.AccessPointRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AccessPointServiceTest {

    @Mock
    private AccessPointRepository accessPointRepository;

    @Mock
    private PortService portService;

    @InjectMocks
    private AccessPointService accessPointService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAccessPointByID_Success() {
        AccessPoint accessPoint = new AccessPoint();
        accessPoint.setId("1");

        when(accessPointRepository.findById("1")).thenReturn(Optional.of(accessPoint));

        AccessPoint result = accessPointService.getAccessPointByID("1");
        assertNotNull(result);
        assertEquals("1", result.getId());
    }

    @Test
    public void testGetAccessPointByID_NotFound() {
        when(accessPointRepository.findById("1")).thenReturn(Optional.empty());

        AccessPoint result = accessPointService.getAccessPointByID("1");
        assertNull(result);
    }

    @Test
    public void testGetAllAccessPoints() {
        List<AccessPoint> accessPoints = Arrays.asList(new AccessPoint(), new AccessPoint());
        when(accessPointRepository.findAll()).thenReturn(accessPoints);

        List<AccessPoint> result = accessPointService.getAllAccessPoints();
        assertEquals(2, result.size());
    }

    @Test
    public void testAddAccessPoint_Success() {
        AccessPoint accessPoint = new AccessPoint();
        accessPoint.setId("1");
        accessPoint.setPorts(Collections.emptyList());

        when(accessPointRepository.findById("1")).thenReturn(Optional.empty());
        when(accessPointRepository.save(any(AccessPoint.class))).thenReturn(accessPoint);

        ResponseEntity<String> response = accessPointService.addAccessPoint(accessPoint);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"responseText\": \"Product added\"}", response.getBody());
    }

    @Test
    public void testAddAccessPoint_ConflictOnExistingAccessPoint() {
        AccessPoint accessPoint = new AccessPoint();
        accessPoint.setId("1");

        when(accessPointRepository.findById("1")).thenReturn(Optional.of(accessPoint));

        ResponseEntity<String> response = accessPointService.addAccessPoint(accessPoint);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("{\"responseText\":\"Product with Id= "+accessPoint.getId()+" already exists in the database\"}", response.getBody());
    }
    @Test
    public void testAddAccessPoint_ConflictOnExistingPort() {
        AccessPoint accessPoint = new AccessPoint();
        accessPoint.setId("1");
        Port port = new Port();
        port.setId("port1");
        accessPoint.setPorts(Arrays.asList(port));

        when(accessPointRepository.findById("1")).thenReturn(Optional.empty());
        when(portService.getPortById("port1")).thenReturn(port);

        ResponseEntity<String> response = accessPointService.addAccessPoint(accessPoint);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("{\"responseText\":\"Port with  Id"+port.getId() +" is assign to another device\"}", response.getBody());
    }

    @Test
    public void testDeleteAccessPoint_Success() {
        AccessPoint accessPoint = new AccessPoint();
        accessPoint.setId("1");
        Port port = new Port();
        port.setId("port1");
        accessPoint.setPorts(Arrays.asList(port));

        when(accessPointRepository.findById("1")).thenReturn(Optional.of(accessPoint));

        ResponseEntity<String> response = accessPointService.deleteAccessPoint("1");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"responseText\": \"Product deleted successfully\"}", response.getBody());

        verify(accessPointRepository, times(1)).delete(accessPoint);
        verify(portService, times(1)).removePort("port1");
    }

    @Test
    public void testDeleteAccessPoint_NotFound() {
        when(accessPointRepository.findById("1")).thenReturn(Optional.empty());

        ResponseEntity<String> response = accessPointService.deleteAccessPoint("1");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("{\"responseText\":\"Product not found\"}", response.getBody());
    }

    @Test
    public void testUpdateDevice_Success() {
        AccessPoint existingAccessPoint = new AccessPoint();
        existingAccessPoint.setId("1");
        AccessPoint newAccessPointData = new AccessPoint();
        newAccessPointData.setId("2");

        when(accessPointRepository.findById("1")).thenReturn(Optional.of(existingAccessPoint));
        when(accessPointRepository.save(any(AccessPoint.class))).thenReturn(newAccessPointData);

        Optional<AccessPoint> result = accessPointService.updateDevice("1", newAccessPointData);
        assertTrue(result.isPresent());
        assertEquals("2", result.get().getId());
    }

    @Test
    public void testUpdateDevice_NotFound() {
        AccessPoint newAccessPointData = new AccessPoint();
        newAccessPointData.setId("2");

        when(accessPointRepository.findById("1")).thenReturn(Optional.empty());

        Optional<AccessPoint> result = accessPointService.updateDevice("1", newAccessPointData);
        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateAccessPoint_Success() {
        AccessPoint existingAccessPoint = new AccessPoint();
        existingAccessPoint.setId("1");
        AccessPoint newAccessPointData = new AccessPoint();
        newAccessPointData.setId("1");

        when(accessPointRepository.findById("1")).thenReturn(Optional.of(existingAccessPoint));
        when(accessPointRepository.save(any(AccessPoint.class))).thenReturn(newAccessPointData);

        ResponseEntity<String> response = accessPointService.updateAccessPoint("1", newAccessPointData);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"responseText\": \"Product updated successfully\"}", response.getBody());
    }

    @Test
    public void testUpdateAccessPoint_NotFound() {
        AccessPoint newAccessPointData = new AccessPoint();
        newAccessPointData.setId("1");

        when(accessPointRepository.findById("1")).thenReturn(Optional.empty());

        ResponseEntity<String> response = accessPointService.updateAccessPoint("1", newAccessPointData);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("{\"responseText\":\"Product not found\"}", response.getBody());
    }

    @Test
    public void testUpdateAccessPoint_Unprocessable() {
        AccessPoint existingAccessPoint = new AccessPoint();
        existingAccessPoint.setId("1");
        AccessPoint newAccessPointData = new AccessPoint();
        newAccessPointData.setId("1");

        when(accessPointRepository.findById("1")).thenReturn(Optional.of(existingAccessPoint));

        ResponseEntity<String> response = accessPointService.updateAccessPoint("1", newAccessPointData);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("{\"responseText\":\"Can't update product\"}", response.getBody());
    }
}

package pl.agh.graf.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.agh.graf.entites.Computer;
import pl.agh.graf.entites.Port;
import pl.agh.graf.repository.ComputerRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ComputerServiceTest {

    @Mock
    private ComputerRepository computerRepository;

    @Mock
    private PortService portService;

    @InjectMocks
    private ComputerService computerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetComputerByID_Success() {
        Computer computer = new Computer();
        computer.setId("1");

        when(computerRepository.findById("1")).thenReturn(Optional.of(computer));

        Computer result = computerService.getComputerByID("1");
        assertNotNull(result);
        assertEquals("1", result.getId());
    }

    @Test
    public void testGetComputerByID_NotFound() {
        when(computerRepository.findById("1")).thenReturn(Optional.empty());

        Computer result = computerService.getComputerByID("1");
        assertNull(result);
    }

    @Test
    public void testGetAllComputers() {
        List<Computer> computers = Arrays.asList(new Computer(), new Computer());
        when(computerRepository.findAll()).thenReturn(computers);

        List<Computer> result = computerService.getAllComputers();
        assertEquals(2, result.size());
    }

    @Test
    public void testAddComputer_Success() {
        Computer computer = new Computer();
        computer.setId("1");
        computer.setPorts(Collections.emptyList());

        when(computerRepository.findById("1")).thenReturn(Optional.empty());
        when(computerRepository.save(any(Computer.class))).thenReturn(computer);

        ResponseEntity<String> response = computerService.addComputer(computer);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"responseText\": \"Product added\"}", response.getBody());
    }

    @Test
    public void testAddComputer_ConflictOnExistingComputer() {
        Computer computer = new Computer();
        computer.setId("1");

        when(computerRepository.findById("1")).thenReturn(Optional.of(computer));

        ResponseEntity<String> response = computerService.addComputer(computer);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("{\"responseText\":\"Product with Id= "+computer.getId()+" already exists in the database\"}", response.getBody());
    }

    @Test
    public void testAddComputer_ConflictOnExistingPort() {
        Computer computer = new Computer();
        computer.setId("1");
        Port port = new Port();
        port.setId("port1");
        computer.setPorts(Arrays.asList(port));

        when(computerRepository.findById("1")).thenReturn(Optional.empty());
        when(portService.getPortById("port1")).thenReturn(port);

        ResponseEntity<String> response = computerService.addComputer(computer);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("{\"responseText\":\"Port with  Id"+port.getId() +" is assign to another device\"}", response.getBody());
    }

    @Test
    public void testDeleteComputer_Success() {
        Computer computer = new Computer();
        computer.setId("1");
        Port port = new Port();
        port.setId("port1");
        computer.setPorts(Arrays.asList(port));

        when(computerRepository.findById("1")).thenReturn(Optional.of(computer));

        ResponseEntity<String> response = computerService.deleteComputer("1");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"responseText\": \"Product deleted successfully\"}", response.getBody());

        verify(computerRepository, times(1)).delete(computer);
        verify(portService, times(1)).removePort("port1");
    }

    @Test
    public void testDeleteComputer_NotFound() {
        when(computerRepository.findById("1")).thenReturn(Optional.empty());

        ResponseEntity<String> response = computerService.deleteComputer("1");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("{\"responseText\":\"Product not found\"}", response.getBody());
    }

    @Test
    public void testUpdateDevice_Success() {
        Computer existingComputer = new Computer();
        existingComputer.setId("1");
        Computer newComputerData = new Computer();
        newComputerData.setId("2");

        when(computerRepository.findById("1")).thenReturn(Optional.of(existingComputer));
        when(computerRepository.save(any(Computer.class))).thenReturn(newComputerData);

        Optional<Computer> result = computerService.updateDevice("1", newComputerData);
        assertTrue(result.isPresent());
        assertEquals("2", result.get().getId());
    }

    @Test
    public void testUpdateDevice_NotFound() {
        Computer newComputerData = new Computer();
        newComputerData.setId("2");

        when(computerRepository.findById("1")).thenReturn(Optional.empty());

        Optional<Computer> result = computerService.updateDevice("1", newComputerData);
        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateComputer_Success() {
        Computer existingComputer = new Computer();
        existingComputer.setId("1");
        Computer newComputerData = new Computer();
        newComputerData.setId("1");

        when(computerRepository.findById("1")).thenReturn(Optional.of(existingComputer));
        when(computerRepository.save(any(Computer.class))).thenReturn(newComputerData);

        ResponseEntity<String> response = computerService.updateComputer("1", newComputerData);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"responseText\": \"Product updated successfully\"}", response.getBody());
    }

    @Test
    public void testUpdateComputer_NotFound() {
        Computer newComputerData = new Computer();
        newComputerData.setId("1");

        when(computerRepository.findById("1")).thenReturn(Optional.empty());

        ResponseEntity<String> response = computerService.updateComputer("1", newComputerData);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("{\"responseText\":\"Product not found\"}", response.getBody());
    }

    @Test
    public void testUpdateComputer_Unprocessable() {
        Computer existingComputer = new Computer();
        existingComputer.setId("1");
        Computer newComputerData = new Computer();
        newComputerData.setId("1");

        when(computerRepository.findById("1")).thenReturn(Optional.of(existingComputer));

        ResponseEntity<String> response = computerService.updateComputer("1", newComputerData);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("{\"responseText\":\"Can't update product\"}", response.getBody());
    }
}

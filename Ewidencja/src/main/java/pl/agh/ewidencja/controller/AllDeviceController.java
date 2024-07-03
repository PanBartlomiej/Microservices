package pl.agh.ewidencja.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.agh.ewidencja.services.*;
//import services.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/all/")
public class AllDeviceController {


    @Autowired
    DeviceService deviceService;

    @GetMapping("GetOne/{productID}")
    public List<Object> getDeviceByID(@PathVariable() String productID) throws IllegalAccessException {
        return deviceService.getDeviceByID(productID);
    }

    @GetMapping("GetAll")
    public List<Object> getAllDevices() throws IllegalAccessException {
        return deviceService.getAllDevices();
    }
}

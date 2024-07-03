package pl.agh.ewidencja.services;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.agh.ewidencja.entites.*;

import java.util.*;
@Service
public class DeviceService {


    @Autowired
    SwitchService switchService;
    @Autowired
    ServerService serverService;
    @Autowired
    RouterService routerService;
    @Autowired
    ComputerService computerService;
    @Autowired
    AccessPointService accessPointService;
    @Autowired
    PortService portService;

    public List<Object> getDeviceByID(String productID) {

        List<Object> list = new ArrayList<>();

        Object object;
        try {
            object = switchService.getSwitchByID(productID);
            if (object != null) {
                list.add(object);
                return list;
            }
        }catch (Exception e) {System.out.println(e.getMessage());}
        try {
            object = serverService.getServerByID(productID);
            if (object != null) {
                list.add(object);
                return list;
            }
        }catch (Exception e) {System.out.println(e.getMessage());}
        try {
            object = routerService.getRouterByID(productID);
            if (object != null) {
                list.add(object);
                return list;
            }
        }catch (Exception e) {System.out.println(e.getMessage());}

        try {
            object = computerService.getComputerByID(productID);
            if (object != null) {
                list.add(object);
                return list;
            }
        }catch (Exception e) {System.out.println(e.getMessage());}

        try {
            object = accessPointService.getAccessPointByID(productID);
            if (object != null) {
                list.add(object);
                return list;
            }
        }catch (Exception e) {System.out.println(e.getMessage());}

        return list;
    }

    public List<Object> getAllDevices() {
        List<Object> list = new ArrayList<>();
        List<Switch> switchList = switchService.getAllSwitches();
        List<Server> serverList = serverService.getAllServers();
        List<Router> routerList = routerService.getAllRouters();
        List<Computer> computerList = computerService.getAllComputers();
        List<AccessPoint> accessPointList = accessPointService.getAllAccessPoints();

        for ( Switch switchObj : switchList ) {
            list.add(switchObj);
        }
        for ( Server serverObj : serverList ) {
            list.add(serverObj);
        }
        for ( Router routerObj : routerList ) {
            list.add(routerObj);
        }
        for ( Computer computerObj : computerList ) {
            list.add(computerObj);
        }
        for ( AccessPoint accessPointObj : accessPointList ) {
            list.add(accessPointObj);
        }
        return list;
    }

    public ResponseEntity<String> deleteDeviceByID(String productID) {
        ResponseEntity<String> response;

        response = switchService.deleteSwitch(productID);
        if (response.getStatusCode().value() == 200)
            return response;
        response = serverService.deleteServer(productID);
        if (response.getStatusCode().value() == 200)
            return response;
        response = routerService.deleteRouter(productID);
        if (response.getStatusCode().value() == 200)
            return response;
        response = computerService.deleteComputer(productID);
        if (response.getStatusCode().value() == 200)
            return response;
        response = accessPointService.deleteAccessPoint(productID);
        if (response.getStatusCode().value() == 200)
            return response;

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Can't find and delete device with ID = " + productID);
    }
}


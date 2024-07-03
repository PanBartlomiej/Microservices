import React, { useEffect, useState } from 'react';
import config from '../config';
import '../cssStyle/deviceList.css'
import {names} from '../names.js';
const DeviceList = ({language}) => {  
  const [message,setMessage] = useState("");
    
    const [switches, setSwitches] = useState([]);
    const [servers, setServers] = useState([]);
    const [computers, setComputers] = useState([]);
    const [routers, setRouters] = useState([]);
    const [accessPoints, setAccessPoints] = useState([]);
    const [expandedTypes, setExpandedTypes] = useState({
        switches: true,
        servers: true,
        computers: true,
        routers: true,
        accessPoints: true
    });
    const [expandedDevices, setExpandedDevices] = useState({});
    const [searchTerm, setSearchTerm] = useState('');

    const fetchData = async () => {
      try {
        const switchResponse = await fetch(config.API_URL_TABELE+'/api/switch/GetAll', {
          mode: 'cors',
          headers: {  Authorization: `Bearer ${localStorage.getItem('token')}`,
        
        },
          method: 'GET',
          }
        )
        const switchData = await switchResponse.json();
        setSwitches(switchData);
        

        const serverResponse =  await fetch(config.API_URL_TABELE+'/api/server/GetAll', {
          mode: 'cors',
          headers: {  Authorization: `Bearer ${localStorage.getItem('token')}` },
          method: 'GET',
          }
        )
        const serverData = await serverResponse.json();
        setServers(serverData);

        const computerResponse =  await fetch(config.API_URL_TABELE+'/api/computer/GetAll', {
          mode: 'cors',
          headers: {  Authorization: `Bearer ${localStorage.getItem('token')}` },
          method: 'GET',
          }
        )
        const computerData = await computerResponse.json();
        setComputers(computerData);

        const routerResponse =  await fetch(config.API_URL_TABELE+'/api/router/GetAll', {
          mode: 'cors',
          headers: {  Authorization: `Bearer ${localStorage.getItem('token')}` },
          method: 'GET',
          }
        )
        const routerData = await routerResponse.json();
        setRouters(routerData);

        const accessPointResponse = await fetch(config.API_URL_TABELE+'/api/accesspoint/GetAll', {
          mode: 'cors',
          headers: {  Authorization: `Bearer ${localStorage.getItem('token')}` },
          method: 'GET',
          }
        )
        const accessPointData = await accessPointResponse.json();
        setAccessPoints(accessPointData);

        console.log(computerData);
        console.log(routerData);
        console.log(accessPointData);
        console.log(switchData);
        console.log(serverData);


      } catch (error) {
        console.error('Error fetching data:', error);
      }
    };
    
    async function DeleteDeviceFun  (nodeToDelete,nodeType)  {
      try {
        const response = await fetch(config.API_URL_GRAF+`/api/${nodeType.toLowerCase()}/${nodeToDelete}`, {
          method: "DELETE",
          mode: 'cors',
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`,
            'Content-Type': 'application/json',
            'Access-Control-Allow-Origin': '*',
          },
        });
        const responseTable = await fetch(config.API_URL_TABELE+`/api/${nodeType.toLowerCase()}/${nodeToDelete}`, {
          method: "DELETE",
          mode: 'cors',
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`,
            'Content-Type': 'application/json',
            'Access-Control-Allow-Origin': '*',
          },
        });
        if (response.ok && responseTable.ok) {
          setMessage(`Node with id ${nodeToDelete} deleted successfully`);
          
        } else if(response.ok && !responseTable.ok  ){
          const jsonResponse = await response.json();
          setMessage(`Error occurred in Table Backend: ${jsonResponse.responseText.toString()}`);
        }else if(!response.ok  && responseTable.ok  ){
          const jsonResponse = await response.json();
          setMessage(`Error occurred in Graf Backend: ${jsonResponse.responseText.toString()}`);
        }else if(!response.ok && !responseTable.ok ){
          const jsonResponse = await response.json();
          const jsonResponseTable = await response.json();
          setMessage(`Error occurred in both Table Backend:${jsonResponse.responseText.toString()}
                     and Graf Backend: ${jsonResponseTable.responseText.toString()}`);
        }
      } catch (error) {
        console.error(error);
        setMessage(error)
      }
    };

    const toggleDevicePorts = (deviceId) => {
      setExpandedDevices(prevState => ({
        ...prevState,
        [deviceId]: !prevState[deviceId]
      }));
    };

    const toggleDeviceType = (type) => {
      setExpandedTypes(prevState => ({
        ...prevState,
        [type]: !prevState[type]
      }));
    };

    const filteredSwitches = switches.filter(device => {
      return device.id.toLowerCase().includes(searchTerm.toLowerCase());
    });

    const filteredServers = servers.filter(device => {
      return device.id.toLowerCase().includes(searchTerm.toLowerCase());
    });

    const filteredComputers = computers.filter(device => {
      return device.id.toLowerCase().includes(searchTerm.toLowerCase());
    });

    const filteredRouters = routers.filter(device => {
      return device.id.toLowerCase().includes(searchTerm.toLowerCase());
    });

    const filteredAccessPoints = accessPoints.filter(device => {
      return device.id.toLowerCase().includes(searchTerm.toLowerCase());
    });

    const handleDelete = async (nodeToDelete,nodeType) => {
      setMessage("Deleting")
      await DeleteDeviceFun(nodeToDelete,nodeType); // Dodaj await, aby poczekać na zakończenie usuwania
      fetchData(); // Pobierz dane ponownie po usunięciu urządzenia
    };

     useEffect(() => {
      fetchData();
    }, []);
return (
<div> 
<div className="devices-container">
  <h1 className="devices-title">{names[language].devices}</h1>
  <div className='device-search-container'>
  <h4>{names[language].searchDeviceByID}</h4>
  <input
    type="text"
    className="search-input"
    placeholder="Search..."
    onChange={(e) => setSearchTerm(e.target.value)}
  />
  </div>
  <div className='message'>{message}</div>
  {/* Switches */}
  <h2 className="device-type" onClick={() => toggleDeviceType('switches')}>
    {names[language].switches}
  </h2>
  {expandedTypes['switches'] && (
    <ul className="device-list">
      {filteredSwitches.map((device) => (
        <li key={device.id} className="device-item" onClick={() => toggleDevicePorts(device.id)}>
          <strong>{device.id}</strong> - {device.netID}
          <button className="delete-button" onClick={() => handleDelete(device.id, 'switch')}>
            {names[language].delete}
          </button>
          {expandedDevices[device.id] && (
            <ul className="sub-device-list">
              {device.ports.map((port) => (
                <li key={port.id}>
                  <strong>{port.id}</strong> - {port.medium}, {port.transmissionSpeed}
                </li>
              ))}
            </ul>
          )}
        </li>
      ))}
    </ul>
  )}

  {/* Servers */}
  <h2 className="device-type" onClick={() => toggleDeviceType('servers')}>
    {names[language].servers}
  </h2>
  {expandedTypes['servers'] && (
    <ul className="device-list">
      {filteredServers.map((device) => (
        <li key={device.id} className="device-item" onClick={() => toggleDevicePorts(device.id)}>
          <strong>{device.id}</strong> - {device.netID}
          <button className="delete-button" onClick={() => handleDelete(device.id, 'server')}>
          {names[language].delete}
          </button>
          {expandedDevices[device.id] && (
            <ul className="sub-device-list">
              {device.ports.map((port) => (
                <li key={port.id}>
                  <strong>{port.id}</strong> - {port.medium}, {port.transmissionSpeed}
                </li>
              ))}
            </ul>
          )}
        </li>
      ))}
    </ul>
  )}

  {/* Router */}
  <h2 className="device-type" onClick={() => toggleDeviceType('routers')}>
    {names[language].routers}
  </h2>
  {expandedTypes['routers'] && (
    <ul className="device-list">
      {filteredRouters.map((device) => (
        <li key={device.id} className="device-item" onClick={() => toggleDevicePorts(device.id)}>
          <strong>{device.id}</strong> - {device.netID}
          <button className="delete-button" onClick={() => handleDelete(device.id, 'router')}>
            {names[language].delete}
          </button>
          {expandedDevices[device.id] && (
            <ul className="sub-device-list">
              {device.ports.map((port) => (
                <li key={port.id}>
                  <strong>{port.id}</strong> - {port.medium}, {port.transmissionSpeed}
                </li>
              ))}
            </ul>
          )}
        </li>
      ))}
    </ul>
  )}

  {/* Computers */}
  <h2 className="device-type" onClick={() => toggleDeviceType('computers')}>
    {names[language].computers}
  </h2>
  {expandedTypes['computers'] && (
    <ul className="device-list">
      {filteredComputers.map((device) => (
        <li key={device.id} className="device-item" onClick={() => toggleDevicePorts(device.id)}>
          <strong>{device.id}</strong> - {device.netID}
          <button className="delete-button" onClick={() => handleDelete(device.id, 'computer')}>
          {names[language].delete}
          </button>
          {expandedDevices[device.id] && (
            <ul className="sub-device-list">
              {device.ports.map((port) => (
                <li key={port.id}>
                  <strong>{port.id}</strong> - {port.medium}, {port.transmissionSpeed}
                </li>
              ))}
            </ul>
          )}
        </li>
      ))}
    </ul>
  )}

  {/* Access Points */}
  <h2 className="device-type" onClick={() => toggleDeviceType('accessPoints')}>
    {names[language].accessPoints}
  </h2>
  {expandedTypes['accessPoints'] && (
    <ul className="device-list">
      {filteredAccessPoints.map((device) => (
        <li key={device.id} className="device-item" onClick={() => toggleDevicePorts(device.id)}>
          <strong>{device.id}</strong> - {device.netID}
          <button className="delete-button" onClick={() => handleDelete(device.id, 'accesspoint')}>
            {names[language].delete}
          </button>
          {expandedDevices[device.id] && (
            <ul className="sub-device-list">
              {device.ports.map((port) => (
                <li key={port.id}>
                  <strong>{port.id}</strong> - {port.medium}, {port.transmissionSpeed}
                </li>
              ))}
            </ul>
          )}
        </li>
      ))}
    </ul>
  )}
  </div>
</div>
      );
};

export default DeviceList;

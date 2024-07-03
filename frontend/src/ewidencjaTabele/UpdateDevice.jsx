import React, { useEffect, useState } from 'react';
import config from '../config';
import { MediumEnum } from "../enum/MediumEnum";

import {names} from '../names.js';
const UpdateDevice = ({language}) => {  
  const [message,setMessage] = useState("");
    
    const [switches, setSwitches] = useState([]);
    const [servers, setServers] = useState([]);
    const [computers, setComputers] = useState([]);
    const [routers, setRouters] = useState([]);
    const [accessPoints, setAccessPoints] = useState([]);
    const [deviceToUpdate, setDeviceToUpdate] = useState({ id: '', type: '' });
    const [deviceType, setDeviceType] = useState('');
    const [productID, setProductID] = useState('');
    const [netID, setNetID] = useState('');
    const [ports, setPorts] = useState([]);
    
  const [ID, setID] = useState("");
  const [medium, setMedium] = useState("wifi");
  const [transmissionSpeed, setTransmissionSpeed] = useState(100);
    const deviceTypes = ['Switch', 'Server', 'Router', 'Computer', 'Access Point'];

    const fetchData = async () => {
      try {
        const switchResponse = await fetch(config.API_URL_TABELE+'/api/switch/GetAll', {
          headers: {  Authorization: `Bearer ${localStorage.getItem('token')}` },
          method: 'GET',
          }
        )
        const switchData = await switchResponse.json();
        setSwitches(switchData);
        

        const serverResponse =  await fetch(config.API_URL_TABELE+'/api/server/GetAll', {
          headers: {  Authorization: `Bearer ${localStorage.getItem('token')}` },
          method: 'GET',
          }
        )
        const serverData = await serverResponse.json();
        setServers(serverData);

        const computerResponse =  await fetch(config.API_URL_TABELE+'/api/computer/GetAll', {
          headers: {  Authorization: `Bearer ${localStorage.getItem('token')}` },
          method: 'GET',
          }
        )
        const computerData = await computerResponse.json();
        setComputers(computerData);

        const routerResponse =  await fetch(config.API_URL_TABELE+'/api/router/GetAll', {
          headers: {  Authorization: `Bearer ${localStorage.getItem('token')}` },
          method: 'GET',
          }
        )
        const routerData = await routerResponse.json();
        setRouters(routerData);

        const accessPointResponse = await fetch(config.API_URL_TABELE+'/api/accesspoint/GetAll', {
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
     
   const updateDevice = async()=>{

   }
      const getSelectedDevice = () => {
        const allDevices = [...switches, ...servers, ...routers, ...computers, ...accessPoints];
        return allDevices.find(device => device.id === deviceToUpdate.id);
      };
    
      useEffect(() => {
        fetchData();
        const selectedDevice = getSelectedDevice();
        
        if (selectedDevice) {
          setDeviceType(deviceToUpdate.type);
          setProductID(selectedDevice.id);
          setNetID(selectedDevice.netID);
          setPorts(selectedDevice.ports || []);
        }
      }, [deviceToUpdate]);
    
    
      const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage("Sending");
        if (ports.length === 0) {
          setMessage("Product must have at least one port");
          return;
        }
        let healthResponseGraf = await fetch(`${config.API_URL_GRAF}/actuator/health`, {
            method: "GET",
            headers: {
              Authorization: `Bearer ${localStorage.getItem('token')}`,
              'Content-Type': 'application/json',
              'Access-Control-Allow-Origin': '*',
            }});
            console.log(healthResponseGraf.status)
    let healthResponseTabele = await fetch(`${config.API_URL_TABELE}/actuator/health`, {
        method: "GET",
        headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`,
       'Content-Type': 'application/json',
       'Access-Control-Allow-Origin': '*',
          }});
         console.log(healthResponseTabele.status)    
    if(healthResponseGraf.status === 200 && healthResponseTabele.status === 200){     
        try {
          let resGraf = await fetch(`${config.API_URL_GRAF}/api/${deviceType}/${productID}`, {
            method: "PUT",
            headers: {
              Authorization: `Bearer ${localStorage.getItem('token')}`,
              'Content-Type': 'application/json',
              'Access-Control-Allow-Origin': '*',
            },
            body: JSON.stringify({
              id: productID,
              type: deviceType,
              netID: netID,
              ports: ports,
            }),
          });
    
          let resTab = await fetch(`${config.API_URL_TABELE}/api/${deviceType}/${productID}`, {
            method: "PUT",
            headers: {
              Authorization: `Bearer ${localStorage.getItem('token')}`,
              'Content-Type': 'application/json',
              'Access-Control-Allow-Origin': '*',
            },
            body: JSON.stringify({
              id: productID,
              netID: netID,
              ports: ports,
            }),
          });
    
          let resJsonTab = await resTab.json();
          let resJsonGraf = await resGraf.json();
    
          if (resTab.status === 200 && resGraf.status === 200) {
            setProductID("");
            setNetID("");
            setPorts([]);
            setMessage("Switch updated successfully");
          } else if(resTab.status === 200 && resGraf.status !== 200) {    
            setMessage(`Error occurred: \n GRAF: ${resJsonGraf.responseText}`);
          }
          else if(resTab.status !== 200 && resGraf.status === 200 ){
            setMessage(`Error occurred: \n  TABELE: ${resJsonTab.responseText}`);
          }
          else{
            setMessage(`Error occurred: \n GRAF: ${resJsonGraf.responseText}\n TABELE: ${resJsonTab.responseText}`);
          }
        } catch (err) {
          console.log(err);
          setMessage(`Error occurred: ${err}`);
        }
    }
    else{ 
        console.log("Error in Health occured")
        console.log(healthResponseGraf)
        console.log(healthResponseTabele)
        setMessage("Graph backend health status: "+healthResponseGraf.status+
        "\n Tabele backend health status: "+healthResponseTabele.status)
    }
      };
      const removePortById = (id) => {
        setPorts(ports.filter(port => port.id !== id));
      };
    

  const handleSubmit2 = (event) => {
    event.preventDefault();
    for (let i = 0; i < ports.length; i++) {
      if (ports[i].id === ID) {
        setMessage(`Port ID ${ID} already exists`);
        return;
      }
    }
    if (medium === "") {
      setMessage("You have to choose a medium");
      return;
    }

    const newPort = {
      id: ID,
      x: 50,
      y: 50,
      medium: medium,
      transmissionSpeed: transmissionSpeed,
      ports: [],
    };

    setPorts((prevPorts) => [...prevPorts, newPort]);
    setID('');
    setMedium('wifi');
    setTransmissionSpeed(100);
  };

      const showAddPort = () => {
        return (
          <div className="add-port-container">
            <h2 className="add-port-title">{names[language].addPort}</h2>
            <form className="add-port-form" onSubmit={handleSubmit2}>
              <label className="add-port-label">
                {names[language].portID}
                <input className="add-port-input" type="text" name="Port" value={ID} onChange={(e) => setID(e.target.value)} />
              </label><br />
        
              <label className="add-port-label">
                {names[language].medium}
                <select className="add-port-select" value={medium} onChange={(e) => setMedium(e.target.value)}>
                  <option value="">{names[language].select}</option>
                  {MediumEnum.map((option, index) => (
                    <option key={index} value={option}>
                      {option}
                    </option>
                  ))}
                </select>
              </label><br />
        
              <label className="add-port-label">
                {names[language].transmisionSpeed}
                <input className="add-port-input" type="number" id="TransmissionSpeedID" name="TransmissionSpeed" min="0"
                  value={transmissionSpeed} onChange={(e) => setTransmissionSpeed(e.target.value)} />
              </label><br />
        
              <button className="add-port-button" type="submit">{names[language].addPort}</button>
            </form>
        
            <div className="message">{message ? <>{message}</> : null}</div>
          </div>
        );
        
      };
  return (
    <div>
      <div className="update-device-container">
        <div className="update-device-form">
          <h2 className="update-device-title">{names[language].selectDeviceToUpdate}</h2>
          <form>
            <div className="dropdown-container">
              <div className="dropdown-item">
                <label htmlFor="switch">{names[language].switches}</label>
                <select
                  id="switch"
                  className="dropdown"
                  value={deviceToUpdate.id}
                  onChange={(e) => {setDeviceToUpdate({ id: e.target.value, type: 'switch' })
                                    setMessage("Device Selected")    
                }}
                >
                  <option value="">{names[language].select}</option>
                  {switches.map((option, index) => (
                    <option key={index} value={option.id}>
                      {option.id}
                    </option>
                  ))}
                </select>
              </div>
              <div className="dropdown-item">
                <label htmlFor="server">{names[language].servers}</label>
                <select
                  id="server"
                  className="dropdown"
                  value={deviceToUpdate.id}
                  onChange={(e) => {
                    setDeviceToUpdate({ id: e.target.value, type: 'server' })
                    setMessage("Device Selected")}}
                >
                  <option value="">{names[language].select}</option>
                  {servers.map((option, index) => (
                    <option key={index} value={option.id}>
                      {option.id}
                    </option>
                  ))}
                </select>
              </div>
              <div className="dropdown-item">
                <label htmlFor="router">{names[language].routers}</label>
                <select
                  id="router"
                  className="dropdown"
                  value={deviceToUpdate.id}
                  onChange={(e) => {
                    setDeviceToUpdate({ id: e.target.value, type: 'router' })
                    setMessage("Device Selected")}}
                >
                  <option value="">{names[language].select}</option>
                  {routers.map((option, index) => (
                    <option key={index} value={option.id}>
                      {option.id}
                    </option>
                  ))}
                </select>
              </div>
              <div className="dropdown-item">
                <label htmlFor="computer">{names[language].computers}</label>
                <select
                  id="computer"
                  className="dropdown"
                  value={deviceToUpdate.id}
                  onChange={(e) => {
                    setDeviceToUpdate({ id: e.target.value, type: 'computer' })
                    setMessage("Device Selected")}}
                >
                  <option value="">{names[language].select}</option>
                  {computers.map((option, index) => (
                    <option key={index} value={option.id}>
                      {option.id}
                    </option>
                  ))}
                </select>
              </div>
              <div className="dropdown-item">
                <label htmlFor="accessPoint">{names[language].accessPoints}</label>
                <select
                  id="accessPoint"
                  className="dropdown"
                  value={deviceToUpdate.id}
                  onChange={(e) => {
                    setDeviceToUpdate({ id: e.target.value, type: 'accesspoint' })
                    setMessage("Device Selected")}}
                >
                  <option value="">{names[language].select}</option>
                  {accessPoints.map((option, index) => (
                    <option key={index} value={option.id}>
                      {option.id}
                    </option>
                  ))}
                </select>
              </div>
            </div>
          </form>
        </div>
      </div>

      <div className="add-product-container">
        <div className="form-container">
          <h2>{names[language].updateDevice}</h2>
          <form className="add-product-form" onSubmit={handleSubmit}>
            <div className="input-group">
              <label htmlFor="deviceType">{names[language].DeviceType}</label>
              <select id="deviceType" value={deviceType} onChange={(e) => setDeviceType(e.target.value)}>
                <option value={deviceType}>{deviceType}</option>
                {deviceTypes.map((option, index) => (
                  <option key={index} value={option}>{option}</option>
                ))}
              </select>
            </div>
            <div className="input-group">
              <label htmlFor="productID">{names[language].ProductID}</label>
              <input id="productID" className="add-product-input" type="text" name="productID" value={productID} onChange={(e) => setProductID(e.target.value)} />
            </div>
            <div className="input-group">
              <label htmlFor="netID">{names[language].IdInsideWFiIS}</label>
              <input id="netID" className="add-product-input" type="text" name="netID" value={netID} onChange={(e) => setNetID(e.target.value)} />
            </div>
            <input className="add-product-button" type="submit" value={names[language].updateDevice} />
          </form>
          <div className="message">{message ? <>{message}</> : null}</div>
        </div>

        <div className="ports-section">
          <h3>Ports List</h3>
          <ul className="ports-list">
            {ports.map((jsonData, index) => (
              <li key={index} className="port-item">
                <div>{names[language].ID} : {jsonData.id}</div>
                <div>{names[language].medium} : {jsonData.medium}</div>
                <div>{names[language].transmisionSpeed} : {jsonData.transmissionSpeed}</div>
                <button className="remove-button" onClick={() => removePortById(jsonData.id)}>{names[language].remove}</button>
              </li>
            ))}
          </ul>

          <div id="inputContainer" className="add-port-container">
            {showAddPort()}
          </div>
        </div>
      </div>
    </div>
  );
};

export default UpdateDevice
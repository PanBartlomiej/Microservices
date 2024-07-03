import React, { useEffect, useState } from 'react';
import config from '../config';
import {names} from '../names.js';
function DeleteDevice({language}){

    const [message,setMessage] = useState("");
    
    
    const [switches, setSwitches] = useState([]);
    const [servers, setServers] = useState([]);
    const [computers, setComputers] = useState([]);
    const [routers, setRouters] = useState([]);
    const [accessPoints, setAccessPoints] = useState([]);
    const [deviceToDelete,setDeviceToDelete] = useState({ id: '', type: '' });


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
  
      useEffect(() => {
        fetchData();
      }, []);

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
            setDeviceToDelete({ id: '', type: '' });
            
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
    const deleteProduct = async (product) => {

        var nodeType =product.type
        var nodeToDelete= product.id
        if(nodeType !=="")  
            DeleteDeviceFun(nodeToDelete,nodeType);
         
      };
     
      const handleDeleteBySelect =async (event)=>{
        event.preventDefault();
        deleteProduct(deviceToDelete);
      }

return(  
<div className="delete-device-container">
  <div className="delete-device-form">
    <h2 className="delete-device-title">{names[language].deleteDevice}</h2>
    <form onSubmit={handleDeleteBySelect}>
      <div className="dropdown-container">
        <div className="dropdown-item">
          <label htmlFor="switch">{names[language].switches}</label>
          <select id="switch" className="dropdown" value={deviceToDelete.id} onChange={(e) => setDeviceToDelete({ id: e.target.value, type: 'switch' })}>
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
          <select id="server" className="dropdown" value={deviceToDelete.id} onChange={(e) => setDeviceToDelete({ id: e.target.value, type: 'server' })}>
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
          <select id="router" className="dropdown" value={deviceToDelete.id} onChange={(e) => setDeviceToDelete({ id: e.target.value, type: 'router' })}>
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
          <select id="computer" className="dropdown" value={deviceToDelete.id} onChange={(e) => setDeviceToDelete({ id: e.target.value, type: 'computer' })}>
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
          <select id="accessPoint" className="dropdown" value={deviceToDelete.id} onChange={(e) => setDeviceToDelete({ id: e.target.value, type: 'accesspoint' })}>
            <option value="">{names[language].select}</option>
            {accessPoints.map((option, index) => (
              <option key={index} value={option.id}>
                {option.id}
              </option>
            ))}
          </select>
        </div>
      </div>
      <input type="submit" value={names[language].deleteDevice} className="delete-product-button" />
      <div className="message">{message ? <p>{message}</p> : null}</div>
    </form>
  </div>
</div>

    )
}

export default DeleteDevice;
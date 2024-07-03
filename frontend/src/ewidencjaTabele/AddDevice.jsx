import { useState } from "react";
import { MediumEnum } from "../enum/MediumEnum";
import config from "../config";
import {names} from '../names.js';

/**
 * AddSwitch component handles the addition of switch devices.
 * It allows the user to fill out the main form and add ports to the ports array.
 * The form data is then sent to the backend API.
 * @returns {JSX.Element} The rendered component.
 */
function AddDevice({language}) {
  const [productID, setProductID] = useState("");
  const [netID, setNetID] = useState("");
  const [ports, setPorts] = useState([]);
  const [message, setMessage] = useState("");
  const [ID, setID] = useState("");
  const [medium, setMedium] = useState("wifi");
  const [transmissionSpeed, setTransmissionSpeed] = useState(100);
  const [deviceType, setDeviceType] = useState("switch");
  const deviceTypes = ["switch", "server", "router", "computer", "accesspoint"];




  /**
   * Handles form submission to add the switch device.
   * Sends the data to the backend API.
   * @param {Event} e The form submit event.
   */
  const handleSubmit = async (e) => {
    
      
     
    e.preventDefault();
    setMessage("Sending");
    if (ports.length === 0) {
      setMessage("Product must have at least one port");
      return;
    }

      try {
      let resGraf = await fetch(`${config.API_URL_GRAF}/api/${deviceType}/add`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json',
          'Access-Control-Allow-Origin': '*',
        },
        body: JSON.stringify({
          id: productID,
          x: 50,
          y: 50,
          type: deviceType,
          netID: netID,
          ports: ports.map(port => ({
            // Funkcja do dodania prefixu do każdego id w tablicy ports
            // chcemy dodawć porty do bazy danych z id równym idUrządzenia+idPortu
            ...port,
            id: `${productID}_${port.id}`
          })),
        }),
      });

      let resTab = await fetch(`${config.API_URL_TABELE}/api/${deviceType}/add`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json',
          'Access-Control-Allow-Origin': '*',
        },
        body: JSON.stringify({
          id: productID,
          netID: netID,
          ports: ports.map(port => ({ 
            // Funkcja do dodania prefixu do każdego id w tablicy ports
            // chcemy dodawć porty do bazy danych z id równym idUrządzenia+idPortu
            ...port,
            id: `${productID}_${port.id}`
          })),
        }),
      });

      let resJsonTab = await resTab.json();
      let resJsonGraf = await resGraf.json();

      if (resTab.status === 200 && resGraf.status === 200) {
        setProductID("");
        setNetID("");
        setPorts([]);
        // window.location.reload();
        setMessage("Switch added successfully");
      } 
      // Tutaj wymagana jest implementacja trasakcyjności czyli jak nie uda się dodać produktu do obu baz
      // tylko do jednej to należy usunąć z tej bazy żeby nie dodało się do żadnej
      // zapobiega to błędą np niedziałającego jednego serwisu. lub sprawdzania parametrów
      else if(resTab.status === 200 && resGraf.status !== 200) {
        const responseTable = await fetch(config.API_URL_TABELE+`/api/${deviceType.toLowerCase()}/${productID}`, {
          method: "DELETE",
          mode: 'cors',
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`,
            'Content-Type': 'application/json',
            'Access-Control-Allow-Origin': '*',
          },
        });
        
        setMessage(`Error occurred: \n GRAF: ${resJsonGraf.responseText}`);
      }
      else if(resTab.status !== 200 && resGraf.status === 200 ){
        const response = await fetch(config.API_URL_GRAF+`/api/${deviceType.toLowerCase()}/${productID}`, {
          method: "DELETE",
          mode: 'cors',
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`,
            'Content-Type': 'application/json',
            'Access-Control-Allow-Origin': '*',
          },
        });
        setMessage(`Error occurred: \n  TABELE: ${resJsonTab.responseText}`);
      }
      else{
        setMessage(`Error occurred: \n GRAF: ${resJsonGraf.responseText}\n TABELE: ${resJsonTab.responseText}`);
      }
    } catch (err) {
      console.log(err);
      setMessage(`Error occurred: ${err}`);
    }
  };

  /**
   * Handles submission of a new port.
   * Adds the port to the ports array.
   */
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

  /**
   * Removes a port from the ports array by its ID.
   * @param {string} idToRemove The ID of the port to remove.
   */
  const removePortById = (idToRemove) => {
    setPorts((prevPorts) => prevPorts.filter((port) => port.id !== idToRemove));
  };

  /**
   * Renders the form for adding ports.
   * @returns {JSX.Element} The port addition form.
   */
  const showAddPort = () => {
    return (
      <div className="add-port-container">
        <h2 className="add-port-title">{names[language].addPort}</h2>
        <form className="add-port-form" onSubmit={handleSubmit2}>
          <label className="add-port-label">
          {names[language].ID}
            <input className="add-port-input" type="text" name="Port" value={ID} onChange={(e) => setID(e.target.value)} />
          </label><br />
    
          <label className="add-port-label">
          {names[language].medium}
            <select className="add-port-select" value={medium} onChange={(e) => setMedium(e.target.value)}>
              <option value="">Select</option>
              {MediumEnum.map((option, index) => (
                <option key={index} value={option}>
                  {option}
                </option>
              ))}
            </select>
          </label><br />
    
          <label className="add-port-label">
          {names[language].transmissionspeed}
            <input className="add-port-input" type="number" id="TransmissionSpeedID" name="TransmissionSpeed" min="0"
              value={transmissionSpeed} onChange={(e) => setTransmissionSpeed(e.target.value)} />
          </label><br />
    
          <button className="add-port-button" type="submit">{names[language].addPort}</button>
        </form>
    
        <div className="message">{message ? <p>{message}</p> : null}</div>
      </div>
    );
    
  };

  /**
   * Renders the main form for adding a switch device.
   * @returns {JSX.Element} The main form.
   */
  return (
  <div className="add-product-container">
  <div className="form-container">
    <h2>{names[language].addProduct}</h2>
    <form className="add-product-form" onSubmit={handleSubmit}>
      <div className="input-group">
        <label htmlFor="deviceType">{names[language].DeviceType}</label>
        <select id="deviceType" value={deviceType} onChange={(e) => setDeviceType(e.target.value)}>
          <option value="">{names[language].select}</option>
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
      <input className="add-product-button" type="submit" value={names[language].addProduct} />
    </form>
    <div className="message">{message ? <p>{message}</p> : null}</div>
  </div>

  <div className="ports-section">
    <h3>{names[language].PortsList}</h3>
    <ul className="ports-list">
  {ports.map((jsonData, index) => (
    <li key={index} className="port-item">
      <div>{names[language].ID} {jsonData.id}</div>
      <div>{names[language].Medium}{jsonData.medium}</div>
      <div>{names[language].TransmissionSpeed}{jsonData.transmissionSpeed}</div>
      <button className="remove-button" onClick={() => removePortById(jsonData.id)}>{names[language].remove}</button>
    </li>
  ))}
</ul>

    <div id="inputContainer" className="add-port-container">
      {showAddPort()}
    </div>
  </div>
</div>
);

}

export default AddDevice;

import React, { useEffect, useState } from 'react';
import config from '../config';
import '../cssStyle/connectPorts.css'
function ConnectPorts () {
  const [nodes, setNodes] = useState([]);
  const [selectedNode1, setSelectedNode1] = useState(null);
  const [selectedNode2, setSelectedNode2] = useState(null);
  const [message, setMessage] = useState("");

  
  const getPorts= async () =>{
    // Fetch graph data from your API endpoint
    try{
      const token = localStorage.getItem('token');
      const response = await fetch(config.API_URL_GRAF+'/api/port/GetAll', {
        method: 'GET',
        headers: { Authorization: `Bearer ${token}` },
    }
    )
        .then((response) => response)
        
        const data = await response.json();
        const portsNodes =  data.map((portItem) => ({
          id: portItem.id,
          medium: portItem.medium,
          transmissionSpeed: portItem.transmissionSpeed,
          ports: portItem.ports
          })
        );
          setNodes(portsNodes);
       }
       catch{}
  }
  useEffect(() => {
   getPorts();
  }, []);

  
  let  handleSubmit = async (e) => {
    e.preventDefault();
      try {
        console.log(selectedNode1)
        console.log(selectedNode2)
        if(selectedNode1 === null || selectedNode2===null){
            setMessage("chose Ports");
        }
        else if(selectedNode1 === selectedNode2){
          setMessage("Cannot connect port to itself")
        }
        else{
        const node1= nodes.find(node => node.id === (selectedNode1));
        const node2= nodes.find(node => node.id === (selectedNode2));
    
        console.log(node1)
          let res = await fetch(config.API_URL_GRAF+"/api/port/connect", {
            headers: {
              Authorization: `Bearer ${localStorage.getItem('token')}`,
              "Content-Type": "application/json",
          },
              method: "POST",
              body: JSON.stringify([{
                id: node1.id,
                medium: node1.medium,
                transmissionSpeed: node1.transmissionSpeed,
                ports: node1.ports
              },
              {
                id: node2.id,
                medium: node2.medium,
                transmissionSpeed: node2.transmissionSpeed,
                ports: node2.ports
            }]),
            });
            console.log(selectedNode1)

        let resJson = await res.json();
        if (res.status === 200) {
            setSelectedNode1(null);
            setSelectedNode2(null);
            setMessage("Ports connected successfully");
        } else {
          setMessage("Error occured:  "+ resJson.responseText.toString());
        }
      }
      } catch (err) {
        console.log(err);
      }
    };

    return (
      <div className="container">
        {/* Dropdowns for Node Selection */}
        <select id="dropdown1" value={selectedNode1} onChange={(e) => setSelectedNode1(e.target.value)}>
          <option value={null}>Select</option>
          {nodes.map((node) => (
            <option key={node.id} value={node.id}>
              id: {node.id}
            </option>
          ))}
        </select>
        <select id="dropdown2" value={selectedNode2} onChange={(e) => setSelectedNode2(e.target.value)}>
          <option value={null}>Select</option>
          {nodes.map((node) => (
            <option key={node.id} value={node.id}>
              id: {node.id}
            </option>
          ))}
        </select>
    
        {/* Button to Connect Ports */}
        <button  className="connect-ports-button" onClick={handleSubmit}>Connect Ports</button>
        
        {/* Message */}
        <div className="message">{message ? <p>{message}</p> : null}</div>
      </div>
    );
    
}
export default ConnectPorts;
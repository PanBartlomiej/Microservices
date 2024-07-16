import React, { useEffect, useState } from 'react';
import {Graph } from 'react-d3-graph';
import "../style.css"
import config from '../config';
import '../cssStyle/graphComponent.css'
import { names } from '../names';
function GraphComponent ({language}) {

  const [graphData, setGraphData] = useState({ nodes: [], links: [] });
  const [clickedNodedata, setClickedNodedata]=useState("");
  const [clickedLinkData,setClickedLinkData]=useState("");
  const [nodePositions, setNodePositions] = useState({});
  
  const [message, setMessage] = useState("");
  const myConfig = {
    staticGraphWithDragAndDrop: true,
    nodeHighlightBehavior: true,
    d3: {
      disableLinkForce: true,
      gravity: -50,
      linkLength: 100,
    },
    node: {
      color: 'lightblue',
      size: 320,
      fontColor: document.getElementsByTagName('body')[0].style.color,
      fontSize:13,
      highlightFontSize: 16,
      highlightStrokeColor: 'blue',
    },
    link: {
      selfLinkDirection: "TOP_RIGHT",
      highlightColor: 'lightblue',
    },
    height: window.innerHeight,
    width: window.innerWidth,
  };
  
  const setNodesAndLinks = async (data) =>{  
    setGraphData();
    console.log(data)

    const switchsNodes = data.map((switchItem) => ({ id: switchItem.id,
    name : "A" ,
    x : switchItem.x ,
    y : switchItem.y,
    label: switchItem.id ,
    color: switchItem.color ? switchItem.color :'lightgreen',
    nodeType: switchItem.type,
    netID: switchItem.netID
  }));
    console.log(data)
    const portsNodes = data.flatMap((switchItem) =>
    switchItem.ports.map((portItem) => ({
      
      id : portItem.id ,
      name : "A" ,
      x : portItem.x ,
      y : portItem.y,
      label: portItem.id,
      color: switchItem.color ? switchItem.color :'lightblue',
      size: 70,
      symbolType: "square",
      nodeType: 'port', 
      medium: portItem.medium
    }))
  );
  var nodesTMP= [...switchsNodes, ...portsNodes];
  const nodes =nodesTMP;

  const switchLinks = data.flatMap((switchItem) =>
   switchItem.ports.map((portItem) => ({ source: switchItem.id, target: portItem.id }))
  );
  const portLinks = data.flatMap((switchItem) =>
    switchItem.ports.flatMap((portItem) =>
      portItem.ports.length > 0
        ? portItem.ports
            .filter((secondPort) =>
              portsNodes.some((portNode) => portNode.id === secondPort.id)
            )
            .map((secondPort) => ({
              source: portItem.id,
              target: secondPort.id
            }))
        : []
    )
  );
var linksTmp = []
linksTmp = [...switchLinks,...portLinks];
const links=linksTmp;
setGraphData({ nodes, links });
data="";
}
const setNodesAndLinks2 = async (dataProducts,dataPorts) =>{  
  console.log(dataProducts.length)
  if(dataProducts.length === 0)
    {setMessage("Can't find Device with this ID");
    return;
    }
    else{
  const switchsNodes = dataProducts.map((switchItem) => ({ id: switchItem.id,
  name : "A" ,
  x : switchItem.x ,
  y : switchItem.y,
  label: switchItem.id ,
  color: switchItem.color ? switchItem.color :'lightgreen',
  nodeType: switchItem.type,
  netID:switchItem.netID
}));
  const portsNodes =  dataPorts.map((portItem) => ({
  
    id : portItem.id ,
    name : "A" ,
    x : portItem.x ,
    y : portItem.y,
    label: portItem.id,
    color: 'blue',
    size: 70,
    symbolType: "square",
    nodeType: 'port', 
    medium: portItem.medium
  }))

var nodesTMP= [...switchsNodes, ...portsNodes];
const nodes =nodesTMP;

const portNodesIds = new Set(portsNodes.map(portNode => portNode.id));

  const switchLinks = dataProducts.flatMap((switchItem) =>
    switchItem.ports
      .filter(portItem => portNodesIds.has(portItem.id))
      .map((portItem) => ({ source: switchItem.id, target: portItem.id }))
  );

  const portLinks = dataProducts.flatMap((switchItem) =>
    switchItem.ports
      .filter(portItem => portNodesIds.has(portItem.id))
      .flatMap((portItem) =>
        portItem.ports
          .filter(secondPort => portNodesIds.has(secondPort.id))
          .map((secondPort) => ({
            source: portItem.id,
            target: secondPort.id,
          }))
      )
  );
var linksTmp = []
linksTmp = [...switchLinks,...portLinks];
const links=linksTmp;
setGraphData({ nodes, links });
    }
}

const getDevices =async () =>{
    try{
    const response = await fetch(config.API_URL_GRAF+'/api/all/GetAll', {
      headers: {  Authorization: `Bearer ${localStorage.getItem('token')}` },
      method: 'GET',
      }
    )
    const data = await response.json();
    setNodesAndLinks(data);
    }catch(e){}
  }

  useEffect(() => {
    // Fetch graph data from API endpoint
    getDevices();
      
  }, []);

  const handleNodeClick = (nodeId) => {
    // Retrieve additional data associated with the clicked node
    const clickedNode = graphData.nodes.find((node) => node.id === nodeId);
    setClickedNodedata(getNodeData(clickedNode));
    console.log('Clicked Node Data:', clickedNode);
  };


  const deleteNode = async (clickedNode) => {
    var nodeType =clickedNode.nodeType.toLowerCase() 
    if(nodeType !=="")  
      try {
      const response = await fetch(config.API_URL_GRAF+`/api/${nodeType.toLowerCase()}/${clickedNode.id}`, {
        method: "DELETE",
        mode: 'cors',
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json',
          'Access-Control-Allow-Origin': '*',
        },
      });
      const responseTable = await fetch(config.API_URL_TABELE+`/api/${nodeType.toLowerCase()}/${clickedNode.id}`, {
        method: "DELETE",
        mode: 'cors',
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json',
          'Access-Control-Allow-Origin': '*',
        },
      });
      if (response.ok && responseTable.ok) {
        setMessage(`Node with id ${clickedNode.id} deleted successfully`);
        await getDevices();
        
      } else {
        const jsonResponse = await response.json();
        setMessage(`Error occurred: ${jsonResponse.responseText}`);
      }
    } catch (error) {
      console.error(error);
    }
  };
  const getNodeData=(clickedNode)=>{
    return (<div className="clicked-node-info">
    <h2>Clicked Node:</h2>
    <pre>{JSON.stringify(clickedNode, null, 2)}</pre>
    <button onClick={(e) => setClickedNodedata("")}>Hide Info</button>
    <button onClick={(e) => deleteNode(clickedNode)}>Delete Node</button>
    {/* <button onClick={(e) => updateNode(clickedNode)}>Update Node</button> */}
  </div>
  )
  }
  
  const deleteLink = async (source,target) => {
    try {
      const response = await fetch(config.API_URL_GRAF+`/api/port/disconnect/${source}/${target}`, {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json',
          'Access-Control-Allow-Origin': '*',
        },
      });
  
      if (response.ok) {
        setMessage(`Link between  ${source} and ${target} deleted successfully`);
        window.location.reload();
      } else {
        const jsonResponse = await response.json();
        setMessage(`Error occurred: ${jsonResponse.responseText}`);
      }
    } catch (error) {
      console.error(error);
    }
  };
  const handleLinkClick =(source,target) =>{
    setClickedLinkData(getLinkData(source,target));
  }
  const getLinkData =(source,target)=>{
    return (<div className="clicked-link-info">
    <h2>Clicked Link:</h2>
    <pre>Node: "{source}" CONNECTED TO Target: "{target}"</pre>
    <button onClick={(e) => setClickedLinkData("")}>Hide Info</button>
    <button onClick={(e) => deleteLink(source, target)}>Delete Link</button>
  </div>
  )
  }
  let savePositions = async () => {
    // Convert node positions object into a table format
    const devices = [];
    for (const id in nodePositions) {
      if (nodePositions.hasOwnProperty(id)) {
        const { x, y } = nodePositions[id];
        devices.push( {"id": id,
        "x" :x,
        "y" :y} );
      }
    }
    console.log(devices)
    console.log(JSON.stringify(devices))
  
    try {
      //ta 1 w URL jest dlatego że bez tego nie działa metoda PUT,
      //można by tu dać metodę POST ale PUT lepiej bo w końcu aktualizujemy dane
      let res = await fetch(config.API_URL_GRAF + "/api/all/updatePosition/1", {
        method: "PUT",
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json',
          'Access-Control-Allow-Origin': '*'
        },
        body: JSON.stringify(devices), // Pass the list of devices as the body
      });
      let resJson = await res.json();
      if (res.status === 200) {
        setMessage("devices coordinates successfully updated");
        console.log(message)
      } else {
        setMessage("Error occurred: " + resJson.responseText.toString());
      }
    } catch (err) {
      console.log(err);
      setMessage("Error occurred: " + err);
    }
    console.log(devices);
  };
  
  
  const handleNodePositionChange = (nodeId, x, y) => {
    // Update the node positions in the state
    setNodePositions({
      ...nodePositions,
      [nodeId]: { x, y }
    });
  };

  const [deviceType, setDeviceType] = useState('all');
  const [IDSearch, setIDSearch] = useState('');

  const deviceTypes = ['all','switch', 'server', 'router', 'computer', 'accesspoint'];

    // Functions to handle search
    const handleTypeSearch = async() => {
      try{
        const response  = await fetch(config.API_URL_GRAF+'/api/'+deviceType+'/GetAll', {
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
          method: 'GET',
          }
        )
        if (response.status === 200) {
          const data = await response.json();
          setNodesAndLinks(data);
        } else {
          console.log('Received non-200 status code:', response.status);
        }
        }catch(e){console.log(e)}
      console.log('Searching by device type...');
      console.log('Device Type:', deviceType);
    };
  
    const handleIDSearch =async () => {
      if(IDSearch==='')
        {
          setMessage("Write ID in input field!")
        }
        else
      try{

        const response  = await fetch(config.API_URL_GRAF+'/api/all/GetDeviceAndNeighbours/'+IDSearch, {
          headers: { Authorization: `Bearer ${localStorage.getItem('token')}` },
          method: 'GET',
          }
        )

        if (response.status === 200) {
          const data = await response.json();
          
          console.log(data.Devices)
          setNodesAndLinks2(data.Devices,data.Ports);
        } else {
          console.log('Received non-200 status code:', response.status);
          setMessage('Cannot Find this Device. Received non-200 status code:', response.status)
        }

        }catch(e){console.log(e)}
      console.log('Searching by IP...');
      console.log('IP:', IDSearch);
    };
  
    return (
      <div>
        <div className="container">
          {/* Device Type Dropdown */}
          <select value={deviceType} onChange={(e) => setDeviceType(e.target.value)}>
            <option value="select">{names[language].selectDeviceType}</option>
            {deviceTypes.map((type, index) => (
              <option key={index} value={type}>{type}</option>
            ))}
          </select>
          <button className="search-button" onClick={handleTypeSearch}>{names[language].searchByType}</button>
        
          <p></p>
          {/* ID Search Field */}
          <input
            type="text"
            placeholder="Search by IP"
            value={IDSearch}
            onChange={(e) => setIDSearch(e.target.value)}
          />

          {/* Search Buttons */}
          <button className="search-button" onClick={handleIDSearch}>{names[language].searchById}</button>
        </div>
        <div>
          <div className="clicked-node-info-container">
            {/* Render clicked node info here */}
          </div>
          <div>
            <button onClick={savePositions}>{names[language].savePositions}</button>
          </div>
          <div className='message'>
              {message}
          </div>
          <div>
            <Graph
              className="Graph"
              id="graph-id"
              data={graphData}
              config={myConfig}
              onClickNode={handleNodeClick}
              onClickLink={handleLinkClick}
              onNodePositionChange={handleNodePositionChange}
            />
          </div>
          <div className="clicked-node-info-container">
            {clickedNodedata}
            {clickedLinkData}
          </div>
          <div className='message'>{message}</div>
        </div>
      </div>
    );
    
};
export default GraphComponent;
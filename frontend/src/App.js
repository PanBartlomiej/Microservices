import React, { useState } from 'react';
import { useAuth } from 'react-oidc-context';
import AddDevice from './ewidencjaTabele/AddDevice';
import ConnectPorts from './ewidencja/ConnectPorts';
import GraphComponent from './ewidencja/GraphComponent';
import DeviceList from './ewidencjaTabele/DeviceList';
import DeleteDevice from './ewidencjaTabele/DeleteDevice';
import UpdateDevice from './ewidencjaTabele/UpdateDevice';
import GrafanaChart from './monitoring/monitoring.jsx'
import './cssStyle/style.css'; // Import jasnego stylu
import './cssStyle/darkStyle.css'; // Import ciemnego stylu
import {names} from './names.js';
import config from "./config";

function App() {
  const grafanaSrc = config.API_URL_MONITORING
  const [selectedComponent, setSelectedComponent] = useState('Tabele');
  const [darkMode, setDarkMode] = useState(false); // Stan dla trybu ciemnego
  const [language, setLanguage] = useState('pl');
  const toggleLanguage = () => {
    setLanguage(language === 'pl' ? 'en' : 'pl');
  };
  // Handle component selection change
  const handleChange = (event) => {
    setSelectedComponent(event.target.value);
  };

  // Toggle dark mode
  const toggleDarkMode = () => {
    setDarkMode(!darkMode);
  
    if(!darkMode){
   var element = document.getElementsByTagName('body')[0];   
   element.style.background="black";
   element.style.fontFamily= "Arial, sans-serif";
   element.style.margin= 0;
   element.style.padding= 0;
   element.style.backgroundColor= "hsl(0, 0%, 12%)";
   element.style.color= "#a8a8a8";}
   else{
    
   var element = document.getElementsByTagName('body')[0]; 
    element.style.backgroundColor= "#f4f5f7";
    element.style.color = "#333";

   }
  };

  // Render selected component
  const renderComponent = () => {
    switch (selectedComponent) {
      case 'Tabele':
        return (
          <div>
            <DeviceList language={language} />
          </div>
        );
      case 'AddDevice':
        return (
          <div>
            <AddDevice language={language}/>
          </div>
        )
      case 'DeleteDevice':
        return (
          <div>
            <DeleteDevice language={language}/>
          </div>
        )
      case 'UpdateDevice':
        return (
          <div>
            <UpdateDevice language={language}/>
          </div>
        )
      case 'Graf':
        return (
          <div>
            <ConnectPorts  language={language}/>
            <GraphComponent  language={language}/>
          </div>
        );
      case 'Monitoring':
        return (
          <div>
            <h2>Monitoring</h2>
            <GrafanaChart src={grafanaSrc}/>
          </div>
        );
      default:
        return (
          <div>
            <DeviceList language={language}/>
          </div>
        );
    }
  };

  const oidc = useAuth();

  switch (oidc.activeNavigator) {
    case 'signinSilent':
      return <div className="loading">Signing you in...</div>;
    case 'signoutRedirect':
      return <div className="loading">Signing you out...</div>;
  }

  if (oidc.isLoading) {
    return <div className="loading">Loading...</div>;
  }

  if (oidc.error) {
    return <div className="error">Oops... {oidc.error.message}</div>;
  }

  if (oidc.isAuthenticated) {
    localStorage.setItem('token', oidc.user?.access_token);

    return (
      <div className={`app-container${darkMode ? '-dark-mode' : '-light-mode'}`}> {/* Dodanie klasy dark-mode w zależności od trybu ciemnego */}
        <button
          className="logout-button"
          onClick={() => {
            oidc.removeUser();
            localStorage.removeItem('token');
            oidc.signoutRedirect();
          }}
        >
          {names[language].logOut}
        </button>
        <button className="language-toggle-button" onClick={toggleLanguage}>
          {language === 'pl' ? 'Switch to English' : 'Przełącz na polski'}
        </button>
        <button className="dark-mode-button" onClick={toggleDarkMode}> {/* Przycisk do przełączania między trybem jasnym a ciemnym */}
          {darkMode ? names[language].switchToLightMode : names[language].switchToDarkMode}
        </button>
        {window.location.search.includes('state') ? (window.location.href = '/') : null}
        
        <h1>{names[language].title}</h1>
        <select className="component-select" value={selectedComponent} onChange={handleChange}>
          <option value="">{names[language].selector}</option>
          <option value="Tabele">{names[language].appInwentory}</option>
          <option value="AddDevice">{names[language].appAdd}</option>
          <option value="DeleteDevice">{names[language].appDelete}</option>
          <option value="UpdateDevice">{names[language].appModify}</option>
          <option value="Graf">{names[language].appGraph}</option>
          <option value="Monitoring">{names[language].appMonitoring}</option>
        </select>
        <div className="selected-component">{renderComponent()}</div>
      </div>
    );
  }

  return (
    <div className="login-container">
      <button className="login-button" onClick={() => void oidc.signinRedirect()}>
        {names[language].logIn}
      </button>
    </div>
  );
}

export default App;

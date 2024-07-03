import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import { AuthProvider } from "react-oidc-context";
import config from './config';
import './cssStyle/style.css'
const root = ReactDOM.createRoot(document.getElementById('root'));

const oidcConfig = {
  authority: config.OIDC_AUTHORITY,
  client_id: config.OIDC_CLIENT_ID,
  redirect_uri: config.OIDC_REDIRECT_URI,
  post_logout_redirect_uri: config.OIDC_POST_LOGOUT_REDIRECT_URI
  
};
root.render(
  <AuthProvider {...oidcConfig}>
    <App />
  </AuthProvider>
);

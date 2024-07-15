// config.js
const config = {
  API_URL_GRAF: 'http://gateway:8222/graf',
  API_URL_TABELE: 'http://gateway:8222/ewidencja',
  API_URL_MONITORING: "http://localhost:3000",
  OIDC_AUTHORITY: 'http://keycloak:8080/realms/oidc',
  OIDC_CLIENT_ID: "ewidencja",
  OIDC_REDIRECT_URI: 'http://localhost',
  OIDC_POST_LOGOUT_REDIRECT_URI: 'http://localhost'
  };
  
  export default config;
  
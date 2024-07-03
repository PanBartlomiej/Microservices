// config.js
const config = {
  API_URL_GRAF: 'http://backend:8089', //powinno być http://backend:8222/graf
  API_URL_TABELE: 'http://backend:8090', //powinno być http://backend:8222/ewidencja
  OIDC_AUTHORITY: 'http://keycloak:8080/realms/oidc',
  OIDC_CLIENT_ID: "ewidencja",
  OIDC_REDIRECT_URI: 'http://localhost',
  OIDC_POST_LOGOUT_REDIRECT_URI: 'http://localhost'
  };
  
  export default config;
  
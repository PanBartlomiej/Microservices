import React, {useEffect, useState} from 'react';


const GrafanaChart = ({ src }) => {

    const [srcG, setSrcG] = useState('');

    useEffect(() => {

        const grafanaUrl = src;
        const headers= {
            Authorization: `Bearer ${localStorage.getItem('token')}`,
            'Content-Type': 'application/json',
            'Access-Control-Allow-Origin': '*',
        };

        // Możesz użyć fetch lub axios do pobrania danych
        fetch(grafanaUrl, { headers })
            .then(response => response.text())
            .then(data => setSrcG(data))
            .catch(error => console.error('Error fetching data:', error));
    }, []);

    return (
        <iframe
            srcDoc={srcG}
            width="100%"
            height="600px"
            frameBorder="0"
            title="Grafana Chart"
            style={{ border: 'none' }}
        ></iframe>
    );
};

export default GrafanaChart;

import React from 'react';


const GrafanaChart = ({ src }) => {
    return (
        <iframe
            src={src}
            width="100%"
            height="600px"
            frameBorder="0"
            title="Grafana Chart"
            style={{ border: 'none' }}
        ></iframe>
    );
};

export default GrafanaChart;

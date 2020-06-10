const webpack = require("webpack");

if (config.mode === "development") {
    config.plugins.push(new webpack.EnvironmentPlugin({
        "HALOS_CORS": true,
        "HALOS_LOG_LEVEL": "DEBUG",
        "HALOS_PROXY_URL": "http://localhost:8080",
        "HALOS_REST_VERSION": "v1",
        "HALOS_VERSION": "0.0.1"
    }))
} else {
    config.plugins.push(new webpack.EnvironmentPlugin({
        "HALOS_CORS": false,
        "HALOS_LOG_LEVEL": "INFO",
        "HALOS_PROXY_URL": "",
        "HALOS_REST_VERSION": "v1",
        "HALOS_VERSION": "0.0.1"
    }))
}

package com.pst.asseco.channels.devops.infrastructure.utils

class EinsteinProperties {

    private final Properties properties

    EinsteinProperties() {
        properties = new Properties()
        loadProperties()
    }

    private void loadProperties() {

        String propsFilePath = "/config/einstein.properties"

        try {
            InputStream propsInputStream = getClass().getResourceAsStream(propsFilePath)
            if (!propsInputStream)
                throw new Exception("Unable to get InputStream from file path $propsFilePath")

            properties.load(propsInputStream)

        } catch (Exception e) {
            Console.err("Unable to load properties file '$propsFilePath'. Cause: $e")
            throw e
        }
    }


    boolean isDebugModeOn() {
        return (properties.debug.toString().toBoolean())
    }

    String getWorkspaceRootFolder() {
        return properties.workspaceFolder.toString()
    }
}

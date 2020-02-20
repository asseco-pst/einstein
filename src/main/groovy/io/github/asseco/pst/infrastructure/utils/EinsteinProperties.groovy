package io.github.asseco.pst.infrastructure.utils

class EinsteinProperties {

    private static EinsteinProperties instance
    private final Properties properties

    private EinsteinProperties() {
        properties = new Properties()
        loadProperties()
    }

    static EinsteinProperties instance() {
        if(!instance)
            instance = new EinsteinProperties()
        return instance
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

    /**
     * Represents the max number of seconds that the process can live
     * @return
     */
    int getMaxDuration() {
        return (properties.maxDuration as int)
    }

    boolean isDebugModeOn() {
        return (properties.debug.toString().toBoolean())
    }

    String getWorkspaceRootFolder() {
        return properties.workspaceFolder.toString()
    }
}

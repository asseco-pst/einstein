package io.github.asseco.pst.infrastructure.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class EinsteinProperties {
    private static EinsteinProperties instance
    private final Properties properties
    private static final Logger logger = LoggerFactory.getLogger(EinsteinProperties.class)

    private EinsteinProperties() {
        properties = new Properties()
        loadProperties()
    }

    static EinsteinProperties instance() {
        if (!instance) {
            instance = new EinsteinProperties()
        }
        return instance
    }

    private void loadProperties() {
        String propsFilePath = "/config/einstein.properties"
        try {
            InputStream propsInputStream = getClass().getResourceAsStream(propsFilePath)
            if (!propsInputStream) {
                throw new Exception("Unable to get InputStream from file path $propsFilePath")
            }
            properties.load(propsInputStream)

        } catch (Exception exception) {
            logger.warn("Unable to load properties file '${propsFilePath}'. Cause: ${exception.getMessage()}")
            logger.debug("Exception thrown:", exception)
            throw exception
        }
    }

    /**
     * Represents the max number of seconds that the process can live
     * @return
     */
    int getMaxDuration() {
        return (properties.maxDuration as int)
    }

    String getWorkspaceRootFolder() {
        return properties.workspaceFolder.toString()
    }
}

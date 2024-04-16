package io.github.asseco.pst.infrastructure.configproperties.properties

public abstract class PropertiesLoader
{
    static ConfigProperties configProperties
    static LogBackProperties logBackProperties

    /**
     * @return the configProperties
     */
    public static ConfigProperties getConfigProperties() throws Exception
    {
        if (configProperties == null)
        {
            configProperties = new ConfigProperties()
        }

        return configProperties
    }

    /**
     * @return the logBackProperties
     */
    public static LogBackProperties getLogBackProperties(String aScriptName) throws Exception
    {
        if (logBackProperties == null)
        {
            logBackProperties = new LogBackProperties(aScriptName)
        }

        return logBackProperties
    }

}

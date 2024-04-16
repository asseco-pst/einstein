package io.github.asseco.pst.infrastructure.configproperties.properties

public class LogBackProperties extends PropertiesManager
{

    private static final String LOGS_ENV_VARIABLE = "LOGS_ROOT_PATH"
    private static final String LOGS_FOLDER_NAME = "einstein-logs"
    private static final String LOG_NAME = "script.log"
    private String scriptName
    protected Properties configProperties

    /**
     * @param aFileName
     * @throws Exception
     */
    public LogBackProperties(String aScriptName) throws Exception
    {
        super()
        scriptName = aScriptName
        configProperties = PropertiesLoader.getConfigProperties().get()
        setProperties()
    }

    @Override
    protected void setProperties()
    {
        System.setProperty("logsRootPathVariable", getRootFilePath())
    }

    private String getRootFilePath()
    {
        return System.getenv(configProperties.get(LOGS_ENV_VARIABLE)) + File.separator + LOGS_FOLDER_NAME + File.separator + this.scriptName + File.separator
    }

    public String getLogFileFullPath()
    {
        return getRootFilePath() + LOG_NAME;
    }

}

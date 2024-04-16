package io.github.asseco.pst.infrastructure.configproperties.properties

abstract class PropertiesManager
{
    protected String propertiesFileName
    protected Properties properties
    protected Map<String, String> propertiesToSave

    protected PropertiesManager(String aPropertiesFileName) throws Exception
    {
        properties = new Properties()
        propertiesToSave = new HashMap<String, String>()
        propertiesFileName = aPropertiesFileName
    }

    protected PropertiesManager() throws Exception
    {
        properties = new Properties()
        propertiesToSave = new HashMap<String, String>()
    }


    public void load() throws Exception
    {
        InputStream input = null

        try
        {
            // Get the class loader
            ClassLoader classLoader = getClass().classLoader

            // Load the config.properties file as an InputStream
            InputStream inputStream = classLoader.getResourceAsStream("config/config.properties")

            properties.load(inputStream)

        }
        catch (Exception e)
        {
            throw new Exception(e)
        }
        finally
        {
            if (input != null)
            {
                input.close()
            }
        }
    }

    protected abstract void setProperties()

    public Properties get()
    {
        return properties
    }

    public String getProperty(String aPropertyName)
    {
        return properties.getProperty(aPropertyName)
    }
}

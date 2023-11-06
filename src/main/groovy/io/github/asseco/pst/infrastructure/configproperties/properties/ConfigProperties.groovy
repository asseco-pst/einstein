package io.github.asseco.pst.infrastructure.configproperties.properties

class ConfigProperties extends PropertiesManager
{

    /**
     * @param aFileName
     * @throws Exception
     */
    ConfigProperties() throws Exception
    {
        super("config.properties")
        load()
    }

    @Override
    protected void setProperties()
    {
        // TODO
    }

}


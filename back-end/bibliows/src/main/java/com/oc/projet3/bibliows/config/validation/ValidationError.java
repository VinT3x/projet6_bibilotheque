package com.oc.projet3.bibliows.config.validation;

import java.io.IOException;
import java.util.*;

public class ValidationError {

    private Properties properties;
    private final String PROPERTIES_FILE = "/validationsMsg.properties";

    public ValidationError(String propertiesFile) {
        properties = new Properties();
        setProperties(propertiesFile);
    }

    private void setProperties(String pathPropertiesFile){
            try {
                properties.load(getClass().getResourceAsStream(PROPERTIES_FILE));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
    }

    public String getErrorMsg(String keyField){
        String paramFieldMsgValue = properties.getProperty(keyField);

        if (paramFieldMsgValue == null )
            throw new  RuntimeException("Propriété " + keyField + "non présente dans le fichier " + PROPERTIES_FILE );

        return paramFieldMsgValue;
    }
}

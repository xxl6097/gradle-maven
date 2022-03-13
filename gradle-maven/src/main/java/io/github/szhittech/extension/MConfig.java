package io.github.szhittech.extension;

import groovy.lang.MissingPropertyException;
import io.github.szhittech.property.PropertyManager;
import io.github.szhittech.util.StringUtils;
import org.gradle.api.Project;
import org.gradle.api.logging.Logging;
import org.gradle.internal.extensibility.DefaultExtraPropertiesExtension;


public class MConfig {

    public boolean sourceJarEnabled = true;
    public boolean signEnabled = false;
    public boolean javaDocEnabled = false;


    public String version = null;
    public String name = null;
    public String groupId = null;
    public String description = null;
    public String url = null;
    public String connection = null;

    public String authorId;
    public String authorName;
    public String authorEmail;

    private void checkArgs(){
        if (StringUtils.isStringEmpty(version)){
            throw new MissingPropertyException("Please insert code like this 'ext { version = \"0.0.0\" }' in your library/build.gradle");
        }
        if (StringUtils.isStringEmpty(name)){
            throw new MissingPropertyException("Please insert code like this 'ext { name = \"hetlogsdk\" }' in your library/build.gradle");
        }
        if (StringUtils.isStringEmpty(description)){
            description = "A Library for Clife.";
        }
        if (StringUtils.isStringEmpty(url)){
            url = "https://github.com/szhittech/clifesdk.git";
        }
        if (StringUtils.isStringEmpty(connection)){
            connection = "scm:git@github.com:szhittech/clifesdk.git";
        }
        if (StringUtils.isStringEmpty(authorId)){
            authorId = System.getProperty("user.name");
        }
        if (StringUtils.isStringEmpty(authorName)){
            authorName = System.getProperty("user.name");
        }
        if (StringUtils.isStringEmpty(authorEmail)){
            authorEmail = "You Name@clife.cn";
        }
        if (StringUtils.isStringEmpty(groupId)){
            groupId = "io.github.szhittech";
        }
        if (PropertyManager.getInstance().isDebug()){
            Logging.getLogger(getClass()).error("\nname:"+name
                    +"\r\nversion:"+version
                    +"\ndescription:"+description
                    +"\nurl:"+url
                    +"\nconnection:"+connection
                    +"\nauthorId:"+authorId
                    +"\ngroupId:"+groupId
                    +"\nauthorName:"+authorName
                    +"\nauthorEmail:"+authorEmail);
        }
    }


    public MConfig(Project project) {
        Object obj = project.getProperties().get("ext");
        if (obj == null)
            throw new MissingPropertyException("Please insert code like this 'ext {  }' in your library/build.gradle");
        if (obj instanceof DefaultExtraPropertiesExtension){
            DefaultExtraPropertiesExtension extension = (DefaultExtraPropertiesExtension) obj;
            sourceJarEnabled = getBoolValue(extension,"sourceJarEnabled",sourceJarEnabled);
            signEnabled = getBoolValue(extension,"signEnabled",signEnabled);
            javaDocEnabled = getBoolValue(extension,"javaDocEnabled",javaDocEnabled);

            version = getStringValue(extension,"version");
            name = getStringValue(extension,"name");
            groupId = getStringValue(extension,"groupId");
            description = getStringValue(extension,"description");
            url = getStringValue(extension,"url");
            connection = getStringValue(extension,"connection");

            authorId = getStringValue(extension,"authorId");
            authorName = getStringValue(extension,"authorName");
            authorEmail = getStringValue(extension,"authorEmail");
        }

        checkArgs();
    }

    private boolean getBoolValue(DefaultExtraPropertiesExtension extension,String key,boolean defaultValue){
        if (extension == null || key == null)
            return defaultValue;
        if (!extension.has(key))
            return defaultValue;
        return (boolean) extension.getProperty(key);
    }


    private String getStringValue(DefaultExtraPropertiesExtension extension,String key){
        if (extension == null || key == null)
            return null;
        if (!extension.has(key))
            return null;
        return (String) extension.getProperty(key);
    }

}

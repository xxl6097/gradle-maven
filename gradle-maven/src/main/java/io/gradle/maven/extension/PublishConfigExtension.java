package io.gradle.maven.extension;


import io.gradle.maven.util.Logc;
import io.gradle.maven.util.StringUtils;

public class PublishConfigExtension {
    /**
     * The source code should be published default, otherwise not
     */
    public boolean sourceJarEnabled = true;

    /**
     * The Signing Plugin is used to generate a signature file for each artifact.
     * Since Gradle Version 4.8
     */
    public boolean signEnabled = false;

    /**
     * The java doc should be published default, otherwise not
     */
    public boolean javaDocEnabled = true;


    public String version = null;
    public String name = null;
    public String description = null;
    public String url = null;
    public String connection = null;

    public String authorId;
    public String authorName;
    public String authorEmail;

    public void checkArgs(){
        if (StringUtils.isStringEmpty(version)){
            throw new NullPointerException("Please insert code like this 'pubconfig { version = \"0.0.0\" }' in your library/build.gradle");
        }
        if (StringUtils.isStringEmpty(name)){
            throw new NullPointerException("Please insert code like this 'pubconfig { name = \"hetlogsdk\" }' in your library/build.gradle");
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
        Logc.e("\nname:"+name
                +"\r\nversion:"+version
                +"\ndescription:"+description
                +"\nurl:"+url
                +"\nconnection:"+connection
                +"\nauthorId:"+authorId
                +"\nauthorName:"+authorName
                +"\nauthorEmail:"+authorEmail);
    }
}

package io.gradle.maven.extension;

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
    public boolean javaDocEnabled = false;


    public String version = "";
    public String name = "";
}

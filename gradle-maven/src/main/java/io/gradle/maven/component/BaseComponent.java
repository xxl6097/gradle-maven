package io.gradle.maven.component;

import io.gradle.maven.extension.PublishConfigExtension;
import org.gradle.api.Project;
import org.gradle.api.publish.maven.MavenPublication;

public abstract class BaseComponent {
    protected Project project;

    public BaseComponent(Project project) {
        this.project = project;
    }

    public void buildComponent(MavenPublication mavenPublication, PublishConfigExtension extension){
        fromComponent(mavenPublication);

        if (extension.sourceJarEnabled) {
            mavenPublication.artifact(sourcesJar());
        }
        if (extension.javaDocEnabled) {
            mavenPublication.artifact(docJar());
        }
        withPom(mavenPublication);
    }

    protected abstract void fromComponent(MavenPublication mavenPublication);

    protected abstract Object docJar();

    protected abstract Object sourcesJar();

    protected Object withPom(MavenPublication mavenPublication) {return null;}
}

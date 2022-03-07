package io.gradle.maven.component;

import io.gradle.maven.BaseComponent;
import org.gradle.api.Project;
import org.gradle.api.publish.maven.MavenPublication;

public class JavaComponent extends BaseComponent {
    public JavaComponent(Project project) {
        super(project);
    }

    @Override
    protected Object fromComponent(MavenPublication mavenPublication) {
         mavenPublication.from(project.getComponents().getByName("java"));
        return null;
    }

    @Override
    protected Object docJar() {
        return null;
    }

    @Override
    protected Object sourcesJar() {
        return null;
    }
}

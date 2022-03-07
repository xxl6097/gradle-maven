package io.gradle.maven.component;

import io.gradle.maven.BaseComponent;
import org.gradle.api.Project;
import org.gradle.api.publish.maven.MavenPublication;

public class AndroidComponent extends BaseComponent {
    public AndroidComponent(Project project) {
        super(project);
    }

    @Override
    protected Object fromComponent(MavenPublication mavenPublication) {
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

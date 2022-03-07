package io.gradle.maven.component;

import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.tasks.bundling.Jar;

public class JavaComponent extends BaseComponent {
    public JavaComponent(Project project) {
        super(project);
    }

    @Override
    protected void fromComponent(MavenPublication mavenPublication) {
        mavenPublication.from(project.getComponents().getByName("java"));
    }

    @Override
    protected Object docJar() {
        Jar javadocJar = project.getTasks().maybeCreate("javadocJar", Jar.class);
        javadocJar.from(project.getTasks().getByName("javadoc"));
        javadocJar.setClassifier("javadoc");
        return javadocJar;
    }

    @Override
    protected Object sourcesJar() {
        Jar sourcesJar = project.getTasks().maybeCreate("sourcesJar", Jar.class);
        JavaPluginConvention javaPluginConvention = project.getConvention().getPlugin(JavaPluginConvention.class);
        sourcesJar.from(javaPluginConvention.getSourceSets().getByName("main").getAllJava());
        sourcesJar.setClassifier("sources");
        return sourcesJar;
    }
}

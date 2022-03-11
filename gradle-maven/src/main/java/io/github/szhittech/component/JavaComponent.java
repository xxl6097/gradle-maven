package io.github.szhittech.component;

import io.github.szhittech.component.base.BaseComponent;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.external.javadoc.MinimalJavadocOptions;
import org.gradle.external.javadoc.StandardJavadocDocletOptions;

public class JavaComponent extends BaseComponent {
    public JavaComponent(Project project) {
        super(project);
    }

    @Override
    protected void fromComponent(MavenPublication mavenPublication) {
        mavenPublication.from(project.getComponents().getByName("java"));
        addOptions();
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

    private void addOptions(){
        project.getTasks().withType(Javadoc.class, new Action<Javadoc>() {
            @Override
            public void execute(Javadoc javadoc) {
                javadoc.options(new Action<MinimalJavadocOptions>() {
                    @Override
                    public void execute(MinimalJavadocOptions minimalJavadocOptions) {
                        StandardJavadocDocletOptions options = (StandardJavadocDocletOptions) minimalJavadocOptions;
                        options.addStringOption("Xdoclint:none", "-quiet");
                        options.addStringOption("encoding", "UTF-8");
                        options.addStringOption("charSet", "UTF-8");
                    }
                });
            }
        });
    }
}

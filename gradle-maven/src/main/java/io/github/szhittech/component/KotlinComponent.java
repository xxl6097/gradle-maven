package io.github.szhittech.component;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.tasks.bundling.Jar;
import org.jetbrains.dokka.gradle.DokkaPlugin;

public class KotlinComponent extends JavaComponent{

    public KotlinComponent(Project project) {
        super(project);
        applyDokkaPlugin();
    }


    private void applyDokkaPlugin() {
        //No more duplicate apply plugin if already had
        if (!project.getPlugins().hasPlugin("org.jetbrains.dokka")) {
            project.getPlugins().apply(DokkaPlugin.class);
        }
    }

    @Override
    protected Object docJar() {
        Task dokka = project.getTasks().getByName("dokka");
        dokka.setProperty("outputFormat", "html");
        dokka.setProperty("outputDirectory", "${project.buildDir}/javadoc");

        Jar dokkaJar = project.getTasks().maybeCreate("dokkaJar", Jar.class);
        dokkaJar.setGroup(JavaBasePlugin.DOCUMENTATION_GROUP);
        dokkaJar.setDescription("Assembles Kotlin docs with Dokka");
        dokkaJar.setClassifier("javadoc");
        dokkaJar.from(dokka);
        return dokkaJar;
    }

}

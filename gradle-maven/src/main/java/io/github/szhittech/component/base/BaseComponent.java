package io.github.szhittech.component.base;


import io.github.szhittech.extension.MConfig;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.publish.maven.*;
import org.gradle.api.tasks.TaskCollection;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.external.javadoc.MinimalJavadocOptions;
import org.gradle.external.javadoc.StandardJavadocDocletOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.function.Consumer;

public abstract class BaseComponent {
    protected Project project;

    public BaseComponent(Project project) {
        this.project = project;
    }

    public void buildComponent(MavenPublication mavenPublication, MConfig config){
        fromComponent(mavenPublication);

        if (config.sourceJarEnabled) {
            mavenPublication.artifact(sourcesJar());
        }

        if (config.javaDocEnabled) {
            mavenPublication.artifact(docJar());
        }

        withPom(mavenPublication,config);
    }

    protected abstract void fromComponent(MavenPublication mavenPublication);

    protected abstract Object docJar();

    protected abstract Object sourcesJar();

    protected Object withPom(final MavenPublication mavenPublication, final MConfig config) {
        mavenPublication.pom(new Action<MavenPom>() {
            @Override
            public void execute(MavenPom mavenPom) {
                mavenPom.getName().set(config.name);
                mavenPom.getDescription().set(config.description);
                mavenPom.getUrl().set(config.url);
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                Calendar calendar = Calendar.getInstance();
                String dateName = df.format(calendar.getTime());
                mavenPom.getInceptionYear().set(dateName);

                mavenPom.scm(new Action<MavenPomScm>() {
                    @Override
                    public void execute(MavenPomScm mavenPomScm) {
                        mavenPomScm.getConnection().set(config.connection);
                        mavenPomScm.getUrl().set(config.url);
                        mavenPomScm.getDeveloperConnection().set(config.connection);
                    }
                });
                mavenPom.developers(new Action<MavenPomDeveloperSpec>() {
                    @Override
                    public void execute(MavenPomDeveloperSpec mavenPomDeveloperSpec) {
                        mavenPomDeveloperSpec.developer(new Action<MavenPomDeveloper>() {
                            @Override
                            public void execute(MavenPomDeveloper mavenPomDeveloper) {
                                mavenPomDeveloper.getId().set(config.authorId);
                                mavenPomDeveloper.getName().set(config.authorName);
                                mavenPomDeveloper.getEmail().set(config.authorEmail);
                            }
                        });
                    }
                });
                mavenPom.licenses(new Action<MavenPomLicenseSpec>() {
                    @Override
                    public void execute(MavenPomLicenseSpec mavenPomLicenseSpec) {
                        mavenPomLicenseSpec.license(new Action<MavenPomLicense>() {
                            @Override
                            public void execute(MavenPomLicense mavenPomLicense) {
                                mavenPomLicense.getUrl().set("http://www.apache.org/licenses/LICENSE-2.0.txt");
                                mavenPomLicense.getDistribution().set(config.description);
                                mavenPomLicense.getName().set("The Apache License, Version 2.0");
                            }
                        });
                    }
                });
            }
        });

        return null;
    }

}

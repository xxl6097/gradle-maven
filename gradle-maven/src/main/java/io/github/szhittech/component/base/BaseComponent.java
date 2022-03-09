package io.github.szhittech.component.base;


import io.github.szhittech.extension.PublishConfigExtension;
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

    public void buildComponent(MavenPublication mavenPublication, PublishConfigExtension extension){
        fromComponent(mavenPublication);

        if (extension.sourceJarEnabled) {
            mavenPublication.artifact(sourcesJar());
        }

        TaskCollection<Javadoc> javadocs  =project.getTasks().withType(Javadoc.class);
        javadocs.forEach(new Consumer<Javadoc>() {
            @Override
            public void accept(Javadoc javadoc) {
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

        if (extension.javaDocEnabled) {
            mavenPublication.artifact(docJar());
        }

        withPom(mavenPublication,extension);
    }

    protected abstract void fromComponent(MavenPublication mavenPublication);

    protected abstract Object docJar();

    protected abstract Object sourcesJar();

    protected Object withPom(final MavenPublication mavenPublication, final PublishConfigExtension extension) {
        mavenPublication.pom(new Action<MavenPom>() {
            @Override
            public void execute(MavenPom mavenPom) {
                mavenPom.getName().set(extension.name);
                mavenPom.getDescription().set(extension.description);
                mavenPom.getUrl().set(extension.url);
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                Calendar calendar = Calendar.getInstance();
                String dateName = df.format(calendar.getTime());
                mavenPom.getInceptionYear().set(dateName);

                mavenPom.scm(new Action<MavenPomScm>() {
                    @Override
                    public void execute(MavenPomScm mavenPomScm) {
                        mavenPomScm.getConnection().set(extension.connection);
                        mavenPomScm.getUrl().set(extension.url);
                        mavenPomScm.getDeveloperConnection().set(extension.connection);
                    }
                });
                mavenPom.developers(new Action<MavenPomDeveloperSpec>() {
                    @Override
                    public void execute(MavenPomDeveloperSpec mavenPomDeveloperSpec) {
                        mavenPomDeveloperSpec.developer(new Action<MavenPomDeveloper>() {
                            @Override
                            public void execute(MavenPomDeveloper mavenPomDeveloper) {
                                mavenPomDeveloper.getId().set(extension.authorId);
                                mavenPomDeveloper.getName().set(extension.authorName);
                                mavenPomDeveloper.getEmail().set(extension.authorEmail);
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
                                mavenPomLicense.getDistribution().set(extension.description);
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

package io.gradle.maven.component;

import io.gradle.maven.extension.PublishConfigExtension;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.publish.maven.MavenPom;
import org.gradle.api.publish.maven.MavenPomDeveloper;
import org.gradle.api.publish.maven.MavenPomDeveloperSpec;
import org.gradle.api.publish.maven.MavenPomLicense;
import org.gradle.api.publish.maven.MavenPomLicenseSpec;
import org.gradle.api.publish.maven.MavenPomScm;
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

    protected Object withPom(MavenPublication mavenPublication) {
        mavenPublication.pom(new Action<MavenPom>() {
            @Override
            public void execute(MavenPom mavenPom) {
                mavenPom.scm(new Action<MavenPomScm>() {
                    @Override
                    public void execute(MavenPomScm mavenPomScm) {
                        mavenPomScm.getConnection().set("scm:git@github.com:szhittech/hetjavasdk.git");
                        mavenPomScm.getUrl().set("https://github.com/szhittech/hetjavasdk");
                        mavenPomScm.getDeveloperConnection().set("scm:git@github.com:szhittech/hetjavasdk.git");
                    }
                });
                mavenPom.developers(new Action<MavenPomDeveloperSpec>() {
                    @Override
                    public void execute(MavenPomDeveloperSpec mavenPomDeveloperSpec) {
                        mavenPomDeveloperSpec.developer(new Action<MavenPomDeveloper>() {
                            @Override
                            public void execute(MavenPomDeveloper mavenPomDeveloper) {
                                mavenPomDeveloper.getId().set("uuxia");
                                mavenPomDeveloper.getName().set("xiaxiaoli");
                                mavenPomDeveloper.getEmail().set("xiaoli.xia@clife.cn");
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
                                mavenPomLicense.getDistribution().set("A Library");
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

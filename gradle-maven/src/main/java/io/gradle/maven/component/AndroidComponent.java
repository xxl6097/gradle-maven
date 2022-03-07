package io.gradle.maven.component;

import com.android.build.gradle.LibraryExtension;
import groovy.util.Node;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.XmlProvider;
import org.gradle.api.artifacts.ExcludeRule;
import org.gradle.api.artifacts.ModuleDependency;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.publish.maven.MavenPom;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.javadoc.Javadoc;

import java.util.function.Consumer;

public class AndroidComponent extends BaseComponent {

    private LibraryExtension android = (LibraryExtension)project.getExtensions().getByName("android");

    public AndroidComponent(Project project) {
        super(project);
        System.err.println("---->" + android.getClass().toString());
    }

    @Override
    protected void fromComponent(MavenPublication mavenPublication) {
        Task bundleReleaseAar  = null;
        if (project.getTasks().findByName("bundleReleaseAar") != null) {
            bundleReleaseAar = project.getTasks().getByName("bundleReleaseAar");
        }
        //we only use bundleRelease in lower android gradle plugin version such as 2.3.3
        //more information look this https://stackoverflow.com/questions/51433769/why-android-gradle-maven-publish-artifact-bundlerelease-not-found/51869825#51869825
        if (project.getTasks().findByName("bundleRelease") != null) {
            bundleReleaseAar = project.getTasks().getByName("bundleRelease");
        }
        mavenPublication.artifact(bundleReleaseAar);
    }

    @Override
    protected Object docJar() {
        Javadoc androidJavaDocs = project.getTasks().create("androidJavadocs", Javadoc.class);
        androidJavaDocs.setSource(android.getSourceSets().getByName("main").getJava().getSrcDirs());
//        androidJavaDocs.classpath += project.files("${android.bootClasspath}${File.pathSeparator}");
        ConfigurableFileCollection classpath = project.files(androidJavaDocs.getClasspath().getAsPath() + "${android.bootClasspath}${File.pathSeparator}");
        androidJavaDocs.setClasspath(classpath);

        Jar androidJavaDocsJar = project.getTasks().create("androidJavaDocsJar", Jar.class);
        androidJavaDocsJar.setClassifier("javadoc");
        androidJavaDocsJar.from(androidJavaDocs.getDestinationDir());
        androidJavaDocsJar.dependsOn(androidJavaDocs);
        return androidJavaDocsJar;
    }

    @Override
    protected Object sourcesJar() {
        Jar androidSourcesJar = project.getTasks().create("androidSourcesJar", Jar.class);
        androidSourcesJar.from(android.getSourceSets().getByName("main").getJava().getSrcDirs());
        androidSourcesJar.setClassifier("sources");
        return androidSourcesJar;
    }

    @Override
    protected Object withPom(MavenPublication mavenPublication) {
        mavenPublication.pom(new Action<MavenPom>() {
            @Override
            public void execute(MavenPom mavenPom) {
                mavenPom.withXml(new Action<XmlProvider>() {
                    @Override
                    public void execute(XmlProvider xmlProvider) {
                        final Node dependenciesNode = xmlProvider.asNode().appendNode("dependencies");
                        project.getConfigurations().getByName("compile").getDependencies().forEach(new Consumer<Dependency>() {
                            @Override
                            public void accept(Dependency dep) {
                                addDependency(dep,"compile",dependenciesNode);
                            }
                        });
                        //support api & implementation configuration until gradle version 3.4

                        if (project.getConfigurations().findByName("api") != null) {
                            // List all "api" dependencies (for new Gradle) as "compile" dependencies
                            project.getConfigurations().getByName("api").getDependencies().forEach(new Consumer<Dependency>() {
                                @Override
                                public void accept(Dependency dep) {
                                    addDependency(dep, "api",dependenciesNode);
                                }
                            });
                        }

                        if (project.getConfigurations().findByName("implementation") != null) {
                            // List all "implementation" dependencies (for new Gradle) as "runtime" dependencies
                            project.getConfigurations().getByName("implementation").getDependencies().forEach(new Consumer<Dependency>() {
                                @Override
                                public void accept(Dependency dep) {
                                    addDependency(dep, "runtime",dependenciesNode);
                                }
                            });
                        }
                    }
                });
            }
        });
        return super.withPom(mavenPublication);
    }

    void addDependency(Dependency dep, String scope,Node dependenciesNode){
        if (dep.getGroup() == null || dep.getVersion() == null || dep.getName() == "unspecified") {
            return ;// ignore invalid dependencies
        }
        if (dep instanceof ModuleDependency){
            Node dependencyNode = dependenciesNode.appendNode("dependency");
            dependencyNode.appendNode("groupId",dep.getGroup());
            dependencyNode.appendNode("artifactId",dep.getName());
            dependencyNode.appendNode("version",dep.getVersion());
            dependencyNode.appendNode("scope",scope);
            ((ModuleDependency) dep).getArtifacts().forEach(depArtifact->{
                dependencyNode.appendNode("type",depArtifact.getType());
            });

            Node exclusionsNode = dependencyNode.appendNode("exclusions");

            if (!((ModuleDependency) dep).isTransitive()){
                Node exclusionNode = exclusionsNode.appendNode("exclusion");
                exclusionNode.appendNode("groupId","*");
                exclusionNode.appendNode("artifactId","*");
            }else if(!((ModuleDependency) dep).getExcludeRules().isEmpty()){
                ((ModuleDependency) dep).getExcludeRules().forEach(new Consumer<ExcludeRule>() {
                    @Override
                    public void accept(ExcludeRule rule) {
                        Node exclusionNode = exclusionsNode.appendNode("exclusion");
                        exclusionNode.appendNode("groupId", rule.getGroup()!=null ? rule.getGroup(): "*");
                        exclusionNode.appendNode("artifactId", rule.getModule()!=null ? rule.getModule(): "*");
                    }
                });
            }else{
                //do nothing
            }

        }
    }

}

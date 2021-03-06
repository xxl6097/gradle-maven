package io.github.szhittech.component;

import com.android.build.gradle.LibraryExtension;
import com.android.build.gradle.api.AndroidSourceSet;
import com.android.build.gradle.api.LibraryVariant;
import com.android.build.gradle.internal.api.LibraryVariantImpl;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.XmlProvider;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ExcludeRule;
import org.gradle.api.artifacts.ModuleDependency;
import org.gradle.api.file.FileCollection;
import org.gradle.api.publish.maven.MavenPom;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.external.javadoc.MinimalJavadocOptions;
import org.gradle.external.javadoc.StandardJavadocDocletOptions;

import java.io.File;
import java.util.Set;
import java.util.function.Consumer;

import groovy.util.Node;
import io.github.szhittech.component.base.BaseComponent;
import io.github.szhittech.extension.MConfig;

public class AndroidComponent extends BaseComponent {

    private FileCollection classpath = null;

    private LibraryExtension android = (LibraryExtension)project.getExtensions().getByName("android");

    public AndroidComponent(Project project) {
        super(project);
        //System.err.println("---->" + android.getClass().toString());
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

        if (bundleReleaseAar != null) {
            mavenPublication.artifact(bundleReleaseAar);
        }

        addOptions();
    }

    @Override
    protected Object docJar() {
        final Javadoc androidJavaDocs = project.getTasks().create("androidJavadocs", Javadoc.class);
        //addOptions(androidJavaDocs);
        AndroidSourceSet sourceSet =  android.getSourceSets().getByName("main");//
        Set<File> source = sourceSet.getJava().getSrcDirs();
        androidJavaDocs.source(source);
        classpath = androidJavaDocs.getClasspath();
        android.getBootClasspath().forEach(new Consumer<File>() {
            @Override
            public void accept(File file) {
                classpath = classpath.plus(project.files(file));
                androidJavaDocs.setClasspath(classpath);
            }
        });

        android.getLibraryVariants().all(new Action<LibraryVariant>() {
            @Override
            public void execute(LibraryVariant libraryVariant) {
                LibraryVariantImpl variant = (LibraryVariantImpl) libraryVariant;
                if (variant.getName().equalsIgnoreCase("release")) {
                    FileCollection collection = variant.getJavaCompileProvider().get().getClasspath();
                    classpath = classpath.plus(collection);
                    //Logging.getLogger(getClass()).error("1---->{}", classpath.getFiles());
                    androidJavaDocs.setClasspath(classpath);
                }

            }
        });
        //androidJavaDocs.exclude("**/R.html", "**/R.*.html", "**/index.html");

        //Logging.getLogger(getClass()).error("2---->{}", classpath.getFiles());

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
    protected Object withPom(MavenPublication mavenPublication, MConfig extension) {
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
        return super.withPom(mavenPublication, extension);
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

    private void addOptions(Javadoc javadoc){
        javadoc.options(new Action<MinimalJavadocOptions>() {
            @Override
            public void execute(MinimalJavadocOptions minimalJavadocOptions) {
                StandardJavadocDocletOptions options = (StandardJavadocDocletOptions) minimalJavadocOptions;
                options.addStringOption("Xdoclint:none", "-quiet");
//                options.addStringOption("encoding", "UTF-8");
//                options.addStringOption("charSet", "UTF-8");
//                options.addStringOption("locale", "en_US");
//                options.setMemberLevel(JavadocMemberLevel.PUBLIC);
                options.charSet("UTF-8");
                options.docEncoding("UTF-8");
            }
        });
    }

}

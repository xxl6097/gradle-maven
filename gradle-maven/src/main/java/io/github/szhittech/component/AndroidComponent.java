package io.github.szhittech.component;

import com.android.build.gradle.LibraryExtension;
import com.android.build.gradle.api.AndroidSourceSet;
import com.android.build.gradle.api.LibraryVariant;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.XmlProvider;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ExcludeRule;
import org.gradle.api.artifacts.ModuleDependency;
import org.gradle.api.internal.DefaultDomainObjectSet;
import org.gradle.api.publish.maven.MavenPom;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.external.javadoc.JavadocMemberLevel;
import org.gradle.external.javadoc.MinimalJavadocOptions;
import org.gradle.external.javadoc.StandardJavadocDocletOptions;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

import groovy.util.Node;
import io.github.szhittech.component.base.BaseComponent;
import io.github.szhittech.extension.MConfig;

public class AndroidComponent extends BaseComponent {

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
        mavenPublication.artifact(bundleReleaseAar);

    }

    @Override
    protected Object docJar() {
        final Javadoc androidJavaDocs = project.getTasks().create("androidJavadocs", Javadoc.class);
        androidJavaDocs.setEnabled(false);
        androidJavaDocs.setFailOnError(false);
        addOptions(androidJavaDocs);
        AndroidSourceSet sourceSet =  android.getSourceSets().getByName("main");//AndroidSourceSet
        androidJavaDocs.setSource(sourceSet.getJava().getSrcDirs());
//        List<File> bootClasspath = android.getBootClasspath();
//        Object[] objs = new Object[]{bootClasspath + File.pathSeparator};
//        ConfigurableFileCollection files = project.files(objs);
//        androidJavaDocs.getClasspath().plus(files);
//        android.getLibraryVariants().forEach(new Consumer<LibraryVariant>() {
//            @Override
//            public void accept(LibraryVariant libraryVariant) {
//                androidJavaDocs.getClasspath().plus(project.files(libraryVariant.getJavaCompile().getOutputs().files()));
//            }
//        });

//        DefaultDomainObjectSet<LibraryVariant> list = android.getLibraryVariants();
//        FileCollection classpath = list.stream().findAny().get().getJavaCompile().getClasspath();
//        androidJavaDocs.getClasspath().plus(classpath);
//        TaskOutputFilePropertyBuilder outfiels = list.stream().findAny().get().getJavaCompile().getOutputs().files();
//        androidJavaDocs.getClasspath().plus(project.files(outfiels));

        List<File> bootClasspath = android.getBootClasspath();
        Object[] objs = new Object[]{bootClasspath + File.pathSeparator};
        androidJavaDocs.getClasspath().plus(project.files(objs));
        DefaultDomainObjectSet<LibraryVariant> variants = android.getLibraryVariants();
        variants.all(new Action<LibraryVariant>() {
            @Override
            public void execute(LibraryVariant variant) {
                if (variant.getName().equalsIgnoreCase("release")) {
                    androidJavaDocs.getClasspath().plus(variant.getJavaCompileProvider().get().getClasspath());
                }
            }
        });
        androidJavaDocs.exclude("**/R.html", "**/R.*.html", "**/index.html");


        Jar androidJavaDocsJar = project.getTasks().create("androidJavaDocsJar", Jar.class);
        androidJavaDocsJar.setClassifier("javadoc");
        androidJavaDocsJar.from(androidJavaDocs.getDestinationDir());
        androidJavaDocsJar.dependsOn(androidJavaDocs);
        return androidJavaDocsJar;

//        Javadoc androidJavaDocs = (Javadoc)this.project.getTasks().create("androidJavadocs", Javadoc.class);
//        Object tmp37_32 = this.android.getSourceSets().getByName("main"); Intrinsics.checkExpressionValueIsNotNull(tmp37_32, "android.sourceSets.getByName(\"main\")");
//        AndroidSourceDirectorySet tmp51_46 = ((AndroidSourceSet)tmp37_32).getJava(); Intrinsics.checkExpressionValueIsNotNull(tmp51_46, "android.sourceSets.getByName(\"main\").java"); androidJavaDocs.setSource(tmp51_46.getSrcDirs());
//        Javadoc tmp66_65 = androidJavaDocs; Intrinsics.checkExpressionValueIsNotNull(tmp66_65, "androidJavaDocs");
//        Javadoc tmp72_66 = tmp66_65; tmp72_66.setClasspath(tmp72_66.getClasspath().plus((FileCollection)this.project.files(new Object[] { this.android.getBootClasspath() + File.pathSeparator })));
//
//        Jar androidJavaDocsJar = (Jar)this.project.getTasks().create("androidJavaDocsJar", Jar.class);
//        Jar tmp152_151 = androidJavaDocsJar; Intrinsics.checkExpressionValueIsNotNull(tmp152_151, "androidJavaDocsJar"); tmp152_151.setClassifier("javadoc");
//        androidJavaDocsJar.from(new Object[] { androidJavaDocs.getDestinationDir() });
//        androidJavaDocsJar.dependsOn(new Object[] { androidJavaDocs });
//        return androidJavaDocsJar;
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
                options.addStringOption("encoding", "UTF-8");
                options.addStringOption("charSet", "UTF-8");
                options.addStringOption("locale", "en_US");
                options.setMemberLevel(JavadocMemberLevel.PUBLIC);
                options.charSet("UTF-8");
                options.docEncoding("UTF-8");
            }
        });
    }

}

package io.gradle.maven;

import groovy.lang.Closure;
import io.gradle.maven.component.AndroidComponent;
import io.gradle.maven.component.JavaComponent;
import io.gradle.maven.component.KotlinComponent;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.artifacts.repositories.PasswordCredentials;
import org.gradle.api.publish.Publication;
import org.gradle.api.publish.PublicationContainer;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;
import org.gradle.plugins.signing.SigningExtension;
import org.gradle.plugins.signing.SigningPlugin;

import java.net.URI;

public class PublishGradle implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        final GradlePublishExtension extension = project.getExtensions().create("GradleConfig",GradlePublishExtension.class);
        project.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(Project project) {
                configurePublishing(project, extension);
                if (extension.version.endsWith("-SNAPSHOT")){
                    configureSigning(project);
                }
            }
        });
    }


    void configurePublishing(final Project project, final GradlePublishExtension extension){
        project.getPluginManager().apply(MavenPublishPlugin.class);

        project.getPlugins().withType(MavenPublishPlugin.class, new Action<MavenPublishPlugin>() {
            @Override
            public void execute(MavenPublishPlugin mavenPublishPlugin) {

                project.getExtensions().configure(PublishingExtension.class, new Action<PublishingExtension>() {
                    @Override
                    public void execute(PublishingExtension publishing) {
                        publishing.publications(new Action<PublicationContainer>() {
                            @Override
                            public void execute(PublicationContainer publication) {
                                publication.create("maven", new Action<Publication>() {
                                    @Override
                                    public void execute(Publication publication) {
                                        MavenPublication maven = (MavenPublication) publication;
                                        if(maven != null){
                                            maven.setVersion(project.getVersion().toString());
                                            componentLibrary(project).buildComponent(maven,extension);
                                        }
                                    }
                                });
                            }
                        });
                        //

                        publishing.repositories(new Action<RepositoryHandler>() {
                            @Override
                            public void execute(RepositoryHandler repository) {
                                repository.maven(new Action<MavenArtifactRepository>() {
                                    @Override
                                    public void execute(MavenArtifactRepository mavenRepository) {
                                        URI uri = URI.create("");//(version.endsWith("-SNAPSHOT")) extension.snapshotRepository else extension.releaseRepository
                                        mavenRepository.setUrl(uri);
                                        mavenRepository.credentials(new Action<PasswordCredentials>() {
                                            @Override
                                            public void execute(PasswordCredentials credential) {
                                                credential.setUsername("");
                                                credential.setPassword("");
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }


    BaseComponent componentLibrary(Project project){
        if (project.getPlugins().hasPlugin("java-library") && !project.getPlugins().hasPlugin("org.jetbrains.kotlin.jvm")){
            return new JavaComponent(project);
        }else if (project.getPlugins().hasPlugin("com.android.library")){
            return new AndroidComponent(project);
        }else if (project.getPlugins().hasPlugin("org.jetbrains.kotlin.jvm")){
            return new KotlinComponent(project);
        }
        return null;
    }


    void configureSigning(final Project project) {
        project.getPluginManager().apply(SigningPlugin.class);
        project.getPlugins().withType(SigningPlugin.class, new Action<SigningPlugin>() {
            @Override
            public void execute(SigningPlugin signingPlugin) {
                project.getExtensions().configure(SigningExtension.class, new Action<SigningExtension>() {
                    @Override
                    public void execute(SigningExtension signing) {
                        PublishingExtension publishing = (PublishingExtension) project.getExtensions().getByName("publishing");
                        signing.sign(publishing.getPublications().getByName("maven"));
                    }
                });
            }
        });
    }
}

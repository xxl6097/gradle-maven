package io.gradle.maven;

import io.gradle.maven.component.AndroidComponent;
import io.gradle.maven.component.BaseComponent;
import io.gradle.maven.component.JavaComponent;
import io.gradle.maven.component.KotlinComponent;
import io.gradle.maven.extension.PublishConfigExtension;
import io.gradle.maven.manager.PropertyManager;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.artifacts.repositories.PasswordCredentials;
import org.gradle.api.component.SoftwareComponent;
import org.gradle.api.publish.Publication;
import org.gradle.api.publish.PublicationContainer;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;
import org.gradle.plugins.signing.SigningExtension;
import org.gradle.plugins.signing.SigningPlugin;
import org.gradle.util.GradleVersion;

import java.net.URI;
import java.util.function.Consumer;

public class PublishGradlePlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        PropertyManager.getInstance().init(project);
        final PublishConfigExtension extension = project.getExtensions().create("pubconfig", PublishConfigExtension.class);
        project.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(Project project) {
                configurePublishing(project, extension);
                if (GradleVersion.current().compareTo(GradleVersion.version("4.8")) >= 0 && !extension.version.endsWith("-SNAPSHOT")) {
                    configureSigning(project);
                }
            }
        });
    }

    void configurePublishing(final Project project, final PublishConfigExtension extension) {
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
                                publication.create("maven", MavenPublication.class, new Action<MavenPublication>() {
                                    @Override
                                    public void execute(MavenPublication maven) {
                                        maven.setVersion(extension.version);
                                        maven.setArtifactId(extension.name);
                                        maven.setGroupId(PropertyManager.getInstance().getEntity().group);
                                        BaseComponent component = componentLibrary(project);
                                        if (component == null){
                                            throw new NullPointerException("BaseComponent Is NUll");
                                        }
                                        component.buildComponent(maven, extension);
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
                                        String version = extension.version;
                                        String repourl = version.endsWith("-SNAPSHOT") ? PropertyManager.getInstance().getEntity().snapshotUrl : PropertyManager.getInstance().getEntity().releaseUrl;

                                        URI uri = URI.create(repourl);
                                        mavenRepository.setUrl(uri);
                                        mavenRepository.credentials(new Action<PasswordCredentials>() {
                                            @Override
                                            public void execute(PasswordCredentials credential) {
                                                credential.setUsername(PropertyManager.getInstance().getEntity().repoUserName);
                                                credential.setPassword(PropertyManager.getInstance().getEntity().repoPassword);
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


    private BaseComponent componentLibrary(Project project) {
        project.getPlugins().forEach(new Consumer<Plugin>() {
            @Override
            public void accept(Plugin plugin) {
                Logc.e("-->"+plugin.toString());
            }
        });


        Plugin tmp = project.getPlugins().findPlugin("com.android.library");
        if (tmp != null){
            Logc.e("1111++>"+tmp.toString());
        }
        tmp = project.getPlugins().findPlugin("java");
        if (tmp != null){
            Logc.e("2222222++>"+tmp.toString());
        }
        tmp = project.getPlugins().findPlugin("org.jetbrains.kotlin.jvm");
        if (tmp != null){
            Logc.e("33333++>"+tmp.toString());
        }
        tmp = project.getPlugins().findPlugin("java-library");
        if (tmp != null){
            Logc.e("44444++>"+tmp.toString());
        }

        if (project.getPlugins().hasPlugin("java")) {
            Logc.i("java");
            return new JavaComponent(project);
        } else if (project.getPlugins().hasPlugin("com.android.library")) {
            Logc.i("com.android.library");
            return new AndroidComponent(project);
        } else if (project.getPlugins().hasPlugin("org.jetbrains.kotlin.jvm")) {
            Logc.i("org.jetbrains.kotlin.jvm");
            return new KotlinComponent(project);
        }else{
            Logc.i("nulllllllll");
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

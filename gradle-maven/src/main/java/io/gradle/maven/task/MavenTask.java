package io.gradle.maven.task;

import io.gradle.maven.PublishGradlePlugin;
import io.gradle.maven.component.AndroidComponent;
import io.gradle.maven.component.BaseComponent;
import io.gradle.maven.component.JavaComponent;
import io.gradle.maven.component.KotlinComponent;
import io.gradle.maven.extension.PublishConfigExtension;
import io.gradle.maven.manager.PropertyManager;
import io.gradle.maven.util.Logc;
import io.gradle.maven.util.StringUtils;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.artifacts.repositories.PasswordCredentials;
import org.gradle.api.publish.PublicationContainer;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;
import org.gradle.api.tasks.TaskAction;
import org.gradle.plugins.signing.SigningExtension;
import org.gradle.plugins.signing.SigningPlugin;
import org.gradle.util.GradleVersion;

import java.net.URI;

public class MavenTask extends DefaultTask {
    PublishConfigExtension extension;
    PublishingExtension publishing;
    private PublishGradlePlugin publishGradlePlugin;

    public void setPublishing(PublishingExtension publishing) {
        this.publishing = publishing;
    }

    public void setExtension(PublishConfigExtension extension) {
        this.extension = extension;
    }

    @TaskAction
    public void taskAction(){
        Project project = getProject();
        if (project != project.getRootProject()){
        }
        Logc.e("---->MavenTask.taskAction");
//        createMavenJavaPublications(project, publishing, extension);
//        makeMavenRepository(publishing,extension);
    }
    void configurePublishing(final Project project, final PublishConfigExtension extension) {
        project.getPluginManager().apply(MavenPublishPlugin.class);

        project.getPlugins().withType(MavenPublishPlugin.class, new Action<MavenPublishPlugin>() {
            @Override
            public void execute(MavenPublishPlugin mavenPublishPlugin) {
                project.getExtensions().configure(PublishingExtension.class, new Action<PublishingExtension>() {
                    @Override
                    public void execute(PublishingExtension publishing) {

                    }
                });
            }
        });

    }

    private void createMavenJavaPublications(final Project project,final PublishingExtension publishing,final PublishConfigExtension extension){
        publishing.publications(new Action<PublicationContainer>() {
            @Override
            public void execute(PublicationContainer publication) {
                publication.create("mavenJava", MavenPublication.class, new Action<MavenPublication>() {
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
    }


    private BaseComponent componentLibrary(Project project) {
        if (project.getPlugins().hasPlugin("java") || project.getPlugins().hasPlugin("java-library")) {
            Logc.e("publish Java Project.");
            return new JavaComponent(project);
        } else if (project.getPlugins().hasPlugin("com.android.library")) {
            Logc.e("publish Android Project.");
            return new AndroidComponent(project);
        } else if (project.getPlugins().hasPlugin("org.jetbrains.kotlin.jvm")) {
            Logc.e("publish kotlin Project.");
            return new KotlinComponent(project);
        }else{
            Logc.e("Unkonw Project.");
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
                        PublishingExtension publishing = project.getExtensions().getByType(PublishingExtension.class);
                        signing.setRequired(true);
                        signing.sign(publishing.getPublications());
                    }
                });
            }
        });
    }

    private void makeMavenRepository(PublishingExtension publishing,final PublishConfigExtension extension){
        String version = extension.version;
        String url = PropertyManager.getInstance().getEntity().maven.getUrl(version);
        String username = PropertyManager.getInstance().getEntity().maven.username;
        String password = PropertyManager.getInstance().getEntity().maven.password;
        if (StringUtils.isStringEmpty(url)){
            throw new NullPointerException("maven url is null");
        }
        if (StringUtils.isStringEmpty(username)){
            throw new NullPointerException("maven username is null");
        }
        if (StringUtils.isStringEmpty(password)){
            throw new NullPointerException("maven password is null");
        }
        final URI uri = URI.create(url);
        publishing.repositories(new Action<RepositoryHandler>() {
            @Override
            public void execute(RepositoryHandler repository) {
                repository.maven(new Action<MavenArtifactRepository>() {
                    @Override
                    public void execute(MavenArtifactRepository mavenRepository) {
                        mavenRepository.setUrl(uri);
                        mavenRepository.credentials(new Action<PasswordCredentials>() {
                            @Override
                            public void execute(PasswordCredentials credential) {
                                credential.setUsername(username);
                                credential.setPassword(password);
                            }
                        });
                    }
                });
            }
        });
    }
    //
    private void makeNexusRepository(PublishingExtension publishing,final PublishConfigExtension extension){
        String version = extension.version;
        final String url = PropertyManager.getInstance().getEntity().nexus.getUrl(version);
        final String username = PropertyManager.getInstance().getEntity().nexus.username;
        final String password = PropertyManager.getInstance().getEntity().nexus.password;
        if (StringUtils.isStringEmpty(url)){
            return;
        }
        if (StringUtils.isStringEmpty(username)){
            return;
        }
        if (StringUtils.isStringEmpty(password)){
            return;
        }

        publishing.repositories(new Action<RepositoryHandler>() {
            @Override
            public void execute(RepositoryHandler repository) {
                repository.maven(new Action<MavenArtifactRepository>() {
                    @Override
                    public void execute(MavenArtifactRepository mavenRepository) {
                        URI uri = URI.create(url);
                        mavenRepository.setUrl(uri);
                        mavenRepository.credentials(new Action<PasswordCredentials>() {
                            @Override
                            public void execute(PasswordCredentials credential) {
                                credential.setUsername(username);
                                credential.setPassword(password);
                            }
                        });
                    }
                });
            }
        });
    }
    //

    private void loadRepositories(final Project project,final PublishConfigExtension extension){
        String url = PropertyManager.getInstance().getEntity().nexus.snapshot;
        if (!StringUtils.isStringEmpty(url)){
            final URI uri = URI.create(url);
            project.getRepositories().maven(new Action<MavenArtifactRepository>() {
                @Override
                public void execute(MavenArtifactRepository mavenArtifactRepository) {
                    mavenArtifactRepository.setUrl(uri);
                    mavenArtifactRepository.credentials(new Action<PasswordCredentials>() {
                        @Override
                        public void execute(PasswordCredentials passwordCredentials) {
                            passwordCredentials.setUsername(PropertyManager.getInstance().getEntity().nexus.username);
                            passwordCredentials.setPassword(PropertyManager.getInstance().getEntity().nexus.password);
                        }
                    });
                }
            });
        }
        url = PropertyManager.getInstance().getEntity().nexus.release;
        if (!StringUtils.isStringEmpty(url)){
            final URI uri = URI.create(url);
            project.getRepositories().maven(new Action<MavenArtifactRepository>() {
                @Override
                public void execute(MavenArtifactRepository mavenArtifactRepository) {
                    mavenArtifactRepository.setUrl(uri);
                    mavenArtifactRepository.credentials(new Action<PasswordCredentials>() {
                        @Override
                        public void execute(PasswordCredentials passwordCredentials) {
                            passwordCredentials.setUsername(PropertyManager.getInstance().getEntity().nexus.username);
                            passwordCredentials.setPassword(PropertyManager.getInstance().getEntity().nexus.password);
                        }
                    });
                }
            });
        }

        String version = extension.version;
        url = PropertyManager.getInstance().getEntity().maven.geReversetUrl(version);
        if (!StringUtils.isStringEmpty(url)){
            final URI uri = URI.create(url);
            project.getRepositories().maven(new Action<MavenArtifactRepository>() {
                @Override
                public void execute(MavenArtifactRepository mavenArtifactRepository) {
                    mavenArtifactRepository.setUrl(uri);
                }
            });
        }

    }

}

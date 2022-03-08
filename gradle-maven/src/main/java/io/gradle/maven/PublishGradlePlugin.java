package io.gradle.maven;

import io.gradle.maven.component.AndroidComponent;
import io.gradle.maven.component.BaseComponent;
import io.gradle.maven.component.JavaComponent;
import io.gradle.maven.component.KotlinComponent;
import io.gradle.maven.extension.PublishConfigExtension;
import io.gradle.maven.manager.PropertyManager;
import io.gradle.maven.task.MavenTask;
import io.gradle.maven.task.NexusTask;
import io.gradle.maven.util.Logc;
import io.gradle.maven.util.StringUtils;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.artifacts.repositories.ArtifactRepository;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.artifacts.repositories.PasswordCredentials;
import org.gradle.api.file.CopySpec;
import org.gradle.api.internal.artifacts.repositories.DefaultMavenArtifactRepository;
import org.gradle.api.internal.provider.AbstractMinimalProvider;
import org.gradle.api.internal.tasks.execution.SelfDescribingSpec;
import org.gradle.api.publish.Publication;
import org.gradle.api.publish.PublicationContainer;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.internal.DefaultPublicationContainer;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.internal.publication.DefaultMavenPublication;
import org.gradle.api.publish.maven.internal.publication.MavenPomInternal;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;
import org.gradle.api.services.BuildService;
import org.gradle.api.tasks.TaskCollection;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.external.javadoc.MinimalJavadocOptions;
import org.gradle.external.javadoc.StandardJavadocDocletOptions;
import org.gradle.plugins.signing.SigningExtension;
import org.gradle.plugins.signing.SigningPlugin;
import org.gradle.util.GradleVersion;
import org.jetbrains.annotations.Nullable;

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
                extension.checkArgs();
                loadRepositories(project,extension);
                configurePublishing(project, extension);
                if (GradleVersion.current().compareTo(GradleVersion.version("4.8")) >= 0 && !extension.version.endsWith("-SNAPSHOT")) {
                    configureSigning(project);
                }
            }
        });

    }

    void configurePublishing(final Project project, final PublishConfigExtension extension) {
        project.getPluginManager().apply(MavenPublishPlugin.class);

        project.getExtensions().configure(PublishingExtension.class, new Action<PublishingExtension>() {
            @Override
            public void execute(PublishingExtension publishing) {
                createMavenJavaPublications(project, publishing, extension);
                makeMavenRepository(publishing,extension);
            }
        });

        taskFinally(project);

        final Task taskPublish = project.getTasks().getByName("publish");

//        Task taskMaven = project.getTasks().create("publishToMaven", new Action<Task>() {
//            @Override
//            public void execute(Task task) {
//                changeToMavenRepository(project, extension);
//                task.finalizedBy(taskPublish);
//            }
//        });
//        taskMaven.setGroup("maven");
//
//
//        Task taskNexus = project.getTasks().create("publishToNexus", new Action<Task>() {
//            @Override
//            public void execute(Task task) {
//                changeToNexusRepository(project, extension);
//                task.finalizedBy(taskPublish);
//            }
//        });
//        taskNexus.setGroup("maven");


        final NexusTask nexus = project.getTasks().create("publishToNexus", NexusTask.class,extension);
        nexus.setGroup("maven");
        nexus.finalizedBy(taskPublish);

        final MavenTask maven = project.getTasks().create("publishToMaven", MavenTask.class,extension);
        maven.setGroup("maven");
        maven.finalizedBy(taskPublish);
    }

    private void createMavenJavaPublications(final Project project,final PublishingExtension publishing,final PublishConfigExtension extension){
        publishing.publications(new Action<PublicationContainer>() {
            @Override
            public void execute(PublicationContainer publication) {
                boolean isSnapshot = StringUtils.isSnapshot(extension.version);
                String mavenName = isSnapshot?"snapshot":"release";
                publication.create(mavenName, MavenPublication.class, new Action<MavenPublication>() {
                    @Override
                    public void execute(MavenPublication maven) {
                        maven.setVersion(extension.version);
                        maven.setArtifactId(extension.name);
                        maven.setGroupId(PropertyManager.getInstance().getEntity().group);
                        BaseComponent component = componentLibrary(project);
                        if (component == null){
                            throw new NullPointerException("BaseComponent Is Null");
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

    private void changeToMavenRepository(final Project project,final PublishConfigExtension extension){
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

        PublishingExtension publishingExtension =  project.getExtensions().getByType(PublishingExtension.class);

        publishingExtension.getRepositories().forEach(new Consumer<ArtifactRepository>() {
            @Override
            public void accept(ArtifactRepository artifactRepository) {
                DefaultMavenArtifactRepository defaultMavenArtifactRepository = (DefaultMavenArtifactRepository) artifactRepository;
                String url = defaultMavenArtifactRepository.getUrl().toString();
                PasswordCredentials passwordCredentials = defaultMavenArtifactRepository.getCredentials(PasswordCredentials.class);
                Logc.e("changeToMavenRepository:\r\n" + url +" \r\n"+ passwordCredentials.getUsername()+" \r\n"+ passwordCredentials.getPassword());
                defaultMavenArtifactRepository.setUrl(uri);
                passwordCredentials.setUsername(username);
                passwordCredentials.setPassword(password);

            }
        });
    }

    private void changeToNexusRepository(final Project project,final PublishConfigExtension extension){
        String version = extension.version;
        String url = PropertyManager.getInstance().getEntity().nexus.getUrl(version);
        String username = PropertyManager.getInstance().getEntity().nexus.username;
        String password = PropertyManager.getInstance().getEntity().nexus.password;
        if (StringUtils.isStringEmpty(url)){
            throw new NullPointerException("nexus url is null");
        }
        if (StringUtils.isStringEmpty(username)){
            throw new NullPointerException("nexus username is null");
        }
        if (StringUtils.isStringEmpty(password)){
            throw new NullPointerException("nexus password is null");
        }
        final URI uri = URI.create(url);

        PublishingExtension publishingExtension =  project.getExtensions().getByType(PublishingExtension.class);

        publishingExtension.getRepositories().forEach(new Consumer<ArtifactRepository>() {
            @Override
            public void accept(ArtifactRepository artifactRepository) {
                DefaultMavenArtifactRepository defaultMavenArtifactRepository = (DefaultMavenArtifactRepository) artifactRepository;
                String url = defaultMavenArtifactRepository.getUrl().toString();
                PasswordCredentials passwordCredentials = defaultMavenArtifactRepository.getCredentials(PasswordCredentials.class);
                Logc.e("changeToNexusRepository:\r\n" + url +" \r\n"+ passwordCredentials.getUsername()+" \r\n"+ passwordCredentials.getPassword());
                defaultMavenArtifactRepository.setUrl(uri);
                passwordCredentials.setUsername(username);
                passwordCredentials.setPassword(password);

            }
        });
    }

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
    //

    private void taskFinally(final Project project){
        project.getTasks().getByName("publish").doLast(new Action<Task>() {
            @Override
            public void execute(Task task) {
                PublishingExtension publishingExtension =  project.getExtensions().getByType(PublishingExtension.class);
                DefaultPublicationContainer b = (DefaultPublicationContainer) publishingExtension.getPublications();

                publishingExtension.getRepositories().forEach(new Consumer<ArtifactRepository>() {
                    @Override
                    public void accept(ArtifactRepository artifactRepository) {
                        DefaultMavenArtifactRepository defaultMavenArtifactRepository = (DefaultMavenArtifactRepository) artifactRepository;
                        String url = defaultMavenArtifactRepository.getUrl().toString();
                        PasswordCredentials passwordCredentials = defaultMavenArtifactRepository.getCredentials(PasswordCredentials.class);
                        Logc.e("发布完毕---->Repo info:" + url +" \r\n"+ passwordCredentials.getUsername()+" \r\n"+ passwordCredentials.getPassword());

                    }
                });
                publishingExtension.getPublications().forEach(new Consumer<Publication>() {
                    @Override
                    public void accept(Publication publication) {
                        DefaultMavenPublication defaultMavenPublication = (DefaultMavenPublication) publication;
                        Logc.e(defaultMavenPublication.getName()+":"+defaultMavenPublication.getGroupId()+":"+defaultMavenPublication.getArtifactId()+":"+defaultMavenPublication.getVersion());
                    }
                });

                Logc.e("---->发布完毕 " );

            }
        });
    }
}

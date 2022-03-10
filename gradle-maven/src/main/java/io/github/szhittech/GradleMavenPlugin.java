package io.github.szhittech;

import io.github.szhittech.component.AndroidComponent;
import io.github.szhittech.component.JavaComponent;
import io.github.szhittech.component.KotlinComponent;
import io.github.szhittech.component.base.BaseComponent;
import io.github.szhittech.extension.MConfig;
import io.github.szhittech.property.PropertyManager;
import io.github.szhittech.task.MavenTask;
import io.github.szhittech.task.NexusTask;
import io.github.szhittech.util.SecringUtil;
import io.github.szhittech.util.StringUtils;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.artifacts.repositories.ArtifactRepository;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.artifacts.repositories.PasswordCredentials;
import org.gradle.api.internal.artifacts.repositories.DefaultMavenArtifactRepository;
import org.gradle.api.logging.Logging;
import org.gradle.api.publish.Publication;
import org.gradle.api.publish.PublicationContainer;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.internal.DefaultPublicationContainer;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.internal.publication.DefaultMavenPublication;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;
import org.gradle.plugins.signing.SigningExtension;
import org.gradle.plugins.signing.SigningPlugin;
import org.gradle.util.GradleVersion;

import java.net.URI;
import java.util.function.Consumer;

public class GradleMavenPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        PropertyManager.getInstance().init(project);
        SecringUtil.loadDefaultSecring(project);
        //final MConfig extension = project.getExtensions().create("mconfig", MConfig.class);
        project.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(Project project) {
                MConfig extension = new MConfig(project);
                loadRepositories(project, extension);
                configurePublishing(project, extension);
                if (GradleVersion.current().compareTo(GradleVersion.version("4.8")) >= 0 && !extension.version.endsWith("-SNAPSHOT")) {
                    configureSigning(project);
                }
            }
        });
    }

    void configurePublishing(final Project project, final MConfig extension) {
        project.getPluginManager().apply(MavenPublishPlugin.class);

        project.getExtensions().configure(PublishingExtension.class, new Action<PublishingExtension>() {
            @Override
            public void execute(PublishingExtension publishing) {
                createMavenJavaPublications(project, publishing, extension);
                makeMavenRepository(publishing, extension);
            }
        });

        taskFinally(project, extension);

        final Task taskPublish = project.getTasks().getByName("publish");

        final MavenTask maven = project.getTasks().create("uploadToMaven", MavenTask.class, extension);
        maven.setGroup("upload");
        maven.finalizedBy(taskPublish);

        PropertyManager.MavenRepository custom = PropertyManager.getInstance().getEntity().nexus;
        if (!StringUtils.isStringEmpty(custom.name)) {
            final NexusTask nexus = project.getTasks().create("uploadTo" + StringUtils.captureName(custom.name), NexusTask.class, extension);
            nexus.setGroup("upload");
            nexus.finalizedBy(taskPublish);
        }
    }

    private void createMavenJavaPublications(final Project project, final PublishingExtension publishing, final MConfig extension) {
        publishing.publications(new Action<PublicationContainer>() {
            @Override
            public void execute(PublicationContainer publication) {
                boolean isSnapshot = StringUtils.isSnapshot(extension.version);
                String mavenName = isSnapshot ? "snapshot" : "release";
                publication.create(mavenName, MavenPublication.class, new Action<MavenPublication>() {
                    @Override
                    public void execute(MavenPublication maven) {
                        maven.setVersion(extension.version);
                        maven.setArtifactId(extension.name);
                        maven.setGroupId(extension.groupId);
                        BaseComponent component = componentLibrary(project);
                        if (component == null) {
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
            return new JavaComponent(project);
        } else if (project.getPlugins().hasPlugin("com.android.library")) {
            return new AndroidComponent(project);
        } else if (project.getPlugins().hasPlugin("org.jetbrains.kotlin.jvm")) {
            return new KotlinComponent(project);
        } else {
            Logging.getLogger(getClass()).error("Unkonw Project.");
        }
        return null;
    }


    private void configureSigning(final Project project) {
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

    private void makeMavenRepository(PublishingExtension publishing, final MConfig extension) {
        String version = extension.version;
        String url = PropertyManager.getInstance().getEntity().maven.getUrl(version);
        String username = PropertyManager.getInstance().getEntity().maven.username;
        String password = PropertyManager.getInstance().getEntity().maven.password;
        if (StringUtils.isStringEmpty(url)) {
            throw new NullPointerException("maven url is null");
        }
        if (StringUtils.isStringEmpty(username)) {
            throw new NullPointerException("maven username is null");
        }
        if (StringUtils.isStringEmpty(password)) {
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

    private void loadRepositories(final Project project, final MConfig extension) {
        String url = PropertyManager.getInstance().getEntity().nexus.snapshot;
        if (!StringUtils.isStringEmpty(url)) {
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
        if (!StringUtils.isStringEmpty(url)) {
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
        if (!StringUtils.isStringEmpty(url)) {
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
    private boolean isSnapShot(String version) {
        return version.endsWith("SNAPSHOT") ? true : false;
    }

    private void taskFinally(final Project project, final MConfig extension) {
        final String name = extension.name;
        final String group = extension.groupId;
        project.getTasks().getByName("publish").doLast(new Action<Task>() {
            @Override
            public void execute(Task task) {
                PublishingExtension publishingExtension = project.getExtensions().getByType(PublishingExtension.class);
                DefaultPublicationContainer b = (DefaultPublicationContainer) publishingExtension.getPublications();

                publishingExtension.getRepositories().forEach(new Consumer<ArtifactRepository>() {
                    @Override
                    public void accept(ArtifactRepository artifactRepository) {
                        DefaultMavenArtifactRepository defaultMavenArtifactRepository = (DefaultMavenArtifactRepository) artifactRepository;
                        String url = defaultMavenArtifactRepository.getUrl().toString();



                        PasswordCredentials passwordCredentials = defaultMavenArtifactRepository.getCredentials(PasswordCredentials.class);
                        if (project.getPlugins().hasPlugin("java") || project.getPlugins().hasPlugin("java-library")) {
                            Logging.getLogger(getClass()).error("Publish Sucess,The Current Project is Java Project.");
                        } else if (project.getPlugins().hasPlugin("com.android.library")) {
                            Logging.getLogger(getClass()).error("Publish Sucess,The Current Project is Android Project.");
                        } else if (project.getPlugins().hasPlugin("org.jetbrains.kotlin.jvm")) {
                            Logging.getLogger(getClass()).error("Publish Sucess,The Current Project is Kotlin Project.");
                        } else {
                            Logging.getLogger(getClass()).error("Unkonw Project.");
                        }

                        if (url.contains("coding")){
                            if (isSnapShot(extension.version)){
                                url = "https://clife-devops.coding.net/public-artifacts/public-repository/maven-snapshots/packages";
                            }else{
                                url = "https://clife-devops.coding.net/public-artifacts/public-repository/maven-releases/packages";
                            }
                            Logging.getLogger(getClass()).error("The {} Url:{}", defaultMavenArtifactRepository.getName() ,url);
                        }else{
                            if (url.equalsIgnoreCase(PropertyManager.getInstance().getEntity().maven.release)){
                                Logging.getLogger(getClass()).error("\r\n##############################################################################");
                                Logging.getLogger(getClass()).error("# First,Click Below Link To Login, And Second Do The Following Steps:        #");
                                Logging.getLogger(getClass()).error("# {}          #",url);
                                Logging.getLogger(getClass()).error("# Step 1: Click 'Staging Repositories' On The Left                           #");
                                Logging.getLogger(getClass()).error("# Step 2: Click 'Refresh' On The Opened Tab,Name:'Staging Repositories'      #");
                                Logging.getLogger(getClass()).error("# Step 3: In the selection list,Choose The Newest One(I Think You Known It)  #");
                                Logging.getLogger(getClass()).error("# Step 4: Wait A Monment, If No Error,Pleanse Choose It And Click 'Release'  #");
                                Logging.getLogger(getClass()).error("##############################################################################\r\n");
                            }else{
                                Logging.getLogger(getClass()).error("The {} Url:{}", defaultMavenArtifactRepository.getName() ,url);
                                String libraryUrl = url + group.replaceAll("\\.", "/");
                                libraryUrl+= "/" + name;
                                Logging.getLogger(getClass()).error("The Library Url:{}", libraryUrl);
                            }
                        }


                        if (PropertyManager.getInstance().isDebug()) {
                            Logging.getLogger(getClass()).error("UserName:{}", passwordCredentials.getUsername());
                            Logging.getLogger(getClass()).error("PassWord:{}", passwordCredentials.getPassword());
                        }
                    }
                });
                publishingExtension.getPublications().forEach(new Consumer<Publication>() {
                    @Override
                    public void accept(Publication publication) {
                        DefaultMavenPublication defaultMavenPublication = (DefaultMavenPublication) publication;
                        Logging.getLogger(getClass()).error("implementation '{}:{}:{}'", defaultMavenPublication.getGroupId(), defaultMavenPublication.getArtifactId(), defaultMavenPublication.getVersion());
                    }
                });

            }
        });
    }
}

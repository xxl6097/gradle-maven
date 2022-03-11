package io.github.szhittech.publish;

import io.github.szhittech.component.AndroidComponent;
import io.github.szhittech.component.JavaComponent;
import io.github.szhittech.component.KotlinComponent;
import io.github.szhittech.component.base.BaseComponent;
import io.github.szhittech.extension.MConfig;
import io.github.szhittech.property.PropertyManager;
import io.github.szhittech.repository.RepositoryEntity;
import io.github.szhittech.task.UploadTaskManager;
import io.github.szhittech.util.StringUtils;
import jdk.nashorn.internal.objects.NativeUint8Array;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.artifacts.repositories.PasswordCredentials;
import org.gradle.api.logging.Logging;
import org.gradle.api.publish.PublicationContainer;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;
import org.gradle.plugins.signing.SigningExtension;
import org.gradle.plugins.signing.SigningPlugin;
import org.gradle.util.GradleVersion;

import java.net.URI;

public class PublishManager {
    private static PublishManager instance;

    public static PublishManager getInstance() {
        if (instance == null) {
            synchronized (PublishManager.class) {
                if (instance == null) {
                    instance = new PublishManager();
                }
            }
        }
        return instance;
    }

    public void configurePublishing(final Project project, final MConfig config) {
        project.getPluginManager().apply(MavenPublishPlugin.class);

        final boolean isSnapShot = StringUtils.isSnapShot(config.version);

        project.getExtensions().configure(PublishingExtension.class, new Action<PublishingExtension>() {
            @Override
            public void execute(PublishingExtension publishing) {
                createMavenJavaPublications(project, publishing, config);
                makeMavenRepository(publishing, isSnapShot);
            }
        });

        UploadTaskManager.getInstance().init(project, config);

        if (GradleVersion.current().compareTo(GradleVersion.version("4.8")) >= 0 && !isSnapShot) {
            configureSigning(project);
        }
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

    private void makeMavenRepository(PublishingExtension publishing, final boolean isSnapShot) {
        final RepositoryEntity repo = isSnapShot ? PropertyManager.getInstance().getMaven_snapshot() : PropertyManager.getInstance().getMaven_release();
        if (repo == null) {
            throw new NullPointerException("maven repo is null");
        }
        if (StringUtils.isStringEmpty(repo.getRepositoryUrl())) {
            throw new NullPointerException("maven url is null");
        }
        if (StringUtils.isStringEmpty(repo.getUsername())) {
            throw new NullPointerException("maven username is null");
        }
        if (StringUtils.isStringEmpty(repo.getPassword())) {
            throw new NullPointerException("maven password is null");
        }
        publishing.repositories(new Action<RepositoryHandler>() {
            @Override
            public void execute(RepositoryHandler repository) {
                repository.maven(new Action<MavenArtifactRepository>() {
                    @Override
                    public void execute(MavenArtifactRepository mavenRepository) {
                        URI uri = URI.create(repo.getRepositoryUrl());
                        mavenRepository.setUrl(uri);
                        mavenRepository.credentials(new Action<PasswordCredentials>() {
                            @Override
                            public void execute(PasswordCredentials credential) {
                                credential.setUsername(repo.getUsername());
                                credential.setPassword(repo.getPassword());
                            }
                        });
                    }
                });
            }
        });
    }


    private void createMavenJavaPublications(final Project project, final PublishingExtension publishing, final MConfig config) {
        publishing.publications(new Action<PublicationContainer>() {
            @Override
            public void execute(PublicationContainer publication) {
                boolean isSnapshot = StringUtils.isSnapshot(config.version);
                String mavenName = "maven";//isSnapshot ? "snapshot" : "release";
                MavenPublication pub = publication.create(mavenName, MavenPublication.class, new Action<MavenPublication>() {
                    @Override
                    public void execute(MavenPublication maven) {
                        maven.setVersion(config.version);
                        maven.setArtifactId(config.name);
                        maven.setGroupId(config.groupId);
                        BaseComponent component = componentLibrary(project);
                        if (component == null) {
                            throw new NullPointerException("BaseComponent Is Null");
                        }
                        component.buildComponent(maven, config);
                    }
                });
                //Logging.getLogger(getClass()).error("{} {} {} {}",pub.getArtifactId(),pub.getGroupId(),pub.getVersion(),pub.getName());
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
}

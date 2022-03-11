package io.github.szhittech.task;


import io.github.szhittech.extension.MConfig;
import io.github.szhittech.property.PropertyManager;
import io.github.szhittech.repository.RepositoryEntity;
import io.github.szhittech.util.StringUtils;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.ArtifactRepositoryContainer;
import org.gradle.api.artifacts.repositories.ArtifactRepository;
import org.gradle.api.artifacts.repositories.PasswordCredentials;
import org.gradle.api.internal.artifacts.repositories.DefaultMavenArtifactRepository;
import org.gradle.api.logging.Logging;
import org.gradle.api.publish.Publication;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.internal.DefaultPublicationContainer;
import org.gradle.api.publish.maven.internal.publication.DefaultMavenPublication;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;
import org.gradle.api.publish.plugins.PublishingPlugin;

import java.util.function.Consumer;

public class UploadTaskManager {
    private static UploadTaskManager instance;

    public static UploadTaskManager getInstance() {
        if (instance == null) {
            synchronized (UploadTaskManager.class) {
                if (instance == null) {
                    instance = new UploadTaskManager();
                }
            }
        }
        return instance;
    }

    public void init(final Project project,final MConfig config){
        final Task publishMavenLocalTask = project.getTasks().getByName(MavenPublishPlugin.PUBLISH_LOCAL_LIFECYCLE_TASK_NAME);
        final Task publishTask = project.getTasks().getByName(PublishingPlugin.PUBLISH_LIFECYCLE_TASK_NAME);

        final boolean isSnapshot = StringUtils.isSnapShot(config.version);

        RepositoryEntity repoNexus = isSnapshot?PropertyManager.getInstance().getNexus_snapshot():PropertyManager.getInstance().getNexus_release();
        if(repoNexus != null){
            String name = repoNexus.getName();
            if (!StringUtils.isStringEmpty(name)) {
                final CustomTask nexus = project.getTasks().create("uploadTo" + StringUtils.captureName(name), CustomTask.class, repoNexus);
                nexus.setGroup("upload");
                nexus.finalizedBy(publishTask);
            }
        }


        RepositoryEntity repo = isSnapshot?PropertyManager.getInstance().getMaven_snapshot():PropertyManager.getInstance().getMaven_release();
        final CustomTask maven = project.getTasks().create("uploadToMaven", CustomTask.class, repo);
        maven.setGroup("upload");
        maven.finalizedBy(publishTask);

//        RepositoryEntity localEnty = new RepositoryEntity("local","file:/"+project.getRootDir().getAbsolutePath() + File.separator + "repo");
//        final CustomTask localTask = project.getTasks().create("uploadTo"+ StringUtils.captureName(localEnty.getName()), CustomTask.class, localEnty);
//        localTask.setGroup("upload");
//        localTask.finalizedBy(publishTask);
        //DefaultMavenLocalArtifactRepository

        taskFinally(project, config);
    }


    private void taskFinally(final Project project, final MConfig config) {
        final String name = config.name;
        final String group = config.groupId;
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
                            if (StringUtils.isSnapShot(config.version)){
                                url = "https://clife-devops.coding.net/public-artifacts/public-repository/maven-snapshots/packages";
                            }else{
                                url = "https://clife-devops.coding.net/public-artifacts/public-repository/maven-releases/packages";
                            }
                            Logging.getLogger(getClass()).error("The {} Url:{}", defaultMavenArtifactRepository.getName() ,url);
                        }else{
                            if (url.equalsIgnoreCase(PropertyManager.getInstance().getMaven_release().getRepositoryUrl())){
                                Logging.getLogger(getClass()).error("\r\n##############################################################################");
                                Logging.getLogger(getClass()).error("# First,Click Below Link To Login, And Second Do The Following Steps:        #");
                                Logging.getLogger(getClass()).error("# https://s01.oss.sonatype.org/                                              #");
                                Logging.getLogger(getClass()).error("# Step 1: Click 'Staging Repositories' On The Left                           #");
                                Logging.getLogger(getClass()).error("# Step 2: Click 'Refresh' On The Opened Tab,Name:'Staging Repositories'      #");
                                Logging.getLogger(getClass()).error("# Step 3: In the selection list,Choose The Newest One(I Think You Known It)  #");
                                Logging.getLogger(getClass()).error("# Step 4: Wait A Monment, If No Error,Pleanse Choose It And Click 'Release'  #");
                                Logging.getLogger(getClass()).error("##############################################################################\r\n");

                                String mavenCentralUrl = ArtifactRepositoryContainer.MAVEN_CENTRAL_URL;
                                mavenCentralUrl = mavenCentralUrl + group.replaceAll("\\.", "/");
                                mavenCentralUrl += "/" + name;
                                Logging.getLogger(getClass()).error("See:{}", mavenCentralUrl);

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

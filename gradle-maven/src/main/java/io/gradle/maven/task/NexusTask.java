package io.gradle.maven.task;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.repositories.ArtifactRepository;
import org.gradle.api.artifacts.repositories.PasswordCredentials;
import org.gradle.api.internal.artifacts.repositories.DefaultMavenArtifactRepository;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.tasks.TaskAction;

import java.net.URI;
import java.util.function.Consumer;

import javax.inject.Inject;

import io.gradle.maven.extension.PublishConfigExtension;
import io.gradle.maven.manager.PropertyManager;
import io.gradle.maven.util.Logc;
import io.gradle.maven.util.StringUtils;

public class NexusTask extends DefaultTask {

    private PublishConfigExtension extension;

    @Inject
    public NexusTask(PublishConfigExtension extension) {
        this.extension = extension;
        Logc.e("---->NexusTask.NexusTask : "+extension.name+":"+extension.version);
    }

    @TaskAction
    public void taskAction(){
        Project project = getProject();
        if (project != project.getRootProject()){
        }
        Logc.e("---->NexusTask.taskAction");
        changeToNexusRepository(project,extension);
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

}

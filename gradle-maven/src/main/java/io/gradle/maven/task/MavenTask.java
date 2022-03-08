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

public class MavenTask extends DefaultTask {
    private PublishConfigExtension extension;

    @Inject
    public MavenTask(PublishConfigExtension extension) {
        this.extension = extension;
        Logc.e("---->MavenTask.MavenTask : "+extension.name+":"+extension.version);
    }

    @TaskAction
    public void taskAction(){
        Project project = getProject();
        if (project != project.getRootProject()){
        }
        Logc.e("---->MavenTask.taskAction");
        changeToMavenRepository(project,extension);
    }


    private void changeToMavenRepository(final Project project,final PublishConfigExtension extension){
        String version = extension.version;
        String group = PropertyManager.getInstance().getEntity().group;
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
                if (!StringUtils.isStringEmpty(group)){
                    group.replaceAll(".","/");
                }

            }
        });
    }

}

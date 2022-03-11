package io.github.szhittech.task;

import io.github.szhittech.repository.RepositoryEntity;
import io.github.szhittech.util.StringUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.repositories.ArtifactRepository;
import org.gradle.api.artifacts.repositories.PasswordCredentials;
import org.gradle.api.internal.artifacts.repositories.DefaultMavenArtifactRepository;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.net.URI;
import java.util.function.Consumer;

public class CustomTask extends DefaultTask {
    private RepositoryEntity entity;

    @Inject
    public CustomTask(RepositoryEntity extension) {
        this.entity = extension;
    }

    @TaskAction
    public void taskAction(){
        Project project = getProject();
        if (project != project.getRootProject()){
        }
        changeToMavenRepository(project,entity);
    }


    private void changeToMavenRepository(final Project project,final RepositoryEntity entity){
        final String url = entity.getRepositoryUrl();
        String username = entity.getUsername();
        String password = entity.getPassword();
        if (StringUtils.isStringEmpty(url)){
            throw new NullPointerException("repository url is null");
        }

        PublishingExtension publishingExtension =  project.getExtensions().getByType(PublishingExtension.class);
        publishingExtension.getRepositories().forEach(new Consumer<ArtifactRepository>() {
            @Override
            public void accept(ArtifactRepository artifactRepository) {
                DefaultMavenArtifactRepository repository = (DefaultMavenArtifactRepository) artifactRepository;
                String orangeUrl = repository.getUrl().toString();
                URI uri = URI.create(url);
                repository.setUrl(uri);
                if (!StringUtils.isStringEmpty(username) && !StringUtils.isStringEmpty(username)){
                    PasswordCredentials passwordCredentials = repository.getCredentials(PasswordCredentials.class);
                    passwordCredentials.setUsername(username);
                    passwordCredentials.setPassword(password);
                }

                //Logging.getLogger(getClass()).error("Publish Repository Url: {}",orangeUrl);
            }
        });
    }

}

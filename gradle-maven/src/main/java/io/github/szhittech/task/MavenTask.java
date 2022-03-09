package io.github.szhittech.task;

import io.github.szhittech.extension.MConfig;
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

import io.github.szhittech.property.PropertyManager;
import io.github.szhittech.util.StringUtils;

public class MavenTask extends DefaultTask {
    private MConfig extension;

    @Inject
    public MavenTask(MConfig extension) {
        this.extension = extension;
        //Logc.e("---->MavenTask.MavenTask : "+extension.name+":"+extension.version);
    }

    @TaskAction
    public void taskAction(){
        Project project = getProject();
        if (project != project.getRootProject()){
        }
        //Logc.e("---->MavenTask.taskAction");
        changeToMavenRepository(project,extension);
    }


    private void changeToMavenRepository(final Project project,final MConfig extension){
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
                DefaultMavenArtifactRepository repository = (DefaultMavenArtifactRepository) artifactRepository;
                String url = repository.getUrl().toString();
                PasswordCredentials passwordCredentials = repository.getCredentials(PasswordCredentials.class);
                repository.setUrl(uri);
                passwordCredentials.setUsername(username);
                passwordCredentials.setPassword(password);

                //Logging.getLogger(getClass()).error("Publish Repository Url: {}",url);
            }
        });
    }

}

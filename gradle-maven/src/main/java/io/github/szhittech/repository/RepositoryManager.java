package io.github.szhittech.repository;


import io.github.szhittech.property.PropertyManager;
import io.github.szhittech.util.StringUtils;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.artifacts.repositories.PasswordCredentials;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RepositoryManager {
    private static RepositoryManager instance;
    private List<RepositoryEntity> repositories = new ArrayList<>();

    public static RepositoryManager getInstance() {
        if (instance == null) {
            synchronized (RepositoryManager.class) {
                if (instance == null) {
                    instance = new RepositoryManager();
                }
            }
        }
        return instance;
    }

    public void loadRepositoreis(final Project project) {
        repositories.add(PropertyManager.getInstance().getMaven_release());
        repositories.add(PropertyManager.getInstance().getMaven_snapshot());
        RepositoryEntity release = PropertyManager.getInstance().getNexus_release();
        RepositoryEntity snapshot = PropertyManager.getInstance().getNexus_snapshot();
        String username = "xiaoli.xia@clife.cn";
        String password = "het002402";
        String snapshotUrl = "https://clife-devops-maven.pkg.coding.net/repository/public-repository/maven-snapshots/";
        String releasesUrl = "https://clife-devops-maven.pkg.coding.net/repository/public-repository/maven-releases/";

        if (release == null){
            release = new RepositoryEntity("coding",username,password,releasesUrl);
        }
        if (snapshot == null){
            snapshot =new RepositoryEntity("coding",username,password,snapshotUrl);
        }
        repositories.add(release);
        repositories.add(snapshot);

        repositories.forEach(new Consumer<RepositoryEntity>() {
            @Override
            public void accept(RepositoryEntity entity) {
                maven(project, entity);
            }
        });

    }

    public void maven(final Project project, final RepositoryEntity entity){
        if (project == null || entity == null || StringUtils.isStringEmpty(entity.getRepositoryUrl()))
            return;
        project.getRepositories().maven(new Action<MavenArtifactRepository>() {
            @Override
            public void execute(MavenArtifactRepository mavenArtifactRepository) {
                URI uri = URI.create(entity.getRepositoryUrl());
                mavenArtifactRepository.setUrl(uri);
                if (!StringUtils.isStringEmpty(entity.getUsername()) && !StringUtils.isStringEmpty(entity.getPassword())){
                    mavenArtifactRepository.credentials(new Action<PasswordCredentials>() {
                        @Override
                        public void execute(PasswordCredentials passwordCredentials) {
                            passwordCredentials.setUsername(entity.getUsername());
                            passwordCredentials.setPassword(entity.getPassword());
                        }
                    });
                }
            }
        });
    }

}

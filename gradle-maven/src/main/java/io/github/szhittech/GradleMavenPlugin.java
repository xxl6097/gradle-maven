package io.github.szhittech;

import io.github.szhittech.extension.MConfig;
import io.github.szhittech.property.PropertyManager;
import io.github.szhittech.publish.PublishManager;
import io.github.szhittech.repository.RepositoryManager;
import io.github.szhittech.util.SecringUtil;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;


public class GradleMavenPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        PropertyManager.getInstance().init(project);
        SecringUtil.loadDefaultSecring(project);
        //final MConfig extension = project.getExtensions().create("mconfig", MConfig.class);
        MavenArtifactRepository local = project.getRepositories().mavenLocal();
        project.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(Project project) {
                MConfig config = new MConfig(project);
                RepositoryManager.getInstance().loadRepositoreis(project);
                PublishManager.getInstance().configurePublishing(project,config);
            }
        });
        //1。2。11。33。34。。35。36。37。38。45
    }

}

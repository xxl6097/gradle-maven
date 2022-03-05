package com.clife.gradle.task


import com.clife.gradle.util.Util
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.tasks.TaskAction

class PreUploadTask extends DefaultTask {

    @TaskAction
    void taskAction() throws IOException {
        Project project = getProject()
        if (project && project != project.getRootProject()) {
            processDepend(getProject())
        }

    }

    void processDepend(Project project) {
        project.configurations.all(new Action<Configuration>() {
            @Override
            void execute(Configuration configuration) {
                String name = configuration.getName()
                if (name == "implementation" || name == "compile" || name == "api") {
                    Iterator<Dependency> itt = configuration.getDependencies().iterator()
                    while (itt.hasNext()) {
                        Dependency item = itt.next()
                        if (item == null || item.getGroup() == null)
                            continue
                        processDependency(item)
                    }
                }
            }
        })
    }


    private void processDependency(Dependency dependency) {
        if (dependency == null)
            return
        String linName = dependency.getName()
        String localVersionName = dependency.getVersion()
        if (localVersionName == null)
            return
        if (Util.isInvalidOrUnspecifiedVersion(localVersionName)){
            IllegalArgumentException e = new IllegalArgumentException("Local Dependecy Error:" + linName + ":" + localVersionName)
            e.printStackTrace()
            throw e
        }


    }

}

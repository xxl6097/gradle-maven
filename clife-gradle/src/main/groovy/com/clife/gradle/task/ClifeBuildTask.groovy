package com.clife.gradle.task

import com.clife.gradle.util.Logc
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

class ClifeBuildTask extends DefaultTask {

    @TaskAction
    void taskAction() throws IOException {
        Project project = getProject()
        if (project && project != project.getRootProject()) {
            processDepend(getProject())
        }


    }

    void processDepend(Project project) {
        Logc.e("<<<<<<<<<<<<<<ClifeBuildTask<<<<<<<<<<<<<<<")
    }

}

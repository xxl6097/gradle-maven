/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.clife.gradle

import com.clife.gradle.api.MavenApi
import com.clife.gradle.api.PublicApi
import com.clife.gradle.util.GUtil
import com.clife.gradle.util.Logc
import com.clife.gradle.util.Util
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * <p>A {@link Plugin} that provides task for configuring and uploading artifacts to Sonatype Nexus.</p>
 *
 * @author Benjamin Muschko
 */
class ClifePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.configurations.all {
            resolutionStrategy.cacheChangingModulesFor 0, 'seconds'
        }
        Logc.project = project
        Util.pluginVersion = GUtil.getVersion()
        Logc.e("====Welcome to clife plugin====\r\nversion:" + Util.pluginVersion)
//        PublicApi.getApi().loadForPublic(project)

        PublicApi.getApi().init(project)
        MavenApi.initMaven(project)
        PublicApi.getApi().secondForPublic(project)

        /*project.tasks.create("ClifeBuildTask", ClifeBuildTask.class)
        String preBuildTaskName = "preBuild"
        Task preBuildTask = project.tasks.getByName(preBuildTaskName)
        preBuildTask.dependsOn("ClifeBuildTask")
        preBuildTask.doLast(new Action<Task>() {
            @Override
            void execute(Task task) {
                Logc.e(">>>>>>build>>>>ClifeBuildTask>>>>")
                //throw new NullPointerException("wahahahahahahahahahah")
            }
        })*/
    }


}
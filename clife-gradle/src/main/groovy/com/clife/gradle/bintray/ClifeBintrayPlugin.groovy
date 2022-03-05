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
package com.clife.gradle.bintray

import com.clife.gradle.ExtraArchivePlugin
import com.clife.gradle.api.ConfigApi
import com.clife.gradle.api.LibraryApi
import com.clife.gradle.bean.conf.BintayBean
import com.clife.gradle.bean.conf.ConfigBean
import com.clife.gradle.bean.lib.LibraryBean
import com.clife.gradle.util.Logc
import com.jfrog.bintray.gradle.BintrayExtension
import com.jfrog.bintray.gradle.tasks.BintrayPublishTask
import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Dependency
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.tasks.bundling.Jar

class ClifeBintrayPlugin implements Plugin<Project> {
    static final String ARCHIVES = Dependency.ARCHIVES_CONFIGURATION//"archives"

    private BintrayExtension bintrayExtension = new BintrayExtension()

    void apply(Project project) {

        createArtifactTasks(project)
        createBintrayExtension(project)
        if (project.getTasks().findByName(BintrayUploadTask.TASK_NAME) == null) {
            //Create and configure the task
            BintrayUploadTask bintrayUpload = project.task(type: BintrayUploadTask, BintrayUploadTask.TASK_NAME)
            bintrayUpload.project = project
            project.gradle.addListener(new ProjectsEvaluatedBuildListener(bintrayUpload, bintrayExtension))
        }

        addBintrayUploadTask(project)
        if (isRootProject(project)) {
            addBintrayPublishTask(project)
        } else {
            if (!project.getRootProject().getPluginManager().hasPlugin("clife.maven")) {
                // Reached this state means that the plugin is not enabled on the root project
                BintrayUploadTask bintrayUploadRoot = project.getRootProject().task(type: BintrayUploadTask, BintrayUploadTask.TASK_NAME)
                bintrayUploadRoot.setEnabled(false)
                bintrayUploadRoot.project = project.getRootProject()
                project.getRootProject().gradle.addListener(new ProjectsEvaluatedBuildListener(bintrayUploadRoot, bintrayExtension))
                project.getRootProject().getPluginManager().apply(ClifeBintrayPlugin.class)
            }
        }

        //project.plugins.apply(ExtraArchivePlugin)
        configureTasks(project)
    }

    private BintrayUploadTask addBintrayUploadTask(Project project) {
        BintrayUploadTask bintrayUploadTask = project.tasks.findByName(BintrayUploadTask.TASK_NAME)
        if (bintrayUploadTask == null) {
            bintrayUploadTask = createBintrayUploadTask(project)
            bintrayUploadTask.setGroup(BintrayUploadTask.GROUP)
        }
        return bintrayUploadTask
    }

    BintrayUploadTask createBintrayUploadTask(Project project) {
        def result = project.getTasks().create(BintrayUploadTask.TASK_NAME, BintrayUploadTask.class)
        result.setDescription('''AddS bintray closure''')
        return result
    }

    private static boolean isRootProject(Project project) {
        project.equals(project.getRootProject())
    }

    private BintrayPublishTask addBintrayPublishTask(Project project) {
        Task signAndPublish = project.tasks.findByName(BintrayPublishTask.TASK_NAME)
        if (signAndPublish == null) {
            project.getLogger().info("Configuring signAndPublish task for project ${project.path}")
            signAndPublish = createBintraySignAndPublishTask(project)
            signAndPublish.setGroup(BintrayUploadTask.GROUP)
        }
    }

    protected BintrayPublishTask createBintraySignAndPublishTask(Project project) {
        def result = project.getTasks().create(BintrayPublishTask.TASK_NAME, BintrayPublishTask.class)
        result.setDescription('''Bintray Sign, publish and Maven central sync task''')
        return result
    }

    private void configureTasks(Project project) {
        /*project.afterEvaluate {
            configationDependenceTask(project, BintrayPublishTask.TASK_NAME)
            //addArchiveTaskToOutgoingArtifacts(project, ExtraArchivePlugin.SOURCES_JAR_TASK_NAME)
            //addArchiveTaskToOutgoingArtifacts(project, BintrayPublishTask.TASK_NAME)
        }*/



//        project.afterEvaluate {
//            project.plugins.withType(JavaPlugin) {
//                Logc.e("==================configureTasks==ssssssssssssssssssssssss")
//                Task archiveTask =  project.tasks.create(ExtraArchivePlugin.SOURCES_JAR_TASK_NAME, type: Jar) {
//                    classifier = 'sources'
//                    group = ExtraArchivePlugin.JAR_TASK_GROUP
//                    description = 'Assembles a jar archive containing the main sources of this project.'
//                    from project.sourceSets.main.allSource
////                from android.sourceSets.main.java.srcDirs
//                }
//                Logc.e("==================configureTasks=="+archiveTask)
//                if (archiveTask){
//                    project.artifacts.add(ARCHIVES, archiveTask)
//                }
//            }
//            Task archiveTask = project.tasks.findByName(ExtraArchivePlugin.SOURCES_JAR_TASK_NAME)
//
//        }

        project.afterEvaluate {

//            Task archiveTask = project.tasks.findByName(ExtraArchivePlugin.SOURCES_JAR_TASK_NAME)
//            Logc.e("==================configureTasks=="+archiveTask)
//            if (archiveTask){
//                project.artifacts.add(ARCHIVES, archiveTask)
//            }


            /*project.plugins.withType(JavaPlugin) {

                Task sourcesJar1 = project.task(ExtraArchivePlugin.SOURCES_JAR_TASK_NAME, type: Jar) {
                    classifier = 'sources'
                    group = ExtraArchivePlugin.JAR_TASK_GROUP
                    description = 'Assembles a jar archive containing the main sources of this project.'
                    from project.sourceSets.main.allSource
                    //from android.sourceSets.main.java.srcDirs
                }


            }
            project.artifacts {
                archives sourcesJar
            }*/

            /*Task genSourcesJarTask = project.task(ExtraArchivePlugin.SOURCES_JAR_TASK_NAME, type: Jar) {
                classifier = 'sources'
                group = ExtraArchivePlugin.JAR_TASK_GROUP
                description = 'Assembles a jar archive containing the main sources of this project.'
                from project.sourceSets.main.allSource
                //from android.sourceSets.main.java.srcDirs

            }

            project.artifacts.apply {
                add("archives", genJavadocJarTask)
                add("archives", genSourcesJarTask)
            }*/
        }

        if (project == project.getRootProject()) {

        } else {
//            project.task('sourcesJar', type: Jar, dependsOn: 'classes') {
//                classifier = 'sources'
//                from project.sourceSets.main.allSource
//            }

//            project.tasks.register("sourcesJar", Jar) {
//                final SourceSetContainer sourceSets = project.convention.getPlugin(JavaPluginConvention).sourceSets
//                Logc.e("=============main==========>"+sourceSets.getByName("main"))
//                it.from sourceSets.getByName("main").allSource
//                it.archiveClassifier.set 'sources'
//            }
        }


    }


    private void createArtifactTasks(Project project) {

    }


    private void configationDependenceTask(Project project, String taskName) {
        Task archiveTask = project.tasks.findByName(taskName)
        Logc.e("=================configationDependenceTask>" + archiveTask)
        if (archiveTask) {
            project.artifacts.add(ARCHIVES, archiveTask)
            //archiveTask.dependsOn(SvnUpdateTask)
        }
    }

    private void addArchiveTaskToOutgoingArtifacts(Project project, String taskName) {
        Task archiveTask = project.tasks.findByName(taskName)

        Logc.e("=================addArchiveTaskToOutgoingArtifacts>" + archiveTask)
        if (archiveTask) {
//            project.artifacts.add(ARCHIVES, archiveTask)
        }
    }

    private void createBintrayExtension(Project project) {
        bintrayExtension.project = project
        bintrayExtension.apiUrl = BintrayUploadTask.API_URL_DEFAULT
        bintrayExtension.configurations = ['archives']
        //bintrayExtension.publications = ['MyPublication']
        ConfigBean config = ConfigApi.getApi().getConfig()
        LibraryBean library = LibraryApi.api.getLibrary(project.name)
        if (config && library) {
            BintayBean bean = config.getBintray()
            if (bean) {
                project.group = config.group
                project.version = library.getVersion(true)
                bintrayExtension.project = project
                bintrayExtension.user = bean.bintrayuser
                bintrayExtension.key = bean.bintrayapikey
                bintrayExtension.publish = true
                bintrayExtension.pkg {
                    repo = bean.repo
                    name = project.name
                    desc = bean.desc
                    websiteUrl = bean.siteUrl
                    issueTrackerUrl = bean.issueUrl
                    vcsUrl = bean.gitUrl
                    licenses = [bean.licenseName]
                    labels = ['aar', 'android', 'sdk', 'clife']
                    publicDownloadNumbers = true
                    publish = true
                }

                /*bintrayExtension.pkg.repo = bean.repo
                bintrayExtension.pkg.name = project.name
                bintrayExtension.pkg.desc = bean.desc
                bintrayExtension.pkg.websiteUrl = bean.siteUrl
                bintrayExtension.pkg.issueTrackerUrl = bean.issueUrl
                bintrayExtension.pkg.vcsUrl = bean.gitUrl
                bintrayExtension.pkg.licenses = [bean.licenseName]
                bintrayExtension.pkg.labels = ['aar', 'android', 'sdk','clife']
                bintrayExtension.pkg.publicDownloadNumbers = true
                bintrayExtension.pkg.publish = true*/
            } else {
                Logc.e("oh no,maven,json's bintray is null")
            }

        }

//        def bintray = project.extensions.findByType(BintrayExtension)
//        bintray.user = project.properties[BINTRAY_USER_DEFAULT_PROPERTY_NAME]

    }


    protected static PublishingExtension getPublishing(Project project) {
        project.extensions.getByType(PublishingExtension)
    }
}

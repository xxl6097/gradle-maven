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

import com.clife.gradle.api.ConfigApi
import com.clife.gradle.api.LibraryApi
import com.clife.gradle.api.MavenApi
import com.clife.gradle.api.PropertyApi
import com.clife.gradle.api.PublicApi
import com.clife.gradle.bean.conf.ConfigBean
import com.clife.gradle.bean.conf.ExtensionBean
import com.clife.gradle.bean.lib.LibraryBean
import com.clife.gradle.bean.prop.PropertyBean
import com.clife.gradle.task.ClifeUploadTask
import com.clife.gradle.task.PreUploadTask
import com.clife.gradle.task.SvnUpdateTask
import com.clife.gradle.util.GUtil
import com.clife.gradle.util.Logc
import com.clife.gradle.util.Util
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.maven.MavenDeployment
import org.gradle.api.execution.TaskExecutionGraph
import org.gradle.api.plugins.MavenPlugin
import org.gradle.api.tasks.Upload
import org.gradle.plugins.signing.SigningPlugin

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/**
 * <p>A {@link Plugin} that provides task for configuring and uploading artifacts to Sonatype Nexus.</p>
 *
 * @author Benjamin Muschko
 */
class NexusPlugin implements Plugin<Project> {
    static final String NEXUS_USERNAME = 'nexusUsername'
    static final String NEXUS_PASSWORD = 'nexusPassword'
    static final String SIGNING_KEY_ID = 'signing.keyId'
    static final String SIGNING_KEYRING = 'signing.secretKeyRingFile'
    static final String SIGNING_PASSWORD = 'signing.password'


    @Override
    void apply(Project project) {

        project.configurations.all {
            resolutionStrategy.cacheChangingModulesFor 0, 'seconds'
        }

        Util.pluginVersion = GUtil.getVersion()
        Logc.e("====Welcome to clife plugin====\r\nversion:" + Util.pluginVersion)

        /*project.repositories {
            maven {
                url 'https://clife-devops-maven.pkg.coding.net/repository/public-repository/maven-snapshots/'
                credentials {
                    username = 'tianliang.liu@clife.cn'
                    password = '>8wexq44buLZBfA'
                }
            }
        }*/


        //PublicApi.getApi().loadForMaven(project)
        PublicApi.getApi().init(project)
        MavenApi.initMaven(project)
        PublicApi.getApi().secondForMaven(project)
        processBuildConifg(project)
        PropertyBean properties = PropertyApi.getApi().getProperty()
        if (properties) {
            int mavenType = properties.getMavenTye()
            if (mavenType == 3) {
//                project.plugins.apply(AndroidMavenPlugin)
//                project.plugins.apply(ClifeBintrayPlugin)
                project.plugins.apply(JCenterPlugin)
                return
            }
        }

        project.plugins.apply(ExtraArchivePlugin)
        project.plugins.apply(MavenPlugin)
        project.plugins.apply(SigningPlugin)

        ConfigBean config = ConfigApi.getApi().getConfig()
        if (config == null) {
            Logc.e("config is null")
        }

        if (config != null) {
            ExtensionBean extension = config.getExtension()
            if (extension == null) {
                Logc.e("extension is null")
            } else {
                configureTasks(project, extension)
                configureSigning(project, extension)
                configurePom(project, extension)
                configureUpload(project, extension)
            }
        }

        /*project.tasks.all(new Action<Task>() {
            @Override
            void execute(Task task) {
                Logc.e(">>>>" + task.getName())
            }
        })*/

        project.afterEvaluate {
            String uploadName = "uploadArchives"
            project.tasks.create("preUpload", PreUploadTask)
            Task uploadTask = project.tasks.getByName(uploadName)
            uploadTask.dependsOn("preUpload")

            int mavenType = properties.getMavenTye()
            String taskUploadNames = "uploadToMaven"
            if (mavenType == 2) {
                taskUploadNames = "uploadToSonatypeForSnapshot"
            }else if (mavenType == 1) {
                taskUploadNames = "uploadToCodingForRelease"
            }else if (mavenType == 0) {
                taskUploadNames = "uploadToCodingForSnapshot"
            }

            Task sucess = project.tasks.create(taskUploadNames, ClifeUploadTask.class)
            sucess.dependsOn(uploadName)
            sucess.setGroup("clife")
        }

    }

    private void configureTasks(Project project, ExtensionBean extensionBean) {
        project.afterEvaluate {
            if (extensionBean != null) {
                configationDependenceTask(project, ExtraArchivePlugin.UPLOAD_TASK_NAME)
                changeInstallTaskConfiguration(project, extensionBean)
                addArchiveTaskToOutgoingArtifacts(project, extensionBean, ExtraArchivePlugin.SOURCES_JAR_TASK_NAME)
                addArchiveTaskToOutgoingArtifacts(project, extensionBean, ExtraArchivePlugin.TESTS_JAR_TASK_NAME)
                addArchiveTaskToOutgoingArtifacts(project, extensionBean, ExtraArchivePlugin.JAVADOC_JAR_TASK_NAME)
            } else {
                Logc.e("extensionBean is null")
            }


        }
    }

    private void changeInstallTaskConfiguration(Project project, ExtensionBean extensionBean) {
        boolean isCofig = extensionBean.getConfiguration() == Dependency.ARCHIVES_CONFIGURATION
        if (!isCofig) {
            project.tasks.getByName(MavenPlugin.INSTALL_TASK_NAME).configuration = project.configurations[extensionBean.getConfiguration()]
        }
    }

    private void configationDependenceTask(Project project, String taskName) {
        Task archiveTask = project.tasks.findByName(taskName)

        if (archiveTask) {
            project.artifacts.add(extension.getConfiguration(), archiveTask)
            archiveTask.dependsOn(SvnUpdateTask)
        }
    }

    private void addArchiveTaskToOutgoingArtifacts(Project project, ExtensionBean extension, String taskName) {
        Task archiveTask = project.tasks.findByName(taskName)

        if (archiveTask) {
            project.artifacts.add(extension.getConfiguration(), archiveTask)
        }
    }

    private void configureSigning(Project project, ExtensionBean extension) {
        if (extension != null) {

            project.afterEvaluate {
                if (extension.isSign()) {
                    project.signing {
                        required {
                            // Gradle allows project.version to be of type Object and always uses the toString() representation.
                            String uploadTaskName = "upload${extension.getConfiguration().capitalize()}"
                            String pathName = project.getPath() + ":" + uploadTaskName
                            String taskName = (project.rootProject == project) ? uploadTaskName : pathName
                            project.gradle.taskGraph.hasTask(taskName) && !project.version.toString().endsWith('SNAPSHOT')
                        }

                        sign project.configurations[extension.getConfiguration()]

                        project.gradle.taskGraph.whenReady {
                            if (project.signing.required) {
                                getPrivateKeyForSigning(project, extension)
                            }

                            String uploadTaskName = "upload${extension.getConfiguration().capitalize()}"
                            String pathName = project.getPath() + ":" + uploadTaskName
                            String taskPath = (project.rootProject == project) ? uploadTaskName : pathName

                            signPomForUpload(project, taskPath)

                            String installTaskPath = (project.rootProject == project) ? ":$MavenPlugin.INSTALL_TASK_NAME" : "$project.path:$MavenPlugin.INSTALL_TASK_NAME"
                            signInstallPom(project, installTaskPath)
                        }
                    }
                } else {
                    Logc.e("configureSigning sign:" + extension.isSign())
                }
            }
        } else {
            Logc.e("configureSigning extension is null")
        }

    }

    private void getPrivateKeyForSigning(Project project, ExtensionBean extension) {
        String signingKeyId = extension.getSignKeyId()
        if (project.hasProperty(SIGNING_KEY_ID)) {
            signingKeyId = project.property(SIGNING_KEY_ID)
        }

        project.ext.set(SIGNING_KEY_ID, signingKeyId)

        File keyringFile = new File(project.getBuildDir().getPath() + File.separator + "signing" + File.separator, 'secring.gpg')

        if (keyringFile.exists()) {
        } else {
            keyringFile = project.hasProperty(SIGNING_KEYRING) ?
                    project.file(project.property(SIGNING_KEYRING)) :
                    new File(new File(System.getProperty('user.home'), '.gnupg'), 'secring.gpg')
            //throw new GradleException("GnuPG secret key file $keyringFile not found. Please set $SIGNING_KEYRING=/path/to/file.gpg in <USER_HOME>/.gradle/gradle.properties.")
        }
        project.ext.set(SIGNING_KEYRING, keyringFile.getPath())

//        Console console = System.console()
//        console.printf "\nThis release $project.version will be signed with your GnuPG key $signingKeyId in $keyringFile.\n"

        String password = extension.getSignPassword()
        if (password == null || password.equalsIgnoreCase("")) {
            if (!project.hasProperty(SIGNING_PASSWORD)) {
                //password = new String(console.readPassword('Please enter your passphrase to unlock the secret key: '))
                password = project.property(SIGNING_PASSWORD)
            }
        }
        project.ext.set(SIGNING_PASSWORD, password)

    }

    /*private void getPrivateKeyForSigning(Project project,ExtensionBean extension) {
        if (!project.hasProperty(SIGNING_KEY_ID)) {
            throw new GradleException("A GnuPG key ID is required for signing. Please set $SIGNING_KEY_ID=xxxxxxxx in <USER_HOME>/.gradle/gradle.properties.")
        }

        String signingKeyId = project.property(SIGNING_KEY_ID)

        File keyringFile = project.hasProperty(SIGNING_KEYRING) ?
                project.file(project.property(SIGNING_KEYRING)) :
                new File(new File(System.getProperty('user.home'), '.gnupg'), 'secring.gpg')

        if (keyringFile.exists()) {
            project.ext.set(SIGNING_KEYRING, keyringFile.getPath())
        } else {
            throw new GradleException("GnuPG secret key file $keyringFile not found. Please set $SIGNING_KEYRING=/path/to/file.gpg in <USER_HOME>/.gradle/gradle.properties.")
        }

        Console console = System.console()
        console.printf "\nThis release $project.version will be signed with your GnuPG key $signingKeyId in $keyringFile.\n"

        if (!project.hasProperty(SIGNING_PASSWORD)) {
            String password = new String(console.readPassword('Please enter your passphrase to unlock the secret key: '))
            project.ext.set(SIGNING_PASSWORD, password)
        }
    }*/

    private void signPomForUpload(Project project, String uploadTaskPath) {
        def uploadTasks = project.tasks.withType(Upload).matching {
            it.path == uploadTaskPath
        }

        uploadTasks.each { task ->
            task.repositories.mavenDeployer() {
                beforeDeployment { MavenDeployment deployment ->
                    project.signing.signPom(deployment)
                }
            }
        }
    }

    private void signInstallPom(Project project, String installTaskPath) {
        def installTasks = project.tasks.withType(Upload).matching {
            it.path == installTaskPath
        }

        installTasks.each { task ->
            task.repositories.mavenInstaller() {
                beforeDeployment { MavenDeployment deployment ->
                    project.signing.signPom(deployment)
                }
            }
        }
    }

    private void configurePom(Project project, ExtensionBean extension) {
        project.ext.modifyPom = { Closure modification ->
            project.afterEvaluate {
                project.poms.each {
                    it.whenConfigured { project.configure(it, modification) }
                }
            }
        }

        createPomsProjectProperty(project, extension)
    }

    private void createPomsProjectProperty(Project project, ExtensionBean extension) {
        project.afterEvaluate {
            project.ext.poms = []
            Task installTask = project.tasks.findByPath(MavenPlugin.INSTALL_TASK_NAME)

            if (installTask) {
                project.ext.poms << installTask.repositories.mavenInstaller().pom
            }

            String uploadTaskName = "upload${extension.getConfiguration().capitalize()}"
            project.ext.poms << project.tasks.getByName(uploadTaskName).repositories.mavenDeployer().pom
        }
    }

    private void configureUpload(Project project, ExtensionBean extension) {
        project.afterEvaluate {
            String uploadTaskName = "upload${extension.getConfiguration().capitalize()}"
            String pathName = project.getPath() + ":" + uploadTaskName
            String taskPath = (project.rootProject == project) ? uploadTaskName : pathName
            project.tasks.getByName(uploadTaskName).repositories.mavenDeployer() {
                project.gradle.taskGraph.whenReady { TaskExecutionGraph taskGraph ->
                    if (taskGraph.hasTask(taskPath)) {
                        String nexusUsername = ''
                        String nexusPassword = ''
                        String releaseUrl = ''
                        String snapshotUrl = ''
                        int mavenType = PropertyApi.getApi().getProperty().getMavenTye()
                        boolean isRelease = (mavenType == 1 || mavenType == 3) ? true : false
                        ConfigBean conf = ConfigApi.getApi().getConfig()
                        if (conf == null) {
                            Logc.e("config is null")
                            NullPointerException e = new NullPointerException("config is null")
                            e.printStackTrace()
                            throw e
                        }
                        if (mavenType == 2 || mavenType == 3) {
                            if (conf != null && conf.getJcenter() != null) {
                                nexusUsername = conf.getJcenter().getUsername()
                                nexusPassword = conf.getJcenter().getPassword()
                                releaseUrl = conf.getJcenter().getReleaseurl()
                                snapshotUrl = conf.getJcenter().getSnapshots()
                            }
                        } else {
                            if (conf != null && conf.getClife() != null) {
                                nexusUsername = conf.getClife().getUsername()
                                nexusPassword = conf.getClife().getPassword()
                                releaseUrl = conf.getClife().getReleaseurl()
                                snapshotUrl = conf.getClife().getSnapshots()
                            }
                        }

                        if (project.hasProperty(NEXUS_USERNAME)) {
                            nexusUsername = project.property(NEXUS_USERNAME)
                        }
                        if (project.hasProperty(NEXUS_PASSWORD)) {
                            nexusPassword = project.property(NEXUS_PASSWORD)
                        }

                        LibraryBean library = LibraryApi.getApi().getLibrary(project.getName())
                        if (library) {
                            pom.groupId = library.getGroup()
                            pom.artifactId = library.getName()
                            pom.version = library.getVersion(isRelease)
                        } else {
                            NullPointerException e = new NullPointerException("library is null")
                            e.printStackTrace()
                            throw e
                        }

                        if (releaseUrl) {
                            repository(url: project.uri(releaseUrl)) {
                                authentication(userName: nexusUsername, password: nexusPassword)
                            }
                        }

                        if (snapshotUrl) {
                            snapshotRepository(url: project.uri(snapshotUrl)) {
                                authentication(userName: nexusUsername, password: nexusPassword)
                            }
                            //Logc.e("2----->configureUpload:"+releaseUrl+"\r\n"+snapshotUrl+"\r\n"+nexusUsername+"\r\n"+nexusPassword)
                        }


                    }
                }
            }
        }
    }


    void processBuildConifg(Project project) {
        if (project && project != project.getRootProject()) {
            def androidObj = project.getExtensions().getByName("android")
            if (androidObj) {
                def object = androidObj.getProperties().get("defaultConfig")
                if (object) {
                    LibraryBean library = LibraryApi.api.getLibrary(project.name)
                    if (library&&library.isContain()) {
                        String libVersion = library.getVersion()
                        String groups = library.getGroupName()
                        try {
                            Method method = object.getClass().getMethod("buildConfigField", String.class, String.class, String.class)
                            method.invoke(object, "String", "LIB_VERSION", "\"" + libVersion + "\"")
                            method.invoke(object, "String", "LIB_NAME", "\"" + project.name + "\"")
                            method.invoke(object, "String", "LIB_GROUP", "\"" + groups + "\"")
                            method.invoke(object, "String", "BUILDTIME", "\"" + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\"")
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace()
                        } catch (IllegalAccessException e) {
                            e.printStackTrace()
                        } catch (InvocationTargetException e) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

    }

}
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
import com.clife.gradle.bean.conf.BintayBean
import com.clife.gradle.bean.conf.ConfigBean
import com.clife.gradle.bean.lib.LibraryBean
import com.clife.gradle.task.ClifeUploadTask
import com.clife.gradle.task.PreUploadTask
import com.clife.gradle.util.Logc
import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.bundling.Jar

/**
 * <p>A {@link Plugin} that provides task for configuring and uploading artifacts to Sonatype Nexus.</p>
 *
 * @author Benjamin Muschko
 */
class JCenterPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.plugins.apply('com.github.dcendents.android-maven')
//        project.plugins.apply('maven-publish')
        project.plugins.apply('com.jfrog.bintray')
//        project.plugins.apply('maven')
        //project.plugins.apply('java')
        //project.plugins.apply('com.android.library')


        ConfigBean cbean = ConfigApi.api.config
        if (cbean == null) {
            Logc.e("config is null in JCenterPlugin")
            return
        }

        BintayBean bbean = cbean.bintray

        if (bbean == null) {
            Logc.e("config.bintray is null in JCenterPlugin")
            return
        }
        project.group = cbean.group

        if (bbean.bintrayuser == null) {
            Logc.e("config.bintray.user is null in JCenterPlugin")
            return
        }
        if (bbean.bintrayapikey == null) {
            Logc.e("config.bintray.apikey is null in JCenterPlugin")
            return
        }

        LibraryBean lbean = LibraryApi.api.getLibrary(project.name)
        if (lbean == null) {
            Logc.e("LibraryBean is null in JCenterPlugin")
            return
        }


        String libVersion = lbean.getVersion(true)
        project.version = libVersion


        project.bintray {
            user = bbean.bintrayuser
            key = bbean.bintrayapikey


            configurations = ['archives'] //When uploading configuration files
            pkg {
                repo = bbean.repo
                name = project.name
                desc = bbean.desc
                websiteUrl = bbean.siteUrl
                issueTrackerUrl = bbean.issueUrl
                vcsUrl = bbean.vcsUrl
                licenses = [bbean.licenseName]
                labels = ['aar', 'android', 'JCenter', 'sdk', 'clife']
                publicDownloadNumbers = true
                publish = true
            }
        }



        /*project.install {
            repositories.mavenInstaller {
                pom {
                    project {
                        packaging bbean.packaging
                        name bbean.desc
                        url bbean.siteUrl
                        licenses {
                            license {
                                name 'The Apache Software License, Version 2.0'
                                url 'http://www.apache.org/licenses/LICENSE-2.0.txt'
                            }
                        }
                        developers {
                            developer {
                                id bbean.did
                                name bbean.dname
                                email bbean.email
                            }
                        }
                        scm {
                            connection bbean.gitUrl
                            developerConnection bbean.gitUrl
                            url bbean.siteUrl

                        }
                    }
                }
            }
        }*/


        /*project.install {
            repositories.mavenInstaller {
                pom {
                    project {
                        packaging 'aar'
                        name 'wahahahha'
                        url 'https://github.com/szhittech/pubcode/tree/master/hetxxxsdk'
                        licenses {
                            license {
                                name 'The Apache Software License, Version 2.0'
                                url 'http://www.apache.org/licenses/LICENSE-2.0.txt'
                            }
                        }
                        developers {
                            developer {
                                id 'clife'
                                name 'uuxia'
                                email 'xiaoli.xia@clife.cn'
                            }
                        }
                        scm {
                            connection 'https://github.com/szhittech/pubcode.git'
                            developerConnection 'https://github.com/szhittech/pubcode.git'
                            url 'https://github.com/szhittech/pubcode/tree/master/hetxxxsdk'

                        }
                    }
                }
            }
        }*/

        project.afterEvaluate {

            /*project.install {
                repositories.mavenInstaller {
                    pom {
                        project {
                            packaging bbean.packaging
                            name bbean.desc
                            url bbean.siteUrl
                            licenses {
                                license {
                                    name 'The Apache Software License, Version 2.0'
                                    url 'http://www.apache.org/licenses/LICENSE-2.0.txt'
                                }
                            }
                            developers {
                                developer {
                                    id bbean.did
                                    name bbean.dname
                                    email bbean.email
                                }
                            }
                            scm {
                                connection bbean.gitUrl
                                developerConnection bbean.gitUrl
                                url bbean.siteUrl

                            }
                        }
                    }
                }
            }*/

            JavaPluginConvention java = project.convention.getPlugin(JavaPluginConvention.class)
//            javaPluginConvention.sourceCompatibility = MINIMUM_GRADLE_JAVA_VERSION
//            javaPluginConvention.targetCompatibility = MINIMUM_GRADLE_JAVA_VERSION
//            def mainSourceSet = javaPluginConvention.sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
//            SourceDirectorySet allMainSources = mainSourceSet.allSource

            def android = project.extensions.findByName("android")

            def src = null
            if (android == null) {
                src = java.sourceSets.getByName("main").java.srcDirs
            } else {
                src = android.sourceSets.getByName("main").java.srcDirs
            }

            Task genSourcesJarTask = project.task('sourcesJar', type: Jar) {
                classifier = 'sources'
                group = ExtraArchivePlugin.JAR_TASK_GROUP
                description = 'Assembles a jar archive containing the main sources of this project.'
                //from project.sourceSets.main.allSource
                //from android.sourceSets.main.java.srcDirs
                //sourcesJarTask.from(allMainSources)
                //from android.sourceSets.getByName("main").java.srcDirs
                from src

            }

            project.artifacts {
                archives genSourcesJarTask
            }


        }


        /*project.afterEvaluate {
            project.ext.poms = []
            Task installTask = project.tasks.findByPath(MavenPlugin.INSTALL_TASK_NAME)

            if (installTask) {
                project.ext.poms << installTask.repositories.mavenInstaller().pom
            }

//            String uploadTaskName = BintrayUploadTask.getTASK_NAME()
//            project.ext.poms << project.tasks.getByName(uploadTaskName).repositories.mavenDeployer().pom
        }*/


        /*Task installTask = project.tasks.getByName("install")
        if (installTask){
            Logc.e("======================install")
            installTask.configure {
                project.repositories.mavenInstaller {
                    pom {
                        project {
                            packaging bbean.packaging
                            name bbean.desc
                            url bbean.siteUrl
                            licenses {
                                license {
                                    name 'The Apache Software License, Version 2.0'
                                    url 'http://www.apache.org/licenses/LICENSE-2.0.txt'
                                }
                            }
                            developers {
                                developer {
                                    id bbean.did
                                    name bbean.dname
                                    email bbean.email
                                }
                            }
                            scm {
                                connection bbean.gitUrl
                                developerConnection bbean.gitUrl
                                url bbean.siteUrl

                            }
                        }
                    }
                }
            }
        }*/


        String uploadName = BintrayUploadTask.getTASK_NAME()
        project.tasks.create("preUpload", PreUploadTask)
        Task uploadTask = project.tasks.getByName(uploadName)
        uploadTask.dependsOn("preUpload")
        uploadTask.dependsOn("build")

        Task sucess = project.tasks.create("uploadToJCenter", ClifeUploadTask.class)
        sucess.dependsOn(uploadName)
        sucess.setGroup("clife")

        /*Task buildTask = project.tasks.getByName("build")
        buildTask.dependsOn("uploadToJCenter")*/

    }

}
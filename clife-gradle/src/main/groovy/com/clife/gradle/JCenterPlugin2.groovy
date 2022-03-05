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

/**
 * <p>A {@link Plugin} that provides task for configuring and uploading artifacts to Sonatype Nexus.</p>
 *
 * @author Benjamin Muschko
 */
class JCenterPlugin2 implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.plugins.apply('com.novoda.bintray-release')

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


        /*project.publish {
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
        }*/

        project.publish {
            bintrayUser = bbean.bintrayuser
            bintrayKey = bbean.bintrayapikey
            userOrg = bbean.repo
            groupId = cbean.group
            artifactId = lbean.name
            publishVersion = libVersion
            desc = bbean.desc
            website = bbean.vcsUrl
        }


        String uploadName = BintrayUploadTask.getTASK_NAME()
        project.tasks.create("preUpload", PreUploadTask)
        Task uploadTask = project.tasks.getByName(uploadName)
        uploadTask.dependsOn("preUpload")

        Task sucess = project.tasks.create("uploadToJCenter", ClifeUploadTask.class)
        sucess.dependsOn(uploadName)
        sucess.setGroup("clife")

    }

}
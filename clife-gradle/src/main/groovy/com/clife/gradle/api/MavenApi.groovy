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
package com.clife.gradle.api

import com.clife.gradle.bean.conf.ConfigBean
import com.clife.gradle.bean.prop.PropertyBean
import com.clife.gradle.coding.CodingApi
import com.clife.gradle.util.Logc
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * <p>A {@link Plugin} that provides task for configuring and uploading artifacts to Sonatype Nexus.</p>
 *
 * @author Benjamin Muschko
 */
class MavenApi {
    static initMaven(Project project) {
        clife(project)
        third(project)
    }

    static third(Project project) {
        ConfigBean conf = ConfigApi.getApi().getConfig()
        if (conf == null) {
            Logc.e("conf is null in RepoApi")
            return
        }
        List<String> list = conf.getRepo();
        if (list == null) {
            Logc.e("repo list is null in RepoApi")
            return
        }
        for (String urlstring : list) {
            project.repositories {
                maven {
                    url urlstring
                }
            }
        }
    }

    static clife(Project project) {
        ConfigBean conf = ConfigApi.getApi().getConfig()
        if (conf == null) {
            Logc.e("conf is null in RepoApi")
            return
        }
        PropertyBean property = PropertyApi.getApi().getProperty()
        if (property == null) {
            Logc.e("property is null in RepoApi")
            return
        }
        int mavenType = property.getMavenTye()
        String urlstring = null
        switch (mavenType) {
            case 0:
                if (conf.getClife() == null)
                    break
                urlstring = conf.getClife().getSnapshots();
                if (urlstring != null && !urlstring.equalsIgnoreCase("")) {
                    Logc.e("publish clife snapshots maven url:" + urlstring)
                    project.repositories {
                        maven {
                            url urlstring
                            credentials {
                                username = CodingApi.username
                                password = CodingApi.password
                            }
                        }
                    }

                }
                break

            case 1:
                if (conf.getClife() == null)
                    break
                urlstring = conf.getClife().getReleaseurl();
                if (urlstring != null && !urlstring.equalsIgnoreCase("")) {
                    Logc.e("publish clife release maven url:" + urlstring)
                    project.repositories {
                        maven {
                            url urlstring
                            credentials {
                                username = CodingApi.username
                                password = CodingApi.password
                            }
                        }
                    }
                }
                break

            case 2:
                if (conf.getJcenter() == null)
                    break
                urlstring = conf.getJcenter().getSnapshots();
                if (urlstring != null && !urlstring.equalsIgnoreCase("")) {
                    Logc.e("publish Jcenter maven url:" + urlstring)
                    project.repositories {
                        maven {
                            url urlstring
                        }
                    }
                }
                break
            default:
                break
        }
    }

}
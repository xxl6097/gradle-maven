package io.gradle.maven

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.component.SoftwareComponent
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPomDeveloper
import org.gradle.api.publish.maven.MavenPomDeveloperSpec
import org.gradle.api.publish.maven.MavenPomLicense
import org.gradle.api.publish.maven.MavenPomLicenseSpec
import org.gradle.api.publish.maven.MavenPomScm
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.plugins.signing.SigningExtension
import org.gradle.plugins.signing.SigningPlugin

class PublishMaven implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def publishingConfig = project.extensions.create("publishConfig", PublishConfig)
        if (publishingConfig == null){
            throw new NullPointerException('please add publishConfig{} in your build.gradle')
        }

        PublishingExtension publishing = project.extensions.getByType(PublishingExtension)

        project.afterEvaluate {
            configurePublishing(project,publishing)
            if (publishingConfig.version.endsWith('SNAPSHOT')){
                configureSigning(project)
            }
        }

    }

    static void configurePublishing(Project project,PublishingExtension extension){
        project.plugins.apply MavenPublishPlugin
        project.plugins.withType(MavenPublishPlugin.class){
            def version = project.version
            project.extensions.configure(PublishingExtension.class){publishing->
                publishing.publications{publication->
                    publication.create("maven",MavenPublication.class){maven->
                        maven.version = version
                    }
                }
            }
        }
    }

    static void configureSigning(Project project){
        project.plugins.apply SigningPlugin
        project.plugins.withType(SigningPlugin.class){
            project.extensions.configure(SigningExtension.class){signing->
                var publishingExt = project.extensions.getByName('publishing') as PublishingExtension
                signing.sign(publishingExt.publications.getByName("maven"))
            }
        }
    }


    def BaseComponent componentLibrary(){

    }

}



package com.clife.gradle

import com.clife.gradle.util.Logc
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.bundling.Jar

class ExtraArchivePlugin implements Plugin<Project> {
    static final String JAR_TASK_GROUP = BasePlugin.BUILD_GROUP
    static final String SOURCES_JAR_TASK_NAME = 'sourcesJar'
    static final String TESTS_JAR_TASK_NAME = 'testsJar'
    static final String JAVADOC_JAR_TASK_NAME = 'javadocJar'
    static final String UPLOAD_TASK_NAME = 'Upload'

    @Override
    void apply(Project project) {
        ExtraArchivePluginExtension extension = project.extensions.create('extraArchive', ExtraArchivePluginExtension)
        configureTasks(project, extension)
    }

    private void configureTasks(Project project, ExtraArchivePluginExtension extension) {
        project.afterEvaluate {
            project.plugins.withType(JavaPlugin) {
                configureSourcesJarTask(project, extension)
                configureTestsJarTask(project, extension)
                configureJavadocJarTask(project, extension)
            }
        }
    }

    private void configureSourcesJarTask(Project project, ExtraArchivePluginExtension extension) {
        if (extension.sources) {
            Logc.e("========configureSourcesJarTask====>")
            project.task(SOURCES_JAR_TASK_NAME, type: Jar) {
                classifier = 'sources'
                group = JAR_TASK_GROUP
                description = 'Assembles a jar archive containing the main sources of this project.'
                from project.sourceSets.main.allSource
//                from android.sourceSets.main.java.srcDirs
            }
        }
    }

    private void configureTestsJarTask(Project project, ExtraArchivePluginExtension extension) {
        if (extension.tests) {
            project.task(TESTS_JAR_TASK_NAME, type: Jar) {
                classifier = 'tests'
                group = JAR_TASK_GROUP
                description = 'Assembles a jar archive containing the test sources of this project.'
                from project.sourceSets.test.output
            }
        }
    }

    private void configureJavadocJarTask(Project project, ExtraArchivePluginExtension extension) {
        if (extension.javadoc) {
            project.task(JAVADOC_JAR_TASK_NAME, type: Jar) {
                classifier = 'javadoc'
                group = JAR_TASK_GROUP
                description = 'Assembles a jar archive containing the generated Javadoc API documentation of this project.'
                from getDocTask(project)
            }
        }
    }

    /**
     * Checks to see if Groovy plugin got applied to project.
     *
     * @param project Project
     * @return Flag
     */
    private boolean hasGroovyPlugin(Project project) {
        hasPlugin(project, GroovyPlugin)
    }

    private boolean hasPlugin(Project project, Class<? extends Plugin> pluginClass) {
        project.plugins.hasPlugin(pluginClass)
    }

    private Task getDocTask(Project project) {
        hasGroovyPlugin(project) ? project.tasks.getByName(GroovyPlugin.GROOVYDOC_TASK_NAME) : project.tasks.getByName(JavaPlugin.JAVADOC_TASK_NAME)
    }
}

package com.clife.gradle.depend;

import com.clife.gradle.api.LibraryApi;
import com.clife.gradle.api.PropertyApi;
import com.clife.gradle.api.RepoApi;
import com.clife.gradle.bean.ext.ClifeExtention;
import com.clife.gradle.bean.lib.LibraryBean;
import com.clife.gradle.bean.lib.VersionBean;
import com.clife.gradle.util.Logc;
import com.clife.gradle.util.Util;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;

import java.util.Iterator;

public class ClifeDepend {
    public static void onDependency(Project project) {
        project.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(Project project) {
                depend(project);
            }
        });

    }

    private static void depend(Project project) {
        ClifeExtention extension = project.getExtensions().getByType(ClifeExtention.class);
        project.getConfigurations().all(new Action<Configuration>() {
            @Override
            public void execute(Configuration configuration) {
                String name = configuration.getName();
                if (name == "implementation" || name == "compile" || name == "api") {
                    Iterator<Dependency> it = configuration.getDependencies().iterator();
                    StringBuffer logMgs = new StringBuffer();
                    while (it.hasNext()) {
                        Dependency item = it.next();
                        if (item == null || item.getGroup() == null)
                            continue;
                        /*String lib = item.getGroup() + ":" + item.getName() + ":" + item.getVersion();
                        Logc.i("---- " + lib);*/
                        String log = dependency(extension, item);
                        if (log != null) {
                            logMgs.append(log);
                        }
                    }
                    if (logMgs.toString() != null && !logMgs.toString().equalsIgnoreCase("")) {
                        printEnd();
                    }
                }

            }
        });
    }

    private static void printEnd() {
        StringBuffer sBuffer = new StringBuffer();
        String vrgs = "\"clifeArgs {ignVersionWarm true}\"";
        String warm = "Notice: if you want igonre this warm, please add " + vrgs + " in app's build.gradle";
        sBuffer.append(warm);
        sBuffer.append("\r\n");
        sBuffer.append("###################################################################");
        Logc.e(sBuffer.toString());
    }

    private static String dependency(ClifeExtention args, Dependency dependency) {
        if (dependency == null)
            return null;
        LibraryBean dest = LibraryApi.getApi().getLibrary(dependency.getName());
        if (dest == null)
            return null;
        boolean isRelease = PropertyApi.getApi().getProperty().isRelease();
        if (dependency.getVersion().endsWith("SNAPSHOT")){
            isRelease = false;
        }
        VersionBean destVersion = dest.getVersionBean(isRelease);
        if (destVersion == null)
            return null;

        String destVersionName = destVersion.getVersion();
        String localVersionName = dependency.getVersion();
        if (destVersionName == null || localVersionName == null)
            return null;
        if (Util.isInvalidOrUnspecifiedVersion(destVersionName) || Util.isInvalidOrUnspecifiedVersion(localVersionName))
            return null;
        String localFullName = dependency.getGroup() + ":" + dependency.getName() + ":" + dependency.getVersion();
        int cc = Util.versionCompareTo(destVersionName, localVersionName);
        if ((args != null && args.isIgnVersionWarm()) && destVersion.getForceUpdate() == 0)
            return null;

        if (cc <= 0)
            return null;
        String warm = "";
        if (destVersion.getForceUpdate() == 1) {
            warm = "Notice: This version is force update,otherwise build not pass!";
        }


        String mavenAddr = RepoApi.getMavenAddr(dest.getMavenAddr(isRelease));

        StringBuffer sBuffer = new StringBuffer();
        sBuffer.append(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        sBuffer.append("\r\n");
        sBuffer.append("The cur version: ");
        sBuffer.append(localFullName);
        sBuffer.append("\r\n");
        sBuffer.append("The new version: ");
        sBuffer.append(dest.getGroupName(isRelease));
        sBuffer.append("\r\n");
        sBuffer.append("The new Version update note:");
        sBuffer.append(destVersion.getDescription());
        sBuffer.append("\r\n");
        sBuffer.append("wiki:");
        sBuffer.append(dest.getDocLink());
        sBuffer.append("\r\n");
        if (mavenAddr != null && !mavenAddr.equalsIgnoreCase("")) {
            sBuffer.append("maven:");
            sBuffer.append(mavenAddr);
        }
        sBuffer.append("\r\n");
        if (!warm.equalsIgnoreCase("")) {
            sBuffer.append(warm);
            sBuffer.append("\r\n");
        }
        sBuffer.append("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>");
        sBuffer.append("\r\n");
        if (destVersion.getForceUpdate() == 1) {
            IllegalArgumentException e= new IllegalArgumentException(sBuffer.toString());
            e.printStackTrace();
            throw e;
        }

        Logc.e(sBuffer.toString());
        return sBuffer.toString();
    }
}

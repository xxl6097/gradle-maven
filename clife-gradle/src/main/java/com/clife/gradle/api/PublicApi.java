package com.clife.gradle.api;

import com.clife.gradle.bean.ext.ClifeExtention;
import com.clife.gradle.depend.ClifeDepend;
import com.clife.gradle.depend.LibraryCompile;

import org.gradle.api.Project;

public class PublicApi {
    private static PublicApi api;

    public static PublicApi getApi() {
        if (api == null) {
            synchronized (PublicApi.class) {
                if (api == null) {
                    api = new PublicApi();
                }
            }
        }
        return api;
    }

    public void init(Project project) {
        project.getExtensions().create("clifeArgs", ClifeExtention.class);
        //load properties values
        PropertyApi.getApi().init(project);
        //load maven config and public info
        ConfigApi.getApi().init(project);
    }


    public void secondForMaven(Project project) {
        //load local library to publishing
        LibraryApi.getApi().loadCurrentProjectInfo(project);
        project.getExtensions().add("clifelib", new LibraryCompile());

        //check dependcies
        ClifeDepend.onDependency(project);
    }

    public void loadForMaven(Project project) {
        project.getExtensions().create("clifeArgs", ClifeExtention.class);
        //load properties values
        PropertyApi.getApi().init(project);
        //load maven config and public info
        ConfigApi.getApi().init(project);
        //load maven url
        RepoApi.maven(project);
        //load local library to publishing
        LibraryApi.getApi().loadCurrentProjectInfo(project);
        project.getExtensions().add("clifelib", new LibraryCompile());

        //check dependcies
        ClifeDepend.onDependency(project);

    }

    public void loadForPublic(Project project) {
        project.getExtensions().create("clifeArgs", ClifeExtention.class);
        //load properties values
        PropertyApi.getApi().init(project);
        //load maven config and public info
        ConfigApi.getApi().init(project);
        //load maven url
        RepoApi.maven(project);
        //load public's librries
        LibraryApi.getApi().loadAllPublicLibraries(project);

        project.getExtensions().add("clifelib", new LibraryCompile());
        //check dependcies
        ClifeDepend.onDependency(project);
    }



    public void secondForPublic(Project project) {
        //load public's librries
        LibraryApi.getApi().loadAllPublicLibraries(project);

        project.getExtensions().add("clifelib", new LibraryCompile());
        //check dependcies
        ClifeDepend.onDependency(project);
    }

}

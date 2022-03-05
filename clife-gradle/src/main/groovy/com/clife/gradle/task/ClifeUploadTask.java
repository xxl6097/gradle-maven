package com.clife.gradle.task;

import com.clife.gradle.api.LibraryApi;
import com.clife.gradle.bean.lib.LibrariesBean;
import com.clife.gradle.bean.lib.LibraryBean;
import com.clife.gradle.coding.CodingApi;
import com.clife.gradle.util.Logc;
import com.clife.gradle.util.Util;
import com.google.gson.Gson;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

import java.util.Collection;
import java.util.List;

public class ClifeUploadTask extends DefaultTask {

    @TaskAction
    public void onUpload(){
        Project project = getProject();
        if (project != project.getRootProject()){
            showLibraryUrl(project);
        }
    }

    private void showLibraryUrl(Project project){
        LibraryBean library = LibraryApi.getApi().getLibrary(project.getName());
        if (library !=null) {
            StringBuffer sb = new StringBuffer();
            sb.append("Android Library Publish Sucess!\r\n");
            sb.append("Library Name:\r\n");
            sb.append("\t");
            sb.append(library.getGroupName());
            sb.append("\r\n");
            sb.append("Maven Url(you can check it):\r\n");
            sb.append("\t");
            sb.append(library.getUrl());
            sb.append("\r\n");
            //sb.append("please commit the \"library.json\" to Coding.net  as soon as possible!");
            Logc.e(sb.toString());
        }

        List<LibraryBean> alllibs = LibraryApi.getApi().getAllLibrary();
        LibrariesBean bean = new LibrariesBean();
        bean.setLibs(alllibs);
        String json  = new Gson().toJson(bean);
        CodingApi.commitLibrary(json);
    }

}

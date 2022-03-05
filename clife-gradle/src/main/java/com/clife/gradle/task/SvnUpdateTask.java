package com.clife.gradle.task;

import com.clife.gradle.api.SvnApi;
import com.clife.gradle.util.Logc;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;


public class SvnUpdateTask extends DefaultTask {

    @TaskAction
    public void onUpdateSvn() {
        Logc.e("start to update project svn...");
        //As first,we must update svn,then publish aar
        SvnApi.getApi().updateSvn(getProject());
    }
}

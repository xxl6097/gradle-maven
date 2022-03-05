package com.clife.gradle.api;

import com.clife.gradle.util.Logc;

import org.gradle.api.Project;
//import org.tmatesoft.svn.core.SVNDepth;
//import org.tmatesoft.svn.core.SVNException;
//import org.tmatesoft.svn.core.wc.ISVNOptions;
//import org.tmatesoft.svn.core.wc.SVNClientManager;
//import org.tmatesoft.svn.core.wc.SVNRevision;
//import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class SvnApi {
    private static SvnApi api;

    public static SvnApi getApi() {
        if (api == null) {
            synchronized (SvnApi.class) {
                if (api == null) {
                    api = new SvnApi();
                }
            }
        }
        return api;
    }

    public void init(Project project) {
        updateSvn(project);
    }

    public void updateSvn(Project project) {
//        ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
//        SVNClientManager clientManager = SVNClientManager.newInstance(options);
//        try {
//            clientManager.getUpdateClient().doUpdate(project.getProjectDir(), SVNRevision.HEAD, SVNDepth.INFINITY, false, false);
//        } catch (SVNException e) {
//            e.printStackTrace();
//            Logc.e(e.getMessage());
//        }
    }
}

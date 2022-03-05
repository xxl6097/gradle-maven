package com.clife.gradle.api;

import com.clife.gradle.bean.conf.ConfigBean;
import com.clife.gradle.bean.lib.LibrariesBean;
import com.clife.gradle.bean.lib.LibraryBean;
import com.clife.gradle.coding.CodingApi;
import com.clife.gradle.util.Logc;
import com.clife.gradle.util.Util;
import com.google.gson.Gson;

import org.gradle.api.Project;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LibraryApi {
    private static LibraryApi api;
    private LibrariesBean libraries;
    private Map<String, LibraryBean> libraryMap = new HashMap<>();

    public static LibraryApi getApi() {
        if (api == null) {
            synchronized (LibraryApi.class) {
                if (api == null) {
                    api = new LibraryApi();
                }
            }
        }
        return api;
    }

    public LibrariesBean getLibraries() {
        return libraries;
    }

    public Map<String, LibraryBean> getLibraryMap() {
        return libraryMap;
    }

    public List<LibraryBean> getAllLibrary() {
        Collection<LibraryBean> lists = libraryMap.values();
        return new ArrayList(lists);
    }


    public LibraryBean getLibrary(String name) {
        LibraryBean lib = libraryMap.get(name);
        if (lib != null) {
//            Logc.e("===>getLibrary:" + lib.toString());
        }
        return lib;
    }

    public void loadCurrentProjectInfo(Project project) {
        String filepath = PropertyApi.getApi().getProperty().getLibraryJsonUri();
        if ((filepath != null && !filepath.equalsIgnoreCase("")) && filepath.startsWith("http")) {
            libraries = CodingApi.getLibraryJson(project, filepath, LibrariesBean.class);
            if (libraries == null) {
                NullPointerException e = new NullPointerException("project's library.json is null,path is " + filepath);
                e.printStackTrace();
                throw e;
            }
        } else {
            String mavenFilePath = project.getRootDir().getPath() + File.separator + "library.json";
            if (Util.isExists(mavenFilePath)) {
                filepath = mavenFilePath;
                //Logc.e("library.json is exist in local:"+filepath);
            } else {
                if (filepath != null && !filepath.equalsIgnoreCase("")) {

                } else {
                    IllegalStateException e = new IllegalStateException("Error:" + mavenFilePath + " does not exist");
                    e.printStackTrace();
                    throw e;
                }
            }

            File file = new File(filepath);
            if (!file.exists()) {
                IllegalStateException e = new IllegalStateException("Error:" + file.getAbsolutePath() + " does not exist");
                e.printStackTrace();
                throw e;
            }

            if (PropertyApi.getApi().getProperty().isDebug()) {
                Logc.e("LibraryApi.filepath:" + file.getPath());
            }
            String jsonString = Util.readJsonData(file.getPath());

            libraries = new Gson().fromJson(jsonString, LibrariesBean.class);
            //Logc.e(WordUtil.STRING_7 + file.getCanonicalPath());
            if (PropertyApi.getApi().getProperty().isDebug()) {
               // Logc.e("LibraryApi.json:" + jsonString);
                System.out.println("LibraryApi.json:" +jsonString);
            }
            if (libraries == null) {
                NullPointerException e = new NullPointerException("project's library.json is null,path is " + filepath);
                e.printStackTrace();
                throw e;
            }
        }

        if (libraries != null) {
            List<LibraryBean> libs = libraries.getLibs();
            if (libs != null && libs.size() > 0) {
                for (LibraryBean item : libs) {
                    libraryMap.put(item.getName(), item);
                }
            }

        }


        loadAllPublicLibraries(project);
//        Logc.e("===>libraries:" + libraries.toString());
//        Logc.e("===>libraryMap:" + libraryMap.toString());
    }


    public void loadAllPublicLibraries(Project project) {
        if (PropertyApi.getApi().getProperty().isIgnoreVersion())
            return;
        ConfigBean configs = ConfigApi.getApi().getConfig();
        if (configs == null) {
            Logc.e("config is null,at loadAllPublicLibraries fuc");
            return;
        }
        List<String> libraries = configs.getLibrariesuri();
        if (libraries == null) {
            Logc.e("libraries is null,at loadAllPublicLibraries fuc");
            return;
        }
        for (String url : libraries) {
            if (url == null || url.equalsIgnoreCase(""))
                continue;
            LibrariesBean libsResp = Util.getSvnJson(url, LibrariesBean.class);
            if (libsResp == null)
                continue;
            List<LibraryBean> libs = libsResp.getLibs();
            if (libs == null || libs.isEmpty())
                continue;
            for (LibraryBean item : libs) {
                if (!libraryMap.containsKey(item.getName())) {
                    libraryMap.put(item.getName(), item);
                }
            }
        }


        LibrariesBean libsResp = CodingApi.getLibraryJson(project, PropertyApi.getApi().getProperty().getAllLibJson(), LibrariesBean.class);
//        Logc.e("LibrariesBean :" + libsResp.toString());
        if (libsResp != null) {
            List<LibraryBean> libs = libsResp.getLibs();
            for (LibraryBean item : libs) {
                if (!libraryMap.containsKey(item.getName())) {
                    libraryMap.put(item.getName(), item);
                }
            }
        }

//        Logc.e("libraries size:" + libraryMap.toString());
    }
}

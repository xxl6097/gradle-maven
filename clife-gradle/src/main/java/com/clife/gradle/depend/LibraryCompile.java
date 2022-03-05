package com.clife.gradle.depend;


import com.clife.gradle.api.LibraryApi;
import com.clife.gradle.bean.lib.LibraryBean;
import com.clife.gradle.util.Logc;

import java.util.Map;


public class LibraryCompile {

    public String get(String name) {
        Map<String, LibraryBean> mapping = LibraryApi.getApi().getLibraryMap();
        if (mapping == null) {
            IllegalStateException e =  new IllegalStateException("Error:public library load failed:" + mapping);
            e.printStackTrace();
            throw e;
        }
        if (!mapping.containsKey(name)) {
            IllegalStateException e = new IllegalStateException("Error:public library not contain this library:" + name);
            e.printStackTrace();
            throw e;
        }

        String library = mapping.get(name).getGroupName();
        Logc.e("Dependence:" + library);
        return library;
    }
}

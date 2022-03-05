package com.clife.gradle.bean.lib;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LibrariesBean implements Serializable {
    private List<LibraryBean> libs;

    public List<LibraryBean> getLibs() {
        return libs;
    }

    public void setLibs(List<LibraryBean> libs) {
        this.libs = libs;
    }

    public Map<String, LibraryBean> getNameMapping() {
        Map<String, LibraryBean> result = new HashMap<>();
        for (LibraryBean library : libs) {
            result.put(library.getName(), library);
        }
        return result;
    }

    @Override
    public String toString() {
        return "LibrariesBean{" +
                "libs=" + libs +
                '}';
    }
}

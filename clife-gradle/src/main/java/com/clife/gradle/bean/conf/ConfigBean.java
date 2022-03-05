package com.clife.gradle.bean.conf;


import java.io.Serializable;
import java.util.List;

public class ConfigBean implements Serializable {

    /**
     * name : hetbasicsdk
     * group : com.github.szhittech
     * pomPackaging : aar
     * jcenter : {"username":"szhittech","password":"het123456","snapshots":"http://200.200.200.40:8083/nexus/content/repositories/clife-android-snapshots/","releaseurl":"https://oss.sonatype.org/service/local/staging/deploy/maven2/"}
     * clife : {"username":"clife-android","password":"add123","snapshots":"http://200.200.200.40:8083/nexus/content/repositories/clife-android-snapshots/","releaseurl":"http://200.200.200.40:8083/nexus/content/repositories/clife-android-releases/","publicurl":"http://200.200.200.40:8083/nexus/content/groups/public"}
     * repo : ["asfjesjfesf","skfejfe"]
     * docLink : http://svn.uuxia.cn/svn/repositories/android/wiki/android/open/index.html
     * isrelease : 0
     * librariesuri : ["http://svn.uuxia.cn/svn/repositories/android/code/trunk/public/sdkversion.json","http://svn.uuxia.cn/svn/repositories/android/code/trunk/public/conf.json"]
     */

    private String name;
    private String group;
    private String pomPackaging;
    private NexusBean jcenter;
    private NexusBean clife;
    private String docLink;
    private int isrelease;
    private List<String> repo;
    private List<String> librariesuri;
    /**
     * extension : {"configuration":"archives","sign":false}
     */
    private ExtensionBean extension;
    private PluginVersionBean plugin;
    private BintayBean bintray;

    public BintayBean getBintray() {
        return bintray;
    }

    public void setBintray(BintayBean bintray) {
        this.bintray = bintray;
    }

    public PluginVersionBean getPlugin() {
        return plugin;
    }

    public void setPlugin(PluginVersionBean plugin) {
        this.plugin = plugin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getPomPackaging() {
        return pomPackaging;
    }


    public void setPomPackaging(String pomPackaging) {
        this.pomPackaging = pomPackaging;
    }

    public NexusBean getJcenter() {
        return jcenter;
    }

    public void setJcenter(NexusBean jcenter) {
        this.jcenter = jcenter;
    }

    public NexusBean getClife() {
        return clife;
    }

    public void setClife(NexusBean clife) {
        this.clife = clife;
    }

    public String getDocLink() {
        return docLink;
    }

    public void setDocLink(String docLink) {
        this.docLink = docLink;
    }

    public int getIsrelease() {
        return isrelease;
    }

    public void setIsrelease(int isrelease) {
        this.isrelease = isrelease;
    }

    public List<String> getRepo() {
        return repo;
    }

    public void setRepo(List<String> repo) {
        this.repo = repo;
    }

    public List<String> getLibrariesuri() {
        return librariesuri;
    }

    public void setLibrariesuri(List<String> librariesuri) {
        this.librariesuri = librariesuri;
    }

    public ExtensionBean getExtension() {
        return extension;
    }

    public void setExtension(ExtensionBean extension) {
        this.extension = extension;
    }

    @Override
    public String toString() {
        return "ConfigBean{" +
                "name='" + name + '\'' +
                ", group='" + group + '\'' +
                ", pomPackaging='" + pomPackaging + '\'' +
                ", jcenter=" + jcenter +
                ", clife=" + clife +
                ", docLink='" + docLink + '\'' +
                ", isrelease=" + isrelease +
                ", repo=" + repo +
                ", librariesuri=" + librariesuri +
                ", extension=" + extension +
                '}';
    }
}

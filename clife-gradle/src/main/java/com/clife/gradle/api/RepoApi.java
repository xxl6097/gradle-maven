package com.clife.gradle.api;

import com.clife.gradle.bean.conf.ConfigBean;
import com.clife.gradle.bean.prop.PropertyBean;
import com.clife.gradle.coding.CodingApi;
import com.clife.gradle.util.Logc;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.artifacts.repositories.PasswordCredentials;

import java.util.List;

public class RepoApi {
    public static void maven(Project project) {
        //MavenApi.initMaven(project);
        /*project.getRepositories().maven(new Action<MavenArtifactRepository>() {
            @Override
            public void execute(MavenArtifactRepository mavenArtifactRepository) {
                mavenClife(mavenArtifactRepository);
                mavenThird(mavenArtifactRepository);
            }
        });*/
    }

    private static void mavenThird(MavenArtifactRepository mavenArtifactRepository) {
        ConfigBean conf = ConfigApi.getApi().getConfig();
        if (conf == null) {
            Logc.e("conf is null in RepoApi");
            return;
        }
        List<String> list = conf.getRepo();
        if (list == null) {
            Logc.e("repo list is null in RepoApi");
            return;
        }
        for (String url : list) {
            //Logc.e("plugin auto third maven url:" + url);
            mavenArtifactRepository.setUrl(url);
        }
    }

    private static void mavenClife(MavenArtifactRepository mavenArtifactRepository) {
        ConfigBean conf = ConfigApi.getApi().getConfig();
        if (conf == null) {
            Logc.e("conf is null in RepoApi");
            return;
        }
        PropertyBean property = PropertyApi.getApi().getProperty();
        if (property == null) {
            Logc.e("property is null in RepoApi");
            return;
        }
        int mavenType = property.getMavenTye();
        //Logc.e("mavenType is " + mavenType);
        String url = null;
        switch (mavenType) {
            case 0:
                if (conf.getClife() == null)
                    break;
                url = conf.getClife().getSnapshots();
                if (url != null && !url.equalsIgnoreCase("")) {
                    Logc.e("plugin auto public maven url:" + url);
                    mavenArtifactRepository.setUrl(url);
                    mavenArtifactRepository.credentials(new Action<PasswordCredentials>() {
                        @Override
                        public void execute(PasswordCredentials passwordCredentials) {
                            passwordCredentials.setUsername(CodingApi.username);
                            passwordCredentials.setPassword(CodingApi.password);
                            Logc.e("plugin auto public maven url:" + CodingApi.username + CodingApi.password);
                        }
                    });

                }
                break;
            case 1:
                if (conf.getClife() == null)
                    break;
                url = conf.getClife().getReleaseurl();
                if (url != null && !url.equalsIgnoreCase("")) {
                    //Logc.e("plugin auto public maven url:" + url);
                    mavenArtifactRepository.setUrl(url);
                    mavenArtifactRepository.credentials(new Action<PasswordCredentials>() {
                        @Override
                        public void execute(PasswordCredentials passwordCredentials) {
                            passwordCredentials.setUsername(CodingApi.username);
                            passwordCredentials.setPassword(CodingApi.password);
                        }
                    });
                }
                break;
            case 2:
                if (conf.getJcenter() == null)
                    break;
                url = conf.getJcenter().getSnapshots();
                if (url != null && !url.equalsIgnoreCase("")) {
                    //Logc.e("plugin auto public maven url:" + url);
                    mavenArtifactRepository.setUrl(url);
                }
                break;
            default:
                break;
        }

    }

    public static String getMavenAddr(String library) {
        PropertyBean property = PropertyApi.getApi().getProperty();
        if (property != null) {
            String url = ConfigApi.getApi().getNexsusAddress(property.getMavenTye());
            url += library;
            return url;
        }
        return null;
    }
}

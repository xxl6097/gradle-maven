package io.github.szhittech.repository;

public class RepositoryEntity {
    private String name = null;
    private String repositoryUrl = null;
    private String username = null;
    private String password = null;

    public RepositoryEntity() {
    }

    public RepositoryEntity(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    public RepositoryEntity(String name, String repositoryUrl) {
        this.name = name;
        this.repositoryUrl = repositoryUrl;
    }

    public RepositoryEntity(String name, String username, String password, String repositoryUrl) {
        this.name = name;
        this.repositoryUrl = repositoryUrl;
        this.username = username;
        this.password = password;
    }

    public RepositoryEntity(String repositoryUrl, String username, String password) {
        this.repositoryUrl = repositoryUrl;
        this.username = username;
        this.password = password;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "RepositoryEntity{" +
                "name='" + name + '\'' +
                ", repositoryUrl='" + repositoryUrl + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}

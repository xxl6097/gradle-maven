package io.gradle.maven

class PublishConfig {
    String groupId = "io.github.szhittech"
    String artifactId = ""
    String version = ""

    String pomName = ""
    String pomDescription = ""
    String pomUrl = ""

    String repoSnapshot = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
    String repoRelease = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
    String repoName = "szhittech"
    String repoPassword = "het123456"
}

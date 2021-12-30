# Android Library和Java Library发布Maven Center的方法

#### 介绍
本项目只要演示Android Library项目和Java Library项目如何发布SNAPSHOT和RELEASE版到Maven Center,其中包含了源码示范。

## 主要文件介绍

1. gradle.properties
2. xxx-library/build.gradle
3. secring.gpg
4. xxx-publish.gradle

注： xxx代表android或者java


### gradle.properties源文件介绍

    GROUP=io.github.szhittech
    #以下变量可以更改为本地私服的用户名密码和地址
    USERNAME=szhittech
    PASSWORD=het123456
    RELEASEURL=https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/
    SNAPSHOTSURL=https://s01.oss.sonatype.org/content/repositories/snapshots/
    #签名信息
    signing.keyId=FB58CB54
    signing.password=2475431305
    signing.secretKeyRingFile=../secring.gpg
    #以下是非必填项
    AUTHOR_ID=uuxia
    AUTHOR_NAME=xiaxiaoli
    AUTHOR_EMAIL='xiaoli.xia@clife.cn

| 参数                  | 必选  | 描述                             |
|:--------------------|:----|--------------------------------|
| GROUP               | 是   | 组名称，需要在[sonatype](https://issues.sonatype.org)注册认证，详见组名注册 |
| USERNAME            | 是   | 私服用户名                          |
| PASSWORD            | 是   | 私服密码                           |
| RELEASEURL          | 是   | 私服release地址                    |
| SNAPSHOTSURL        | 是   | 私服快照地址                         |
| signing.keyId       | 是   | 签名key                          |
| signing.password    | 是   | 签名密码                           |
| signing.secretKeyRingFile    | 是   | 注意签名文件路径                           |
| AUTHOR_ID           | 否   | 开发者ID，随意填写                     |
| AUTHOR_NAME         | 否   | 开发者姓名                          |
| AUTHOR_EMAIL        | 否   | 开发者姓名邮箱                        |


### xxx-library/build.gradle源文件介绍


    ext {
        NAME = 'hetandroidsdk'
        //加上-SNAPSHOT后缀就是发布快照版本,需要加SNAPSHOT的maven依赖
        VERSION = '0.0.0'
        //以下是非必填项
        DESCRIPTION='A Library For Android'
        SRCURL='https://github.com/szhittech/hetandroidsdk'
        CONNECTION='scm:git@github.com:szhittech/hetandroidsdk.git'
    }
    //这个依赖地址一定要正确
    apply from: '../android-publish.gradle'

### secring.gpg文件介绍

签名文件，可以直接下载本工程中签名文件使用

### 使用说明
1. 下载`xxx-publish.gradle`文件只工程根目录；
2. 下载`secring.gpg`文件只工程根目录；
3. 配置`gradle.properties`文件，可以直接拷贝本工程的内容；
4. java到library目录下执行`gradle publish`,Android-library到library目录下执行`gradle uploadArchives`
5. 上述步骤成功后，请登录[https://s01.oss.sonatype.org/](https://s01.oss.sonatype.org/);
6. 页面左侧栏点击`Build Promotion`->`Staging Repositories`；
7. 在`Staging Repositories`选项卡可以看到刚提交的release版本库，如：`iogithubszhittect-xxxx`;
8. 勾选`iogithubszhittect-xxxx`，点击`Close`；
9. 稍等几十秒 `Refresh`，再次勾选`iogithubszhittect-xxxx`，点击`Release`,即可发布成功，等待大概4小时；

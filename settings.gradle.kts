pluginManagement {
    repositories {
        // 腾讯云Gradle镜像
        maven {
            url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
        }
        // 阿里云Maven镜像
        maven {
            url = uri("https://maven.aliyun.com/repository/public/")
        }
        // 华为云镜像
        maven {
            url = uri("https://repo.huaweicloud.com/repository/maven/")
        }
        // Google Maven仓库
        google {
            url = uri("https://maven.aliyun.com/repository/google/")
        }
        // Maven Central
        maven {
            url = uri("https://maven.aliyun.com/repository/central/")
        }
        // Gradle Plugin Portal
        gradlePluginPortal()
        // 官方仓库作为备选
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // 腾讯云Gradle镜像
        maven {
            url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
        }
        // 阿里云Maven镜像
        maven {
            url = uri("https://maven.aliyun.com/repository/public/")
        }
        // 华为云镜像
        maven {
            url = uri("https://repo.huaweicloud.com/repository/maven/")
        }
        // Google Maven仓库
        google {
            url = uri("https://maven.aliyun.com/repository/google/")
        }
        // Maven Central
        maven {
            url = uri("https://maven.aliyun.com/repository/central/")
        }
        // Spring仓库
        maven {
            url = uri("https://maven.aliyun.com/repository/spring/")
        }
        // 官方仓库作为备选
        mavenCentral()
    }
}

rootProject.name = "CodeChecker"
include(":app")

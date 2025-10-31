/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
//    alias(libs.plugins.kotlin.jvm)
    `kotlin-dsl`
}

repositories {
    maven("https://mirrors.tencent.com/nexus/repository/maven-public")
    maven {
        isAllowInsecureProtocol = true
        name = "Nenus"
        setUrl("http://maven.cloud.cicoe.net/repository/kmp/")
        credentials {
            username = "kmp2"
            password = "notekmp1504"
        }
    }
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.dokka.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
}

dependencies {
    implementation(libs.jsoup)
}

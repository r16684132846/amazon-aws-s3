/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
//    alias(libs.plugins.kotlinx.serialization)
}

group = "aws.sdk.kotlin"

repositories {
    maven("https://mirrors.tencent.com/nexus/repository/maven-public")
    maven("https://mirrors.tencent.com/nexus/repository/maven-tencent")
    maven {
        isAllowInsecureProtocol = true
        name = "Nenus"
        setUrl("http://maven.cloud.cicoe.net/repository/kmp/")
        credentials {
            username = "kmp2"
            password = "notekmp1504"
        }
    }
    mavenLocal()
    mavenCentral()
}

dependencies {
    compileOnly(kotlin("gradle-plugin"))
    compileOnly(kotlin("gradle-plugin-api"))

    implementation(libs.smithy.model)
    implementation(libs.smithy.aws.traits)
    implementation(libs.smithy.protocol.traits)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.kotlin.test.junit5)
}

gradlePlugin {
    plugins {
        create("sdk-bootstrap") {
            id = "sdk-bootstrap"
            implementationClass = "aws.sdk.kotlin.gradle.sdk.Bootstrap"
        }
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
        showStackTraces = true
        showExceptions = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

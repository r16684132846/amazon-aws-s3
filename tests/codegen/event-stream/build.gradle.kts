/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import aws.sdk.kotlin.gradle.codegen.dsl.smithyKotlinPlugin
import aws.sdk.kotlin.gradle.codegen.smithyKotlinProjectionSrcDir
import aws.sdk.kotlin.tests.codegen.CodegenTest
import aws.sdk.kotlin.tests.codegen.Model

description = "AWS SDK for Kotlin's event stream codegen test suite"

val tests = listOf(
    CodegenTest(
        "restJson1",
        Model("event-stream-model-template.smithy"),
        "aws.sdk.kotlin.test#TestService",
    ),
    CodegenTest(
        "awsJson11",
        Model("event-stream-initial-request-response.smithy"),
        "aws.sdk.kotlin.test#TestService",
    ),
)

smithyBuild {
    tests.forEach { test ->
        projections.register(test.name) {
            imports = listOf(layout.projectDirectory.file(test.model.path + test.model.fileName).asFile.absolutePath)
            transforms = listOf(
                """
                {
                  "name": "includeServices",
                  "args": {
                    "services": ["${test.serviceShapeId}"]
                  }
                }
                """,
            )
            smithyKotlinPlugin {
                serviceShapeId = test.serviceShapeId
                packageName = "aws.sdk.kotlin.test.${test.name.lowercase()}"
                packageVersion = "1.0"
                buildSettings {
                    generateFullProject = false
                    generateDefaultBuildFiles = false
                    optInAnnotations = listOf(
                        "aws.smithy.kotlin.runtime.InternalApi",
                        "aws.sdk.kotlin.runtime.InternalSdkApi",
                    )
                }
            }
        }
    }
}

kotlin.sourceSets.getByName("test") {
    smithyBuild.projections.forEach {
        kotlin.srcDir(smithyBuild.smithyKotlinProjectionSrcDir(it.name))
    }
}

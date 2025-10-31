/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package aws.sdk.kotlin.e2etest

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.abortMultipartUpload
import aws.sdk.kotlin.services.s3.createMultipartUpload
import aws.sdk.kotlin.services.s3.model.CompletedPart
import aws.sdk.kotlin.services.s3.paginators.listPartsPaginated
import aws.sdk.kotlin.services.s3.uploadPart
import aws.smithy.kotlin.runtime.content.ByteStream
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertContentEquals
import kotlin.time.Duration.Companion.seconds

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PaginatorTest {
    private val client = S3Client {
        region = S3TestUtils.DEFAULT_REGION
    }

    private lateinit var testBucket: String

    @BeforeAll
    fun createResources(): Unit = runBlocking {
        testBucket = S3TestUtils.getTestBucket(client)
    }

    @AfterAll
    fun cleanup() = runBlocking {
        S3TestUtils.deleteBucketAndAllContents(client, testBucket)
    }

    // ListParts has a strange pagination termination condition via [IsTerminated]. Verify it actually works correctly.
    @Test
    fun testListPartsPagination() = runBlocking {
        val chunk = "!".repeat(5 * 1024 * 1024).encodeToByteArray() // Parts must be at least 5MB
        val expectedParts = (1..10).toList()

        val id = client.createMultipartUpload {
            bucket = testBucket
            key = "list-parts-test"
        }.uploadId!!

        try {
            expectedParts.map { idx ->
                val part = client.uploadPart {
                    bucket = testBucket
                    key = "list-parts-test"
                    uploadId = id
                    partNumber = idx
                    body = ByteStream.fromBytes(chunk)
                }
                CompletedPart {
                    partNumber = idx
                    eTag = part.eTag
                }
            }

            val actualParts = withTimeout(10.seconds) {
                // Failure behavior is infinite loop so cap at 10 seconds
                client.listPartsPaginated {
                    bucket = testBucket
                    key = "list-parts-test"
                    uploadId = id
                }
                    .transform { it.parts?.forEach { it.partNumber?.let { emit(it) } } }
                    .toList()
                    .sorted()
            }

            assertContentEquals(expectedParts, actualParts)
        } finally {
            client.abortMultipartUpload {
                bucket = testBucket
                key = "list-parts-test"
                uploadId = id
            }
        }
    }
}

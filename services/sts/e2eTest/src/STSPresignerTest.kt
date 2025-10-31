/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package aws.sdk.kotlin.services.sts

import aws.sdk.kotlin.services.sts.model.GetCallerIdentityRequest
import aws.sdk.kotlin.services.sts.presigners.presignGetCallerIdentity
import aws.sdk.kotlin.testing.withAllEngines
import aws.smithy.kotlin.runtime.http.SdkHttpClient
import aws.smithy.kotlin.runtime.http.complete
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.TestInstance
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

/**
 * Tests for presigner
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StsPresignerTest {
    @Test
    fun testGetCallerIdentityPresigner() = runBlocking {
        val req = GetCallerIdentityRequest { }

        val presignedRequest = StsClient { region = "us-west-2" }.use { sts ->
            sts.presignGetCallerIdentity(req, 60.seconds)
        }

        withAllEngines { engine ->
            val httpClient = SdkHttpClient(engine)
            val call = httpClient.call(presignedRequest)
            call.complete()

            assertEquals(200, call.response.status.value, "presigned sts request failed for engine: $engine")
        }
    }
}

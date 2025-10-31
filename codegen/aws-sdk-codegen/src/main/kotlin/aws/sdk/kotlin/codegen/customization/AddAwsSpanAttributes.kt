/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package aws.sdk.kotlin.codegen.customization

import software.amazon.smithy.kotlin.codegen.core.RuntimeTypes
import software.amazon.smithy.kotlin.codegen.core.withBlock
import software.amazon.smithy.kotlin.codegen.integration.KotlinIntegration
import software.amazon.smithy.kotlin.codegen.integration.SectionWriter
import software.amazon.smithy.kotlin.codegen.integration.SectionWriterBinding
import software.amazon.smithy.kotlin.codegen.rendering.protocol.HttpProtocolClientGenerator

/**
 * Set AWS specific span attributes for an operation
 * See https://opentelemetry.io/docs/reference/specification/trace/semantic_conventions/instrumentation/aws-sdk/
 */
class AddAwsSpanAttributes : KotlinIntegration {
    override val sectionWriters: List<SectionWriterBinding>
        get() = listOf(SectionWriterBinding(HttpProtocolClientGenerator.OperationTelemetryBuilder, addAwsSpanAttrWriter))

    private val addAwsSpanAttrWriter = SectionWriter { w, _ ->
        w.withBlock("attributes = #T {", "}", RuntimeTypes.Core.Collections.attributesOf) {
            write("#S to #S", "rpc.system", "aws-api")
        }
    }
}

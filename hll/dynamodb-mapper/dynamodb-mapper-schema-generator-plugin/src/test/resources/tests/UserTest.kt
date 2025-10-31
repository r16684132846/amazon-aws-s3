/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.example

import aws.sdk.kotlin.hll.dynamodbmapper.model.itemOf
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.smithy.kotlin.runtime.ExperimentalApi
import org.example.dynamodbmapper.generatedschemas.UserConverter
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalApi::class)
class UserTest {
    @Test
    fun testConversion() {
        val user = User(123, "Steve", "Rogers", 84)
        val converted = UserConverter.convertTo(user)

        assertEquals(
            itemOf(
                "id" to AttributeValue.N("123"),
                "fName" to AttributeValue.S("Steve"),
                "lName" to AttributeValue.S("Rogers"),
                "age" to AttributeValue.N("84"),
            ),
            converted,
        )

        val unconverted = UserConverter.convertFrom(converted)

        assertEquals(user, unconverted)
    }
}

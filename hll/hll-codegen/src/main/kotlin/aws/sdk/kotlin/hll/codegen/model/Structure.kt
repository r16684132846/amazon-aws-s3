/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package aws.sdk.kotlin.hll.codegen.model

import aws.sdk.kotlin.runtime.InternalSdkApi
import aws.smithy.kotlin.runtime.collections.Attributes
import aws.smithy.kotlin.runtime.collections.emptyAttributes
import aws.smithy.kotlin.runtime.collections.get
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSTypeReference

/**
 * Describes a structure (i.e., class, struct, etc.) which contains zero or more [Member] instances
 * @param type The [TypeRef] for this structure, which includes its name and Kotlin package
 * @param members The [Member] instances which are part of this structure
 * @param attributes An [Attributes] collection for associating typed attributes with this structure
 */
@InternalSdkApi
public data class Structure(
    val type: TypeRef,
    val members: Set<Member>,
    val attributes: Attributes = emptyAttributes(),
) {
    @InternalSdkApi
    public companion object {
        /**
         * Derives a [Structure] from the given [KSTypeReference]
         */
        public fun from(ksTypeRef: KSTypeReference): Structure {
            val struct = Structure(
                type = Type.from(ksTypeRef),
                members = (ksTypeRef.resolve().declaration as KSClassDeclaration)
                    .getDeclaredProperties()
                    .map(Member.Companion::from)
                    .toSet(),
            )

            return ModelParsingPlugin.transform(struct, ModelParsingPlugin::postProcessStructure)
        }
    }
}

/**
 * Gets a collection of all generic variables referenced by/in a [Structure], including in the structure's [TypeRef] and
 * in the [TypeRef]s of every member
 */
@InternalSdkApi
public fun Structure.genericVars(): List<TypeVar> = buildList {
    addAll(type.genericVars())
    members.flatMap { it.type.genericVars() }.let(::addAll)
}

/**
 * Gets the low-level [Structure] equivalent for this high-level structure
 */
@InternalSdkApi
public val Structure.lowLevel: Structure
    get() = attributes[ModelAttributes.LowLevelStructure]

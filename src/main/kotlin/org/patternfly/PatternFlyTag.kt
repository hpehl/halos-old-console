package org.patternfly

import kotlinx.html.Tag
import kotlinx.html.TagConsumer
import kotlinx.html.visitTag
import kotlinx.html.visitTagAndFinalize

internal fun <T : PatternFlyTag> T.visitPf(block: T.() -> Unit) = visitTag {
    setupPatternFlyTag(this, block)
}

internal fun <T : PatternFlyTag, R> T.visitPfAndFinalize(consumer: TagConsumer<R>, block: T.() -> Unit): R =
    visitTagAndFinalize(consumer) {
        setupPatternFlyTag(this, block)
    }

private fun <T : PatternFlyTag> setupPatternFlyTag(tag: T, block: T.() -> Unit) {
    if (tag is Ouia) {
        setOuiaType(tag)
    }
    tag.attributes[Dataset.COMPONENT_TYPE.long] = tag.componentType.id
    tag.head()
    tag.block()
    tag.tail()
}

/** Holds information for building PatternFly components as part of the HTML DSL */
interface PatternFlyTag : Tag {

    val componentType: ComponentType
    fun head() = Unit
    fun tail() = Unit
}

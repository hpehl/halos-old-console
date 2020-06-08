package org.patternfly

import kotlinx.html.DIV
import kotlinx.html.FlowContent
import kotlinx.html.HR
import kotlinx.html.HtmlTagMarker
import kotlinx.html.LI
import kotlinx.html.TagConsumer
import kotlinx.html.attributesMapOf
import kotlinx.html.visitAndFinalize
import org.w3c.dom.HTMLElement

// ------------------------------------------------------ dsl

@HtmlTagMarker
fun TagConsumer<HTMLElement>.pfDivider(
    variant: DividerVariant = DividerVariant.HR,
    vararg classes: String
): HTMLElement =
    when (variant) {
        DividerVariant.HR ->
            HR(
                attributesMapOf("class", "divider".component().append(*classes)),
                this
            ).visitAndFinalize(this) {}
        DividerVariant.DIV ->
            DIV(
                attributesMapOf("class", "divider".component().append(*classes), "role", "separator"),
                this
            ).visitAndFinalize(this) {}
        DividerVariant.LI ->
            LI(
                attributesMapOf("class", "divider".component().append(*classes), "role", "separator"),
                this
            ).visitAndFinalize(this) {}
    }

@HtmlTagMarker
fun FlowContent.pfDivider(variant: DividerVariant = DividerVariant.HR, vararg classes: String) {
    when (variant) {
        DividerVariant.HR -> HR(attributesMapOf("class", "divider".component().append(*classes)), consumer)
        DividerVariant.DIV -> DIV(
            attributesMapOf("class", "divider".component().append(*classes), "role", "separator"),
            consumer
        )
        DividerVariant.LI -> LI(
            attributesMapOf("class", "divider".component().append(*classes), "role", "separator"),
            consumer
        )
    }
}

@HtmlTagMarker
fun FlowContent.pfVerticalDivider(vararg classes: String) {
    DIV(
        attributesMapOf(
            "class",
            buildString {
                append("divider".component())
                classes.joinTo(this, " ", " ")
            },
            "role",
            "separator"
        ), consumer
    )
}

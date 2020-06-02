package org.patternfly

import kotlinx.html.*

// ------------------------------------------------------ dsl

@HtmlTagMarker
fun <T, C : TagConsumer<T>> C.pfDivider(variant: DividerVariant = DividerVariant.HR, vararg classes: String): T =
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



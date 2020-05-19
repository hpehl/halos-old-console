package org.patternfly

import kotlinx.html.*
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

// ------------------------------------------------------ dsl functions

@HtmlTagMarker
fun FlowContent.pfAlert(
    severity: Severity,
    title: String,
    closable: Boolean = false,
    inline: Boolean = false,
    block: AlertTag.() -> Unit = {}
): Unit = AlertTag(severity, title, closable, inline, consumer).visit(block)

fun AlertTag.pfAlertDescription(block: DIV.() -> Unit = {}): Unit =
    DIV(attributesMapOf("class", "alert".component("description")), consumer).visit(block)

// ------------------------------------------------------ tags

class AlertTag(
    private val severity: Severity,
    private val title: String,
    private val closable: Boolean = false,
    private val inline: Boolean = false,
    consumer: TagConsumer<*>
) : DIV(
    attributesMapOf("class", buildString {
        append("alert".component())
        append(" ${severity.modifier}")
        if (inline) append(" inline".modifier())
    }),
    consumer
), PatternFlyTag, Aria, Ouia {

    var onClose: (() -> Unit)? = null
    override val componentType: ComponentType = ComponentType.Alert

    override fun head() {
        aria["label"] = severity.aria
        div("alert".component("icon")) {
            pfIcon(this@AlertTag.severity.iconClass)
        }
        h4("alert".component("title")) {
            span("pf-screen-reader") { +this@AlertTag.severity.aria }
            +this@AlertTag.title
        }
    }

    override fun tail() {
        if (closable) {
            div("alert".component("action")) {
                pfPlainButton(iconClass = "times".fas()) {
                    aria["label"] = "Close ${this@AlertTag.severity.aria.toLowerCase()}: ${this@AlertTag.title}"
                    onClickFunction = { evt ->
                        this@AlertTag.onClose?.invoke()
                        val button = evt.currentTarget as HTMLElement
                        val alert = button.closest("[data-pfc=${ComponentType.Alert.name}]")
                        alert?.pfComponent<AlertComponent>()?.close()
                    }
                }
            }
        }
    }
}

enum class Severity(
    val modifier: String,
    val iconClass: String,
    val aria: String
) {
    default("", "bell".fas(), "Default alert"),
    info("info".modifier(), "info-circle".fas(), "Info alert"),
    success("success".modifier(), "check-circle".fas(), "Success alert"),
    warning("warning".modifier(), "exclamation-triangle".fas(), "Warning alert"),
    danger("danger".modifier(), "exclamation-circle".fas(), "Danger alert");
}

// ------------------------------------------------------ components

class AlertComponent(element: HTMLDivElement) : PatternFlyComponent<HTMLDivElement>(element) {
    fun close() {
        element.parentElement?.removeChild(element)
    }
}

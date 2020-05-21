package org.patternfly

import kotlinx.html.*
import kotlinx.html.dom.create
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import org.jboss.elemento.removeFromParent
import org.patternfly.ComponentType.Alert
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.EventTarget
import kotlin.browser.document

// ------------------------------------------------------ dsl

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

// ------------------------------------------------------ tag

class AlertTag(
    private val severity: Severity,
    private val title: String,
    private val closable: Boolean = false,
    private val inline: Boolean = false,
    consumer: TagConsumer<*>
) :
    DIV(
        attributesMapOf("class", buildString {
            append("alert".component())
            append(" ${severity.modifier}")
            if (inline) append(" inline".modifier())
        }),
        consumer
    ), PatternFlyTag, Ouia {

    var onClose: (() -> Unit)? = null
    override val componentType: ComponentType = Alert

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
                    onClickFunction = {
                        this@AlertTag.onClose?.invoke()
                        it.target!!.pfAlert().close()
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

// ------------------------------------------------------ component

fun EventTarget.pfAlert(): AlertComponent = (this as Element).pfAlert()

fun Element.pfAlert(): AlertComponent =
    component(this, Alert, { document.create.div() }, { it as HTMLDivElement }, ::AlertComponent)

class AlertComponent(element: HTMLDivElement) : PatternFlyComponent<HTMLDivElement>(element) {
    fun close() {
        element.removeFromParent()
    }
}

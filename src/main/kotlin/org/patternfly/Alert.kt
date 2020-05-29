package org.patternfly

import kotlinx.html.*
import kotlinx.html.dom.append
import kotlinx.html.dom.create
import kotlinx.html.dom.prepend
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import org.jboss.elemento.Id
import org.jboss.elemento.removeFromParent
import org.patternfly.ComponentType.Alert
import org.patternfly.ComponentType.AlertGroup
import org.w3c.dom.*
import org.w3c.dom.events.EventTarget
import kotlin.browser.document
import kotlin.browser.window

// ------------------------------------------------------ api

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

// ------------------------------------------------------ dsl

@HtmlTagMarker
fun <T, C : TagConsumer<T>> C.pfToastAlertGroup(): T =
    AlertGroupTag(true, this).visitAndFinalize(this) {}

@HtmlTagMarker
fun FlowContent.pfAlertGroup(block: AlertGroupTag.() -> Unit = {}) = AlertGroupTag(false, consumer).visit(block)

@HtmlTagMarker
fun FlowContent.pfAlert(
    severity: Severity,
    text: String,
    closable: Boolean = false,
    inline: Boolean = false,
    block: AlertTag.() -> Unit = {}
) = AlertTag(severity, text, closable, inline, consumer).visit(block)

@HtmlTagMarker
fun AlertGroupTag.pfAlert(
    severity: Severity,
    text: String,
    closable: Boolean = false,
    inline: Boolean = false,
    block: AlertTag.() -> Unit = {}
) {
    li("alert-group".component("item")) {
        pfAlert(severity, text, closable, inline, block)
    }
}

@HtmlTagMarker
fun AlertTag.pfAlertDescription(block: DIV.() -> Unit = {}) =
    DIV(attributesMapOf("class", "alert".component("description")), consumer).visit(block)

// ------------------------------------------------------ tag

class AlertGroupTag(private val toast: Boolean, consumer: TagConsumer<*>) :
    UL(attributesMapOf("class", "alert-group".component()), consumer), PatternFlyTag, Ouia {

    override val componentType: ComponentType = AlertGroup

    override fun head() {
        if (toast) {
            classes += "toast".modifier()
        }
    }
}

class AlertTag(
    private val severity: Severity,
    private val text: String,
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
            +this@AlertTag.text
        }
    }

    override fun tail() {
        if (closable) {
            div("alert".component("action")) {
                pfPlainButton(iconClass = "times".fas()) {
                    aria["label"] = "Close ${this@AlertTag.severity.aria.toLowerCase()}: ${this@AlertTag.text}"
                    onClickFunction = {
                        this@AlertTag.onClose?.invoke()
                        it.target.pfAlert().close()
                    }
                }
            }
        }
    }
}

// ------------------------------------------------------ component

private val globalToastAlertGroup: AlertGroupComponent by lazy {
    val selector = ".${"toast".modifier()}${AlertGroup.selector()}"
    document.querySelector(selector).pfAlertGroup()
}

fun Document.pfToastAlertGroup(): AlertGroupComponent = globalToastAlertGroup

fun EventTarget?.pfAlertGroup(): AlertGroupComponent = (this as Element).pfAlertGroup()

fun Element?.pfAlertGroup(): AlertGroupComponent =
    component(
        this,
        AlertGroup,
        { document.create.ul() as HTMLUListElement },
        { it as HTMLUListElement },
        ::AlertGroupComponent
    )

class AlertGroupComponent(element: HTMLUListElement) : PatternFlyComponent<HTMLUListElement>(element) {
    private val toast: Boolean = element.matches(".${"toast".modifier()}")
    private val timeoutHandles: MutableMap<String, Int> = mutableMapOf()

    fun add(block: FlowContent.() -> Unit) {
        if (toast) {
            val id = Id.unique("alert")
            element.prepend {
                li("alert-group".component("item")) {
                    this.id = id
                    block(this)
                }
            }
            with(alertElement(id)) {
                onmouseover = { stopTimeout(id) }
                onmouseout = { startTimeout(id) }
            }
            startTimeout(id)
        } else {
            element.append {
                li("alert-group".component("item")) {
                    block(this)
                }
            }
        }
    }

    private fun startTimeout(id: String) {
        val handle = window.setTimeout({ alertElement(id).pfAlert().close() }, Settings.notificationTimeout)
        timeoutHandles[id] = handle
    }

    private fun stopTimeout(id: String) {
        timeoutHandles[id]?.let { window.clearTimeout(it) }
    }

    private fun alertElement(id: String) =
        element.querySelector("#$id > ${Alert.selector()}") as HTMLElement
}

fun EventTarget?.pfAlert(): AlertComponent = (this as Element).pfAlert()

fun Element?.pfAlert(): AlertComponent =
    component(this, Alert, { document.create.div() }, { it as HTMLDivElement }, ::AlertComponent)

class AlertComponent(element: HTMLDivElement) : PatternFlyComponent<HTMLDivElement>(element) {
    fun close() {
        if (element.parentElement?.matches(".${"alert-group".component("item")}") == true) {
            element.parentElement.removeFromParent()
        } else {
            element.removeFromParent()
        }
    }
}

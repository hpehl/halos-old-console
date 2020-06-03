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

// ------------------------------------------------------ dsl

/**
 * Creates the toast alert group. You should only create one toast alert group and append it to the body.
 * Use [Document.pfToastAlertGroup] to get the toast [AlertGroupComponent].
 */
@HtmlTagMarker
fun <T, C : TagConsumer<T>> C.pfToastAlertGroup(): T =
    AlertGroupTag(true, this).visitAndFinalize(this) {}

/**
 * Creates an alert group.
 * Use [EventTarget.pfAlertGroup] or [Element.pfAlertGroup] to get the related [AlertGroupComponent].
 */
@HtmlTagMarker
fun FlowContent.pfAlertGroup(block: AlertGroupTag.() -> Unit = {}) = AlertGroupTag(false, consumer).visit(block)

/**
 * Creates a standalone alert which is not part of an alert group.
 * Use [EventTarget.pfAlert] or [Element.pfAlert] to get the related [AlertComponent].
 */
@HtmlTagMarker
fun FlowContent.pfAlert(
    severity: Severity,
    text: String,
    closable: Boolean = false,
    inline: Boolean = false,
    block: AlertTag.() -> Unit = {}
) = AlertTag(severity, text, closable, inline, consumer).visit(block)

/**
 * Creates an alert which is part of the given group.
 * Use [EventTarget.pfAlert] or [Element.pfAlert] to get the related [AlertComponent].
 */
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

/** Creates an alert description */
@HtmlTagMarker
fun AlertTag.pfAlertDescription(block: DIV.() -> Unit = {}) =
    DIV(attributesMapOf("class", "alert".component("description")), consumer).visit(block)

// ------------------------------------------------------ tag

class AlertGroupTag internal constructor(private val toast: Boolean, consumer: TagConsumer<*>) :
    UL(attributesMapOf("class", "alert-group".component()), consumer), PatternFlyTag, Ouia {

    override val componentType: ComponentType = AlertGroup

    override fun head() {
        if (toast) {
            classes += "toast".modifier()
        }
    }
}

class AlertTag internal constructor(
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

    /** Callback when the alert has been closed. */
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
                        it.target.pfAlert().close()
                        this@AlertTag.onClose?.invoke()
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

/** Gets the global toast [AlertGroupComponent]. */
fun Document.pfToastAlertGroup(): AlertGroupComponent = globalToastAlertGroup

/** Gets the [AlertGroupComponent] for the given event target. */
fun EventTarget?.pfAlertGroup(): AlertGroupComponent = (this as Element).pfAlertGroup()

/** Gets the [AlertGroupComponent] for the given element. */
fun Element?.pfAlertGroup(): AlertGroupComponent =
    component(
        this,
        AlertGroup,
        { document.create.ul() as HTMLUListElement },
        { it as HTMLUListElement },
        ::AlertGroupComponent
    )

class AlertGroupComponent internal constructor(element: HTMLUListElement) :
    PatternFlyComponent<HTMLUListElement>(element) {

    private val toast: Boolean = element.matches(".${"toast".modifier()}")
    private val timeoutHandles: MutableMap<String, Int> = mutableMapOf()

    /**
     * Add the given HTML which should be a call to [FlowContent.pfAlert].
     * If this alert group is the toast alert group, this function manages the
     * timers to dismiss the alert.
     */
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

/** Gets the [AlertComponent] for the given event target. */
fun EventTarget?.pfAlert(): AlertComponent = (this as Element).pfAlert()

/** Gets the [AlertComponent] for the given element. */
fun Element?.pfAlert(): AlertComponent =
    component(this, Alert, { document.create.div() }, { it as HTMLDivElement }, ::AlertComponent)

class AlertComponent internal constructor(element: HTMLDivElement) : PatternFlyComponent<HTMLDivElement>(element) {

    /** Closes this alert component. */
    fun close() {
        if (element.parentElement?.matches(".${"alert-group".component("item")}") == true) {
            element.parentElement.removeFromParent()
        } else {
            element.removeFromParent()
        }
    }
}

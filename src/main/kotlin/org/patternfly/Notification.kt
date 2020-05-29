package org.patternfly

import kotlinx.html.*
import kotlinx.html.dom.create
import kotlinx.html.js.div
import org.jboss.elemento.aria
import org.patternfly.ComponentType.NotificationBadge
import org.patternfly.ComponentType.NotificationDrawer
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.EventTarget
import kotlin.browser.document

// ------------------------------------------------------ api

object Notification {

    fun error(text: String, details: String? = null) = alert(Severity.danger, text, details)
    fun info(text: String, details: String? = null) = alert(Severity.info, text, details)
    fun success(text: String, details: String? = null) = alert(Severity.success, text, details)
    fun warning(text: String, details: String? = null) = alert(Severity.warning, text, details)

    internal fun alert(severity: Severity, text: String, details: String? = null) {
        val notificationDrawer = document.pfNotificationDrawer()
        // TODO notificationDrawer.add {}
        if (!notificationDrawer.isOpen()) {
            document.pfToastAlertGroup().add {
                pfAlert(severity, text, true) {
                    details?.let { pfAlertDescription { +it } }
                }
            }
        }
        document.pfNotificationBadge().markUnread()
    }
}

// ------------------------------------------------------ dsl

@HtmlTagMarker
fun FlowContent.pfNotificationBadge(block: NotificationBadgeTag.() -> Unit = {}) =
    NotificationBadgeTag(consumer).visit(block)

@HtmlTagMarker
fun <T, C : TagConsumer<T>> C.pfNotificationDrawer(block: NotificationDrawerTag.() -> Unit = {}): T =
    NotificationDrawerTag(this).visitAndFinalize(this, block)

// ------------------------------------------------------ tag

class NotificationBadgeTag(consumer: TagConsumer<*>) :
    BUTTON(attributesMapOf("class", "${"button".component()} ${"plain".modifier()}"), consumer),
    PatternFlyTag, Ouia {

    override val componentType: ComponentType = NotificationBadge

    override fun head() {
        aria["label"] = "Notifications"
        span("${"notification-badge".component()} ${"read".modifier()}") {
            pfIcon("bell".fas())
        }
    }
}

class NotificationDrawerTag(consumer: TagConsumer<*>) :
    DIV(attributesMapOf("class", "notification-drawer".component()), consumer), PatternFlyTag, Ouia {

    override val componentType: ComponentType = NotificationDrawer

    override fun head() {
        div("notification-drawer".component("header")) {
            h1("notification-drawer".component("header", "title")) {
                +"Notifications"
            }
            span("notification-drawer".component("header", "status"))
        }
    }
}

// ------------------------------------------------------ component

private val globalNotificationBadge: NotificationBadgeComponent by lazy {
    val selector = ".${"page".component("header")} ${NotificationBadge.selector()}"
    document.querySelector(selector).pfNotificationBadge()
}

fun Document.pfNotificationBadge(): NotificationBadgeComponent = globalNotificationBadge

fun EventTarget?.pfNotificationBadge(): NotificationBadgeComponent = (this as Element).pfNotificationBadge()

fun Element?.pfNotificationBadge(): NotificationBadgeComponent =
    component(
        this,
        NotificationBadge,
        { document.create.button() as HTMLButtonElement },
        { it as HTMLButtonElement },
        ::NotificationBadgeComponent
    )

class NotificationBadgeComponent(element: HTMLButtonElement) : PatternFlyComponent<HTMLButtonElement>(element) {

    fun markRead() {
        element.aria["label"] = "Notifications"
        element.querySelector(".${"notification-badge".component()}")?.let {
            it.classList.add("read".modifier())
            it.classList.remove("unread".modifier())
        }
    }

    fun markUnread() {
        element.aria["label"] = "Unread Notifications"
        element.querySelector(".${"notification-badge".component()}")?.let {
            it.classList.add("unread".modifier())
            it.classList.remove("read".modifier())
        }
    }
}

private val globalNavigationDrawer: NotificationDrawerComponent by lazy {
    val selector = NotificationDrawer.selector()
    val element = document.querySelector(selector)
    element.pfNotificationDrawer()
}

fun Document.pfNotificationDrawer(): NotificationDrawerComponent = globalNavigationDrawer

fun EventTarget?.pfNotificationDrawer(): NotificationDrawerComponent = (this as Element).pfNotificationDrawer()

fun Element?.pfNotificationDrawer(): NotificationDrawerComponent =
    component(
        this,
        NotificationDrawer,
        { document.create.div() },
        { it as HTMLDivElement },
        ::NotificationDrawerComponent
    )

class NotificationDrawerComponent(element: HTMLDivElement) : PatternFlyComponent<HTMLDivElement>(element) {
    fun isOpen(): Boolean = false
}

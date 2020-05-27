package org.jboss.mvp

import org.w3c.dom.HTMLElement
import org.w3c.dom.PopStateEvent
import org.w3c.dom.Window
import kotlin.browser.document
import kotlin.browser.window
import kotlin.dom.clear

fun String.placeRequest(): PlaceRequest {
    val token = substringAfter("#").substringBefore(";")
    val paramList = substringAfter(";")
    val params = if (paramList != this) {
        paramList.split(";").associate {
            val (key, value) = it.split("=")
            key to value
        }
    } else {
        emptyMap()
    }
    return PlaceRequest(token, params)
}

data class PlaceRequest(val token: String, val params: Map<String, String> = mapOf()) {

    val empty: Boolean = token.isEmpty()

    override fun toString(): String = buildString {
        append("#$token")
        if (params.isNotEmpty()) {
            params.map { (key, value) -> "$key=$value" }.joinTo(this, ";", ";")
        }
    }
}

class PlaceManager(selector: String, private val defaultPlace: PlaceRequest) {

    private var current: Presenter<*>? = null
    private var element: HTMLElement? = document.querySelector(selector) as HTMLElement

    init {
        window.addEventListener("popstate", {
            val event = it as PopStateEvent
            val place = if (event.state != null) {
                event.state as PlaceRequest
            } else {
                (event.target as Window).location.hash.placeRequest()
            }
            navigate(place)
        })
    }

    fun gotoCurrent() = goto(window.location.hash.placeRequest())

    fun goto(place: PlaceRequest) {
        val url = window.location.pathname + place.toString()
        window.location.href = url
    }

    private fun navigate(place: PlaceRequest) {
        val safePlace = if (place.empty) defaultPlace else place
        val presenter = Presenter.lookup<Presenter<View>>(safePlace.token)
        if (presenter != null) {
            if (presenter !== current) {
                current?.hide()
            }
            presenter.prepareFromRequest(place)
            element?.let {
                it.clear()
                it.append(*presenter.view.elements)
            }
            presenter.show()
            current = presenter
        } else {
            if (!defaultPlace.empty) {
                console.warn("Unable to navigate to $place. Presenter for ${place.token} not found. Going to default place.")
                goto(defaultPlace)
            } else {
                console.error("Unable to navigate to $place. Presenter for ${place.token} not found and no default place defined!")
            }
        }
    }
}
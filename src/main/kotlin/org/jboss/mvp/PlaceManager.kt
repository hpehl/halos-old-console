package org.jboss.mvp

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
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

// Will break the JSON (de)serialization if implemented as data class property
val PlaceRequest.url: String
    get() = window.location.pathname + toString()

@Suppress("EXPERIMENTAL_API_USAGE")
private var json: Json = Json { prettyPrint = false }

@Suppress("UnsafeCastFromDynamic")
fun PlaceRequest.toJson(): Any? = json.stringify(PlaceRequest.serializer(), this).asDynamic()

@Serializable
data class PlaceRequest(val token: String, val params: Map<String, String> = mapOf()) {

    override fun toString(): String = buildString {
        append("#$token")
        if (params.isNotEmpty()) {
            params.map { (key, value) -> "$key=$value" }.joinTo(this, ";", ";")
        }
    }

    companion object {
        fun fromEvent(event: PopStateEvent) = if (event.state != null) {
            fromJson(event.state)
        } else {
            (event.target as Window).location.hash.placeRequest()
        }

        fun fromJson(data: Any?): PlaceRequest = json.parse(serializer(), data as String)
    }
}

class PlaceManager(selector: String, private val defaultPlace: PlaceRequest) {

    private var currentPresenter: Presenter<*>? = null
    private var element: HTMLElement? = document.querySelector(selector) as HTMLElement
    private val navigationHandler: MutableList<(PlaceRequest) -> Unit> = mutableListOf()
    private var internalPlace: PlaceRequest? = null
    val currentPlace: PlaceRequest
        get() = internalPlace ?: defaultPlace

    init {
        window.addEventListener("popstate", {
            navigate(PlaceRequest.fromEvent(it as PopStateEvent))
        })
    }

    fun onNavigate(handler: (PlaceRequest) -> Unit) {
        navigationHandler.add(handler)
    }

    fun gotoCurrent() {
        internalPlace = navigate(window.location.hash.placeRequest())
        internalPlace?.let {
            window.history.replaceState(it.toJson(), "", it.url)
        }
    }

    fun goto(place: PlaceRequest) {
        internalPlace = navigate(place)
        internalPlace?.let {
            window.history.pushState(it.toJson(), "", it.url)
        }
    }

    private fun navigate(place: PlaceRequest): PlaceRequest? {
        val nonEmptyPlace = if (place.token.isEmpty()) defaultPlace else place
        val safePlace = if (nonEmptyPlace.token in Presenter) nonEmptyPlace else defaultPlace
        val presenter = Presenter.lookup<Presenter<View>>(safePlace.token)
        if (presenter != null) {
            if (presenter !== currentPresenter) {
                currentPresenter?.hide()
            }
            presenter.prepareFromRequest(place)
            element?.let {
                it.clear()
                it.append(*presenter.view.elements)
            }
            presenter.show()
            currentPresenter = presenter
            navigationHandler.forEach { it(safePlace) }
            return safePlace
        } else {
            console.error("No presenter found for $safePlace!")
            return null
        }
    }
}

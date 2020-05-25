package org.wildfly.halos.mvp

import kotlinx.html.TagConsumer
import org.w3c.dom.HTMLElement

interface Presenter<V : View> {
    val token: String
    val view: V

    fun prepareFromRequest(place: PlaceRequest) {}
    fun show() {}
    fun hide() {}

    companion object {
        private val registry: MutableMap<String, Presenter<*>> = mutableMapOf()

        fun register(presenter: Presenter<*>) {
            registry[presenter.token] = presenter
        }

        fun lookup(token: String): Presenter<*>? = registry[token]
    }
}

interface View {
    val elements: TagConsumer<HTMLElement>.() -> Unit
}

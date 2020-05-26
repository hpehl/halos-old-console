package org.wildfly.halos.mvp

import kotlinx.html.TagConsumer
import org.w3c.dom.HTMLElement
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

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

interface HasPresenter<P : Presenter<V>, V : View> {
    val presenter: P
}

fun <P : Presenter<V>, V : View> bind(token: String): BindPresenter<P, V> = BindPresenter(token)

class BindPresenter<P : Presenter<V>, V : View>(private val token: String) : ReadOnlyProperty<V, P> {
    private var presenter: P? = null

    override fun getValue(thisRef: V, property: KProperty<*>): P {
        if (presenter == null) {
            presenter = Presenter.lookup(token).unsafeCast<P>()
        }
        return presenter!!
    }
}

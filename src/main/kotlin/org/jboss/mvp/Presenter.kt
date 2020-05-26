package org.jboss.mvp

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
        private val registry: MutableMap<String, () -> Presenter<*>> = mutableMapOf()
        private val instances: MutableMap<String, Presenter<*>> = mutableMapOf()

        fun register(token: String, presenter: () -> Presenter<*>) {
            registry[token] = presenter
        }

        fun lookup(token: String): Presenter<*>? {
            return if (token in instances) {
                instances[token]
            } else {
                if (token in registry) {
                    registry[token]?.invoke()?.let {
                        instances[token] = it
                        it
                    }
                } else {
                    null
                }
            }
        }
    }
}

interface View {
    val elements: Array<HTMLElement>
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
            if (presenter == null) {
                console.error("Unable to bind presenter to view: No presenter registered for $token")
            }
        }
        return presenter!!
    }
}

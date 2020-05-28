package org.jboss.mvp

import org.patternfly.PatternFlyComponent
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

interface Presenter<V : View> {
    val token: String
    val view: V

    /** Called once the presenter is created. */
    fun bind() {}

    /** Called before the presenter is shown. */
    fun prepareFromRequest(place: PlaceRequest) {}

    /** Called after the view has been attached to the DOM. */
    fun show() {}

    /** Called before the view has been removed from the DOM. */
    fun hide() {}

    companion object {
        private val registry: MutableMap<String, () -> Presenter<out View>> = mutableMapOf()
        private val instances: MutableMap<String, Presenter<out View>> = mutableMapOf()

        fun register(token: String, presenter: () -> Presenter<out View>) {
            registry[token] = presenter
        }

        @Suppress("UNCHECKED_CAST")
        fun <P : Presenter<out View>> lookup(token: String): P? {
            return if (token in instances) {
                instances[token] as P
            } else {
                if (token in registry) {
                    registry[token]?.invoke()?.let {
                        instances[token] = it
                        it.bind()
                        it as P
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

interface HasPresenter<P : Presenter<out View>> {
    val presenter: P
}

fun <P : Presenter<out View>> token(token: String): PresenterToken<P> = PresenterToken(token)

class PresenterToken<P : Presenter<out View>>(private val token: String) : ReadOnlyProperty<View, P> {
    private var presenter: P? = null

    override fun getValue(thisRef: View, property: KProperty<*>): P {
        if (presenter == null) {
            presenter = Presenter.lookup<P>(token)
            if (presenter == null) {
                console.error("Unable to bind presenter to view: No presenter registered for $token")
            }
        }
        return presenter!!
    }
}

fun <V : View, T : PatternFlyComponent<HTMLElement>> component(
    selector: String,
    lookup: (Element?) -> T
): ViewComponent<V, T> = ViewComponent(selector, lookup)

class ViewComponent<V : View, T : PatternFlyComponent<HTMLElement>>(
    private val selector: String,
    private val lookup: (Element?) -> T
) : ReadOnlyProperty<V, T> {
    private var element: Element? = null

    override fun getValue(thisRef: V, property: KProperty<*>): T {
        if (element == null) {
            element = thisRef.elements.map { it.querySelector(selector) }.first { it != null }
            if (element == null) {
                console.error("Unable to find view component for $selector")
            }
        }
        return lookup(element)
    }
}
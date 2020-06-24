package org.patternfly

import dev.fritz2.binding.SingleMountPoint
import dev.fritz2.dom.Tag
import dev.fritz2.dom.WithText
import dev.fritz2.dom.html.Dl
import kotlinx.coroutines.flow.Flow
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement

// ------------------------------------------------------ dsl

fun Dl.dt(baseClass: String? = null, id: String? = null, content: Dt.() -> Unit): Dt =
    register(Dt(id, baseClass), content)

fun Dl.dd(baseClass: String? = null, id: String? = null, content: Dd.() -> Unit): Dd =
    register(Dd(id, baseClass), content)

// ------------------------------------------------------ tag

class Dt(id: String? = null, baseClass: String? = null) : Tag<HTMLElement>("dt", id, baseClass),
    WithText<HTMLElement>

class Dd(id: String? = null, baseClass: String? = null) : Tag<HTMLElement>("dd", id, baseClass),
    WithText<HTMLElement>

// ------------------------------------------------------ helpers

internal fun Flow<Boolean>.bindAttr(name: String, target: Element?) =
    AttributeMountPoint(name, this, target)

internal class AttributeMountPoint(private val name: String, upstream: Flow<Boolean>, private val target: Element?) :
    SingleMountPoint<Boolean>(upstream) {
    override fun set(value: Boolean, last: Boolean?) {
        target?.setAttribute(name, value.toString())
    }
}

package org.wildfly.halos.model

import kotlinx.html.TagConsumer
import kotlinx.html.classes
import kotlinx.html.h1
import kotlinx.html.p
import org.patternfly.modifier
import org.patternfly.pfContent
import org.patternfly.pfSection
import org.w3c.dom.HTMLElement
import org.wildfly.halos.mvp.Presenter
import org.wildfly.halos.mvp.View

class ManagementModelPresenter : Presenter<ManagementModelView> {
    override val token = "mm"
    override val view = ManagementModelView()
}

class ManagementModelView : View {
    override val elements: TagConsumer<HTMLElement>.() -> Unit = {
        pfSection("light".modifier()) {
            pfContent {
                h1 {
                    classes += "pf-c-title"
                    +"Management Model"
                }
                p { +"Not yet implemented" }
            }
        }
    }
}
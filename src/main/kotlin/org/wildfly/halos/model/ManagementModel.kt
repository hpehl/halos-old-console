package org.wildfly.halos.model

import kotlinx.html.classes
import kotlinx.html.dom.create
import kotlinx.html.h1
import kotlinx.html.p
import org.jboss.mvp.Presenter
import org.jboss.mvp.View
import org.patternfly.modifier
import org.patternfly.pfContent
import org.patternfly.pfSection
import kotlin.browser.document

class ManagementModelPresenter : Presenter<ManagementModelView> {
    override val token = "mm"
    override val view = ManagementModelView()

    companion object {
        const val TOKEN = "mm"
    }
}

class ManagementModelView : View {
    override val elements = with(document.create) {
        arrayOf(
            pfSection("light".modifier()) {
                pfContent {
                    h1 {
                        classes += "pf-c-title"
                        +"Management Model"
                    }
                    p { +"Not yet implemented" }
                }
            }
        )
    }
}
package org.wildfly.halos.model

import org.jboss.mvp.Presenter
import org.jboss.mvp.View
import org.jboss.mvp.renderAll
import org.patternfly.modifier
import org.patternfly.pfContent
import org.patternfly.pfSection
import org.wildfly.halos.Places

class ManagementModelPresenter : Presenter<ManagementModelView> {
    override val token = Places.management
    override val view = ManagementModelView()
}

class ManagementModelView : View {
    override val elements = renderAll({
        pfSection("light".modifier()) {
            pfContent {
                h1 { text("Management Model") }
                p { text("Not yet implemented") }
            }
        }
    })
}

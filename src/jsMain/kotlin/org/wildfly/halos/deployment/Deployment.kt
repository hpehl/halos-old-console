package org.wildfly.halos.deployment

import org.jboss.mvp.Presenter
import org.jboss.mvp.View
import org.jboss.mvp.renderAll
import org.patternfly.modifier
import org.patternfly.pfContent
import org.patternfly.pfSection
import org.wildfly.halos.Places

class DeploymentPresenter : Presenter<DeploymentView> {
    override val token = Places.DEPLOYMENT
    override val view = DeploymentView()
}

class DeploymentView : View {
    override val elements = renderAll({
        pfSection("light".modifier()) {
            pfContent {
                h1 { text("Deployment") }
                p { text("Not yet implemented") }
            }
        }
    })
}

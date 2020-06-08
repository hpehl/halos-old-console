@file:JsModule("@patternfly/react-charts")
@file:JsNonModule

package org.patternfly.charts

import react.RClass
import react.RProps

@JsName("ChartDonut")
external val pfcDonut: RClass<DonutProps>

external interface DonutProps : RProps {
    var ariaDesc: String
    var ariaTitle: String
    var constrainToVisibleArea: Boolean
    var data: dynamic
    var subTitle: String
    var title: String
}

package org.patternfly

@Suppress("SpellCheckingInspection")
internal enum class Dataset(val long: String, val short: String) {
    COMPONENT_TYPE("data-pfc", "pfc"),
    DROPDOWN_ITEM("data-pfi", "pfi"),
    NAVIGATION_ITEM("data-pfn", "pfn"),
    OUIA_COMPONENT_TYPE("data-ouia-component-type", "ouiaComponentType"),
    REGISTRY("data-pfr", "pfr")
}

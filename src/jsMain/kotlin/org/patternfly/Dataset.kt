package org.patternfly

@Suppress("SpellCheckingInspection")
internal enum class Dataset(val long: String, val short: String) {
    COMPONENT_TYPE("data-pfct", "pfct"),
    DATA_LIST_ITEM("data-pfdli", "pfdli"),
    DROPDOWN_ITEM("data-pfddi", "pfddi"),
    NAVIGATION_ITEM("data-pfni", "pfni"),
    OUIA_COMPONENT_TYPE("data-ouia-component-type", "ouiaComponentType"),
}

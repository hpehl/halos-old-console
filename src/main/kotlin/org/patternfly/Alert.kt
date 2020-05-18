package org.patternfly

import kotlinx.html.DIV
import kotlinx.html.TagConsumer
import kotlinx.html.attributesMapOf

class PfAlert(private val severity: Severity, consumer: TagConsumer<*>) :
    DIV(
        attributesMapOf("class", "page".component("header", "brand")),
        consumer
    ), Aria, Ouia

enum class Severity(
    private val modifier: String,
    private val icon: String,
    private val aria: String
) {
    default("", "bell".fas(), "Default alert"),
    info("info".modifier(), "info-circle".fas(), "Info alert"),
    success("success".modifier(), "check-circle".fas(), "Success alert"),
    warning("warning".modifier(), "exclamation-triangle".fas(), "Warning alert"),
    danger("danger".modifier(), "exclamation-circle".fas(), "Danger alert");
}

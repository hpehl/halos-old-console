package org.patternfly

typealias Identifier<T> = (T) -> String

typealias AsString<T> = (T) -> String

typealias SelectHandler<T> = (T) -> Unit

typealias Filter<T> = (T) -> Boolean

enum class ComponentType(val id: String) {
    Alert("at"),
    AlertGroup("ag"),
    Button("btn"),
    Content("cnt"),
    DataList("dl"),
    Drawer("dw"),
    Dropdown("dd"),
    EmptyState("es"),
    Icon("i"),
    Navigation("nav"),
    NotificationBadge("nb"),
    NotificationDrawer("nd"),
    Page("p"),
    PageHeader("ph"),
    PageMain("pm"),
    Sidebar("sb");
}

enum class Direction {
    RIGHT, UP
}

enum class DividerVariant {
    HR, DIV, LI
}

enum class Orientation {
    HORIZONTAL, VERTICAL
}

enum class SelectionMode {
    NONE, SINGLE, MULTIPLE
}

enum class Severity(
    val modifier: String,
    val iconClass: String,
    val aria: String
) {
    DEFAULT("", "bell".fas(), "Default alert"),
    INFO("info".modifier(), "info-circle".fas(), "Info alert"),
    SUCCESS("success".modifier(), "check-circle".fas(), "Success alert"),
    WARNING("warning".modifier(), "exclamation-triangle".fas(), "Warning alert"),
    DANGER("danger".modifier(), "exclamation-circle".fas(), "Danger alert");
}

enum class Size(val modifier: String) {
    _4xl("4xl".modifier()),
    _3xl("3xl".modifier()),
    _2xl("2xl".modifier()),
    xl("xl".modifier()),
    lg("lg".modifier()),
    md("md".modifier())
}

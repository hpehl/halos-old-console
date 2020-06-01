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

enum class Orientation {
    HORIZONTAL, VERTICAL
}

enum class Size(val modifier: String) {
    _4xl("4xl".modifier()),
    _3xl("3xl".modifier()),
    _2xl("2xl".modifier()),
    xl("xl".modifier()),
    lg("lg".modifier()),
    md("md".modifier())
}

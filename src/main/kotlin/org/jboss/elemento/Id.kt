package org.jboss.elemento

import kotlin.browser.document

object Id {

    private const val UNIQUE_ID = "id-"
    private var counter: Int = 0

    /** Creates an identifier guaranteed to be unique within this document. */
    fun unique(): String {
        var id: String
        do {
            id = "$UNIQUE_ID$counter"
            counter = if (counter == Int.MAX_VALUE) {
                0
            } else {
                counter + 1
            }
        } while (document.getElementById(id) != null)
        return id
    }

    /** Creates an identifier guaranteed to be unique within this document. The unique part comes last. */
    fun unique(id: String, vararg additionalIds: String): String =
        build(id, *additionalIds) + "-" + unique()

    fun build(id: String, vararg additionalIds: String): String {
        val segments = listOf(id, *additionalIds)
        return segments.joinToString("-") { asId(it) }
    }

    fun asId(text: String): String {
        return text.split("[-\\s]").asSequence()
            .map { it.replace("\\s+".toRegex(), "") }
            .map { it.replace("[^a-zA-Z0-9-_]".toRegex(), "") }
            .map { it.replace('_', '-') }
            .map { it.toLowerCase() }
            .filter { it.isNotEmpty() }
            .joinToString()
//        val parts = text.split("[-\\s]").toTypedArray()
//        val sanitized = mutableListOf<String>()
//        for (part in parts) {
//            var s = part.replace("\\s+".toRegex(), "")
//            s = s.replace("[^a-zA-Z0-9-_]".toRegex(), "")
//            s = s.replace('_', '-')
//            if (s.isNotEmpty()) sanitized.add(s)
//        }
//        return if (sanitized.isEmpty()) {
//            ""
//        } else {
//            sanitized.filter(String::isNotEmpty).joinToString(transform = String::toLowerCase)
//        }
    }
}
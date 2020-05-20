package org.patternfly

import kotlin.browser.document

object Id {

    private const val UNIQUE_ID = "id-"
    private var counter = 0

    /** Creates an identifier guaranteed to be unique within this document. */
    fun unique(): String {
        var id: String
        do {
            id = "$UNIQUE_ID$counter"
            counter++
        } while (document.getElementById(id) != null)
        return id
    }

    /** Creates an identifier guaranteed to be unique within this document. The unique part comes last.  */
    fun unique(id: String, vararg additionalIds: String): String =
        build(id, *additionalIds) + "-" + unique()

    fun build(id: String, vararg additionalIds: String): String {
        val segments = listOf(id, *additionalIds)
        return segments.joinToString("-") { asId(it) }
    }

    fun asId(text: String): String {
        val parts = text.split("[-\\s]").toTypedArray()
        val sanitized = mutableListOf<String>()
        for (part in parts) {
            var s = part.replace("\\s+".toRegex(), "")
            s = s.replace("[^a-zA-Z0-9-_]".toRegex(), "")
            s = s.replace('_', '-')
            if (s.isNotEmpty()) sanitized.add(s)
        }
        return if (sanitized.isEmpty()) {
            ""
        } else {
            sanitized.filter(String::isNotEmpty).joinToString(transform = String::toLowerCase)
        }
    }
}
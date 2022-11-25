package org.wildfly.halos.meta

fun interface SegmentResolver {
    fun resolve(segment: Segment, index: Int, first: Boolean, last: Boolean): Segment

    fun andThen(resolver: SegmentResolver): SegmentResolver =
        SegmentResolver { segment, index, first, last ->
            val resolved = resolve(segment, index, first, last)
            resolver.resolve(resolved, index, first, last)
        }
}

val STATEMENT_CONTEXT_RESOLVER: SegmentResolver = SegmentResolver { segment, _, _, _ ->
    StatementContext.resolve(segment)
}

val WILDCARD_LAST_RESOLVER: SegmentResolver =
    STATEMENT_CONTEXT_RESOLVER.andThen { segment, _, _, last ->
        if (last && segment is KeyValueSegment && segment.value != "*") {
            KeyValueSegment(segment.key, "*")
        } else {
            segment
        }
    }

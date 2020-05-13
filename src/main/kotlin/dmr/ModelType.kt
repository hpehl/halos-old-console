package org.wildfly.halos.dmr

enum class ModelType(val typeChar: Char) {
    BIG_DECIMAL('d'),
    BIG_INTEGER('i'),
    BOOLEAN('Z'),
    BYTES('b'),
    DOUBLE('D'),
    EXPRESSION('e'),
    INT('I'),
    LIST('l'),
    LONG('J'),
    OBJECT('o'),
    PROPERTY('p'),
    STRING('s'),
    TYPE('t'),
    UNDEFINED('u');

    companion object {
        fun forChar(c: Char): ModelType {
            return when (c) {
                'J' -> LONG
                'I' -> INT
                'Z' -> BOOLEAN
                's' -> STRING
                'D' -> DOUBLE
                'd' -> BIG_DECIMAL
                'i' -> BIG_INTEGER
                'b' -> BYTES
                'l' -> LIST
                't' -> TYPE
                'o' -> OBJECT
                'p' -> PROPERTY
                'e' -> EXPRESSION
                'u' -> UNDEFINED
                else -> throw IllegalArgumentException("Invalid type character '$c'")
            }
        }
    }
}
package org.wildfly.dmr

@Suppress("UNUSED_PARAMETER")
internal fun stringToBytes(s: String): ByteArray = js(
    "var b = [];for (var i = 0; i < s.length; i++) {b.push(s.charCodeAt(i));}return b;"
) as ByteArray

@Suppress("UNUSED_PARAMETER")
internal fun byteArrayToString(bytes: ByteArray): String = js(
    "var s='';var a=new Uint8Array(bytes);var l=a.byteLength;for(var i=0;i<l;i++){s+=String.fromCharCode(a[i]);}return s;"
) as String

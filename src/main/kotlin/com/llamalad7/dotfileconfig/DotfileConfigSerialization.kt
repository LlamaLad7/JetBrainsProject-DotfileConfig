package com.llamalad7.dotfileconfig

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.intellij.openapi.actionSystem.KeyboardShortcut
import javax.swing.KeyStroke

class ShortcutSerializer : JsonSerializer<Shortcut>() {
    override fun serialize(value: Shortcut, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(value.impl.toString())
    }
}

class ShortcutDeserializer : JsonDeserializer<Shortcut>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Shortcut {
        val str = p.valueAsString
        if ('+' in str) {
            // 2-stroke
            val (first, second) = str.split('+').map { it.readKeystroke() }
            return Shortcut(KeyboardShortcut(first, second))
        }
        return Shortcut(KeyboardShortcut(str.readKeystroke(), null))
    }

    private fun String.readKeystroke() = KeyStroke.getKeyStroke(removeSurrounding("[", "]"))
}
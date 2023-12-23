package com.llamalad7.dotfileconfig

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.intellij.openapi.actionSystem.KeyboardShortcut
import com.intellij.openapi.keymap.Keymap

data class DotfileConfig(
    val keybinds: KeybindConfig,
) {
    fun apply(
        keymap: Keymap,
    ) {
        keybinds.apply(keymap)
    }
}

data class KeybindConfig(val bindings: Map<String, List<Shortcut>>) {
    constructor(keymap: Keymap) : this(
        keymap.actionIdList
            .associateWith { action ->
                keymap.getShortcuts(action)
                    .filterIsInstance<KeyboardShortcut>()
                    .map(::Shortcut)
            }
    )

    fun apply(keymap: Keymap) {
        for ((action, shortcuts) in bindings) {
            val existing = keymap.getShortcuts(action)
            keymap.removeAllActionShortcuts(action)
            try {
                shortcuts.forEach {
                    keymap.addShortcut(action, it.impl)
                }
            } catch (e: Exception) {
                existing.forEach {
                    keymap.addShortcut(action, it)
                }
                throw e
            }
        }
    }
}

@JsonSerialize(using = ShortcutSerializer::class)
@JsonDeserialize(using = ShortcutDeserializer::class)
data class Shortcut(val impl: KeyboardShortcut)
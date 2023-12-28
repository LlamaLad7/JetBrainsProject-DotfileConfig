package com.llamalad7.dotfileconfig

import com.intellij.openapi.actionSystem.KeyboardShortcut
import com.intellij.openapi.keymap.Keymap
import com.intellij.openapi.keymap.KeymapManager
import com.intellij.testFramework.HeavyPlatformTestCase
import javax.swing.KeyStroke

class KeybindTests : HeavyPlatformTestCase() {
    private lateinit var keymap: Keymap
    private val currentGotoClassShortcuts get() = keymap.getShortcuts("GotoClass").asList()

    private val generatedGotoClassShortcuts
        get() =
            DotfileConfigPlugin.generateConfig().keybinds?.bindings?.get("GotoClass")
                ?: error("Could not get GotoClass bindings")

    override fun setUp() {
        super.setUp()
        keymap = KeymapManager.getInstance().activeKeymap
    }

    fun `test Loading Keybinds`() {
        loadConfig("keybinds/gotoClassCtrlShiftF10")
        assertEquals(
            currentGotoClassShortcuts,
            listOf(
                KeyboardShortcut(
                    KeyStroke.getKeyStroke("ctrl shift pressed F10"),
                    null
                )
            )
        )
        loadConfig("keybinds/gotoClassCtrlShiftF11")
        assertEquals(
            currentGotoClassShortcuts,
            listOf(
                KeyboardShortcut(
                    KeyStroke.getKeyStroke("ctrl shift pressed F11"),
                    null
                )
            )
        )
    }

    fun `test Loading Two-Stroke Keybinds`() {
        loadConfig("keybinds/gotoClassCtrlShiftF10+F11")
        assertEquals(
            currentGotoClassShortcuts,
            listOf(
                KeyboardShortcut(
                    KeyStroke.getKeyStroke("ctrl shift pressed F10"),
                    KeyStroke.getKeyStroke("pressed F11")
                )
            )
        )
        loadConfig("keybinds/gotoClassCtrlShiftF11+F12")
        assertEquals(
            currentGotoClassShortcuts,
            listOf(
                KeyboardShortcut(
                    KeyStroke.getKeyStroke("ctrl shift pressed F11"),
                    KeyStroke.getKeyStroke("pressed F12")
                )
            )
        )
    }

    fun `test Keybind Config Generation`() {
        val ctrlShiftF10 = KeyboardShortcut(
            KeyStroke.getKeyStroke("ctrl shift pressed F10"),
            null
        )
        val ctrlShiftF11PlusF12 = KeyboardShortcut(
            KeyStroke.getKeyStroke("ctrl shift pressed F11"),
            KeyStroke.getKeyStroke("pressed F12")
        )

        setGotoClassShortcuts(listOf(ctrlShiftF10))
        assertEquals(
            generatedGotoClassShortcuts,
            listOf(
                Shortcut(
                    ctrlShiftF10
                )
            )
        )
        setGotoClassShortcuts(listOf(ctrlShiftF11PlusF12))
        assertEquals(
            generatedGotoClassShortcuts,
            listOf(
                Shortcut(
                    ctrlShiftF11PlusF12
                )
            )
        )
    }

    private fun setGotoClassShortcuts(shortcuts: List<KeyboardShortcut>) {
        keymap.removeAllActionShortcuts("GotoClass")
        shortcuts.forEach {
            keymap.addShortcut("GotoClass", it)
        }
    }
}
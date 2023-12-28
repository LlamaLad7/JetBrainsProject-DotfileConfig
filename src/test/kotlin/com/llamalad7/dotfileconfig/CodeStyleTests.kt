package com.llamalad7.dotfileconfig

import com.intellij.json.formatter.JsonCodeStyleSettings
import com.intellij.testFramework.HeavyPlatformTestCase

class CodeStyleTests : HeavyPlatformTestCase() {
    private lateinit var jsonStyle: JsonCodeStyleSettings
    private val currentJsonSettings get() =
        DotfileConfigPlugin.generateConfig().codeStyle?.settings?.get("JSON") ?: error("Could not get JSON settings!")

    override fun setUp() {
        super.setUp()
        jsonStyle = getCustomStyle()
    }

    fun `test JSON Boolean Setting`() {
        loadConfig("json/noSpaceAfterColon")
        assertFalse(jsonStyle.SPACE_AFTER_COLON)
        loadConfig("json/spaceAfterColon")
        assertTrue(jsonStyle.SPACE_AFTER_COLON)
    }

    fun `test JSON Number Setting`() {
        loadConfig("json/propertyAlignment2")
        assertEquals(jsonStyle.PROPERTY_ALIGNMENT, 2)
        loadConfig("json/propertyAlignment0")
        assertEquals(jsonStyle.PROPERTY_ALIGNMENT, 0)
    }

    fun `test JSON Config Generation`() {
        jsonStyle.KEEP_TRAILING_COMMA = true
        assertEquals(currentJsonSettings["KEEP_TRAILING_COMMA"], true)
        jsonStyle.KEEP_TRAILING_COMMA = false
        assertEquals(currentJsonSettings["KEEP_TRAILING_COMMA"], false)
    }
}
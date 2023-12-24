package com.llamalad7.dotfileconfig

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.keymap.KeymapManager
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.psi.codeStyle.CodeStyleSchemes

object DotfileConfigPlugin {
    private val mapper = JsonMapper.builder()
        .enable(SerializationFeature.INDENT_OUTPUT)
        .build()
        .registerKotlinModule()
    private val homeDir = VfsUtil.getUserHomeDir() ?: error("Could not locate user home directory!")
    private val defaultCodeStyleSettings get() = CodeStyleSchemes.getInstance().defaultScheme.codeStyleSettings
    private val activeKeymap get() = KeymapManager.getInstance().activeKeymap
    val configFile get() = homeDir.findOrCreateChildData(this, ".ideaconfig")

    fun updateConfig() {
        configFile.refresh(false, false)
        val text = configFile.contentsToByteArray(false).decodeToString()
        if (text.isBlank()) {
            runWriteAction {
                val newText = writeConfig().toByteArray()
                configFile.getOutputStream(this).use { it.write(newText) }
            }
        } else {
            applyConfig(text)
        }
    }

    private fun applyConfig(text: String) {
        try {
            val config = mapper.readValue<DotfileConfig>(text)
            config.apply(
                codeStyleSettings = defaultCodeStyleSettings,
                keymap = activeKeymap,
            )
        } catch (e: Exception) {
            thisLogger().error("Failed to read IDEA config: ", e)
        }
    }

    private fun writeConfig(): String {
        val config = DotfileConfig(
            codeStyle = CodeStyleConfig(defaultCodeStyleSettings),
            keybinds = KeybindConfig(activeKeymap),
        )
        return mapper.writeValueAsString(config)
    }
}
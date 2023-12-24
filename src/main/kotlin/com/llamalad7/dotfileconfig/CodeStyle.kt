package com.llamalad7.dotfileconfig

import com.intellij.lang.Language
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable
import com.intellij.psi.codeStyle.CustomCodeStyleSettings
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider

private val indentOptions: Set<String> =
    CodeStyleSettingsCustomizable.IndentOption.values().mapTo(hashSetOf()) { it.name }
private val commonOptions: Set<String> = listOf(
    CodeStyleSettingsCustomizable.SpacingOption::class.java,
    CodeStyleSettingsCustomizable.BlankLinesOption::class.java,
    CodeStyleSettingsCustomizable.WrappingOrBraceOption::class.java,
    CodeStyleSettingsCustomizable.CommenterOption::class.java,
).flatMapTo(hashSetOf()) {
    it.enumConstants.map(Enum<*>::name)
}

fun getCurrentCodeStyle(codeStyle: CodeStyleSettings): Map<String, Map<String, Any>> {
    val providers = Language.getRegisteredLanguages().mapNotNull { LanguageCodeStyleSettingsProvider.forLanguage(it) }
    val result = mutableMapOf<String, Map<String, Any>>()
    for (provider in providers) {
        val language = provider.language
        val settings = mutableMapOf<String, Any>().also { result[language.id] = it }
        val collector = CodeStyleSettingsCollector(codeStyle, language, settings::put)
        for (type in LanguageCodeStyleSettingsProvider.SettingsType.values()) {
            provider.customizeSettings(collector, type)
        }
    }
    return result
}

fun applyCodeStyle(codeStyle: CodeStyleSettings, style: Map<String, Map<String, Any>>) {
    for ((languageId, settings) in style) {
        val language = Language.findLanguageByID(languageId) ?: continue
        val provider = LanguageCodeStyleSettingsProvider.forLanguage(language) ?: continue
        val applicator = CodeStyleSettingsApplicator(codeStyle, language, settings)
        for (type in LanguageCodeStyleSettingsProvider.SettingsType.values()) {
            provider.customizeSettings(applicator, type)
        }
    }
}

private class CodeStyleSettingsCollector(
    private val codeStyle: CodeStyleSettings,
    language: Language,
    private val consumer: (String, Any) -> Unit
) :
    CodeStyleSettingsCustomizable {
    private val common = codeStyle.getCommonSettings(language)

    override fun showAllStandardOptions() {
        common.indentOptions?.let { indent ->
            for (option in indentOptions) {
                visit(indent, option)
            }
        }
        for (option in commonOptions) {
            visit(common, option)
        }
    }

    override fun showStandardOptions(vararg optionNames: String) {
        for (option in optionNames) {
            visit(common, option)
        }
    }

    override fun showCustomOption(
        settingsClass: Class<out CustomCodeStyleSettings>,
        fieldName: String,
        title: String,
        groupName: String?,
        vararg options: Any?
    ) {
        visit(codeStyle.getCustomSettings(settingsClass), fieldName)
    }

    override fun showCustomOption(
        settingsClass: Class<out CustomCodeStyleSettings>,
        fieldName: String,
        title: String,
        groupName: String?,
        anchor: CodeStyleSettingsCustomizable.OptionAnchor?,
        anchorFieldName: String?,
        vararg options: Any?
    ) {
        visit(codeStyle.getCustomSettings(settingsClass), fieldName)
    }

    private fun visit(settingsObject: Any, fieldName: String) {
        val field = runCatching { settingsObject.javaClass.getField(fieldName) }.getOrElse { return }
        consumer(fieldName, field.get(settingsObject))
    }
}

private class CodeStyleSettingsApplicator(
    private val codeStyle: CodeStyleSettings,
    language: Language,
    private val settings: Map<String, Any>
) :
    CodeStyleSettingsCustomizable {
    private val common = codeStyle.getCommonSettings(language)

    override fun showAllStandardOptions() {
        common.indentOptions?.let { indentOptions ->
            for (option in CodeStyleSettingsCustomizable.IndentOption.values()) {
                apply(indentOptions, option.name)
            }
        }
        for (option in commonOptions) {
            apply(common, option)
        }
    }

    override fun showStandardOptions(vararg optionNames: String) {
        for (option in optionNames) {
            apply(common, option)
        }
    }

    override fun showCustomOption(
        settingsClass: Class<out CustomCodeStyleSettings>,
        fieldName: String,
        title: String,
        groupName: String?,
        vararg options: Any?
    ) {
        apply(codeStyle.getCustomSettings(settingsClass), fieldName)
    }

    override fun showCustomOption(
        settingsClass: Class<out CustomCodeStyleSettings>,
        fieldName: String,
        title: String,
        groupName: String?,
        anchor: CodeStyleSettingsCustomizable.OptionAnchor?,
        anchorFieldName: String?,
        vararg options: Any?
    ) {
        apply(codeStyle.getCustomSettings(settingsClass), fieldName)
    }

    private fun apply(settingsObject: Any, fieldName: String) {
        val field = runCatching { settingsObject.javaClass.getField(fieldName) }.getOrElse { return }
        val value = settings[fieldName] ?: return
        field.set(settingsObject, value)
    }
}
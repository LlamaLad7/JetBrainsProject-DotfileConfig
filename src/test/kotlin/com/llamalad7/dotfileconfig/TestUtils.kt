package com.llamalad7.dotfileconfig

import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.codeStyle.CodeStyleSchemes
import com.intellij.psi.codeStyle.CustomCodeStyleSettings
import junit.framework.TestCase
import kotlin.io.path.toPath

fun TestCase.getConfig(name: String) = VirtualFileManager.getInstance().findFileByNioPath(
    javaClass.classLoader.getResource("configs/$name.json")!!.toURI().toPath()
)!!

fun TestCase.loadConfig(name: String) = DotfileConfigPlugin.updateConfig(file = getConfig(name))

inline fun <reified T : CustomCodeStyleSettings> getCustomStyle() = codeStyle.getCustomSettings(T::class.java)

val codeStyle get() = CodeStyleSchemes.getInstance().defaultScheme.codeStyleSettings
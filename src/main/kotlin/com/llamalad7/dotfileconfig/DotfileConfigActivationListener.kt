package com.llamalad7.dotfileconfig

import com.intellij.openapi.application.ApplicationActivationListener
import com.intellij.openapi.wm.IdeFrame

class DotfileConfigActivationListener : ApplicationActivationListener {
    override fun applicationActivated(ideFrame: IdeFrame) {
        DotfileConfigPlugin.updateConfig()
    }
}
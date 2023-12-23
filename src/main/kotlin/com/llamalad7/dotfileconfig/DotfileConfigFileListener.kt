package com.llamalad7.dotfileconfig

import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent

class DotfileConfigFileListener : BulkFileListener {
    override fun after(events: List<VFileEvent>) {
        val configFile = DotfileConfigPlugin.configFile
        // Avoid reacting to our own events!
        if (events.any { it.file == configFile && it.requestor != DotfileConfigPlugin }) {
            DotfileConfigPlugin.updateConfig()
        }
    }
}
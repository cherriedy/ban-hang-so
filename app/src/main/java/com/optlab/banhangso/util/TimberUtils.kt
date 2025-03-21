package com.optlab.banhangso.util

import com.optlab.banhangso.BuildConfig
import timber.log.Timber

object TimberUtils {

    @JvmStatic
    fun configTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTreeWithTag())
        } else {
            Timber.plant(ReleaseTree())
        }
    }

    private class DebugTreeWithTag : Timber.DebugTree() {
        override fun createStackElementTag(element: StackTraceElement): String? {
            return super.createStackElementTag(element) + ": " + element.lineNumber;
        }
    }
}
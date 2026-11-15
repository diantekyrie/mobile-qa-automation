package com.qapractice.app

import android.os.Build
import android.view.View
import android.view.WindowInsets

/**
 * Android 15+ draws apps edge-to-edge, so content would sit under the status bar,
 * navigation bar and keyboard. This keeps the view's own padding and adds those insets.
 */
fun View.padForSystemBars() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return
    val left = paddingLeft
    val top = paddingTop
    val right = paddingRight
    val bottom = paddingBottom
    setOnApplyWindowInsetsListener { view, insets ->
        val bars = insets.getInsets(WindowInsets.Type.systemBars() or WindowInsets.Type.ime())
        view.setPadding(left + bars.left, top + bars.top, right + bars.right, bottom + bars.bottom)
        insets
    }
    requestApplyInsets()
}

package com.sam.canpoint.ecard.presentation

import android.app.Presentation
import android.content.Context
import android.view.Display
import com.sam.canpoint.ecard.keyboard.KeyBoardEvent

abstract class BasePresentation : Presentation, IPresentation {
    constructor(outerContext: Context?, display: Display?) : super(outerContext, display) {}
    constructor(outerContext: Context?, display: Display?, theme: Int) : super(outerContext, display, theme) {}

    override fun cancelPresentation() {
        cancel()
    }

    override fun showPresentation() {
        show()
    }

    override fun interceptKeyEvent(event: KeyBoardEvent?): Boolean {
        return false
    }
}
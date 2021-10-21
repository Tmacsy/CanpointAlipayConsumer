package com.sam.canpoint.ecard.presentation

import com.sam.canpoint.ecard.keyboard.KeyBoardEvent

interface IPresentation {

    fun cancelPresentation()

    fun showPresentation()

    fun interceptKeyEvent(event: KeyBoardEvent?): Boolean
}
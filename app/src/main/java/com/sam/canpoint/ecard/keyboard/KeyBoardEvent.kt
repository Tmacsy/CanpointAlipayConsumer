package com.sam.canpoint.ecard.keyboard

class KeyBoardEvent {
    var type: KeyBoardType? = null
    var num: String? = null
}

enum class KeyBoardType {
    CANCEL, FUNCTION, DOWN, UP, SETTING, CONFIRM, NUM, DELETE, ADD,NONE
}
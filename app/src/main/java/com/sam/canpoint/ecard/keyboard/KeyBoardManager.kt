package com.sam.canpoint.ecard.keyboard

import android.view.KeyEvent
import com.sam.system.log.L

class KeyBoardManager {

    var keyBoardCallback: IKeyBoardCallback? = null
    private val keyBoardEvent = KeyBoardEvent()

    fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        keyBoardEvent.type = KeyBoardType.NONE
        keyBoardEvent.num = ""
        when (keyCode) {
            SEARCH -> {
                keyBoardEvent.type = KeyBoardType.FUNCTION
                keyBoardCallback?.keyBoardEvent(keyBoardEvent)
                return true
            }
            UP -> {
                keyBoardEvent.type = KeyBoardType.UP
                keyBoardCallback?.keyBoardEvent(keyBoardEvent)
                return true
            }
            DOWN -> {
                keyBoardEvent.type = KeyBoardType.DOWN
                keyBoardCallback?.keyBoardEvent(keyBoardEvent)
                return true
            }
            CANCEL, CANCEL_TWO -> {
                keyBoardEvent.type = KeyBoardType.CANCEL
                keyBoardCallback?.keyBoardEvent(keyBoardEvent)
                return true
            }
            SETTING -> {
                keyBoardEvent.type = KeyBoardType.SETTING
                keyBoardCallback?.keyBoardEvent(keyBoardEvent)
                return true
            }
            ONE -> {
                keyBoardEvent.num = "1"
                keyBoardEvent.type = KeyBoardType.NUM
                keyBoardCallback?.keyBoardEvent(keyBoardEvent)
                return true
            }
            TWO -> {
                keyBoardEvent.num = "2"
                keyBoardEvent.type = KeyBoardType.NUM
                keyBoardCallback?.keyBoardEvent(keyBoardEvent)
                return true
            }
            THREE -> {
                keyBoardEvent.num = "3"
                keyBoardEvent.type = KeyBoardType.NUM
                keyBoardCallback?.keyBoardEvent(keyBoardEvent)
                return true
            }
            FOUR -> {
                keyBoardEvent.num = "4"
                keyBoardEvent.type = KeyBoardType.NUM
                keyBoardCallback?.keyBoardEvent(keyBoardEvent)
                return true
            }
            FIVE -> {
                keyBoardEvent.num = "5"
                keyBoardEvent.type = KeyBoardType.NUM
                keyBoardCallback?.keyBoardEvent(keyBoardEvent)
                return true
            }
            SIX -> {
                keyBoardEvent.num = "6"
                keyBoardEvent.type = KeyBoardType.NUM
                keyBoardCallback?.keyBoardEvent(keyBoardEvent)
                return true
            }
            SEVEN -> {
                keyBoardEvent.num = "7"
                keyBoardEvent.type = KeyBoardType.NUM
                keyBoardCallback?.keyBoardEvent(keyBoardEvent)
                return true
            }
            EIGHT -> {
                keyBoardEvent.num = "8"
                keyBoardEvent.type = KeyBoardType.NUM
                keyBoardCallback?.keyBoardEvent(keyBoardEvent)
                return true
            }
            NINE -> {
                keyBoardEvent.num = "9"
                keyBoardEvent.type = KeyBoardType.NUM
                keyBoardCallback?.keyBoardEvent(keyBoardEvent)
                return true
            }
            DELETE -> {
                keyBoardEvent.type = KeyBoardType.DELETE
                keyBoardCallback?.keyBoardEvent(keyBoardEvent)
                return true
            }
            ZERO -> {
                keyBoardEvent.num = "0"
                keyBoardEvent.type = KeyBoardType.NUM
                keyBoardCallback?.keyBoardEvent(keyBoardEvent)
                return true
            }
            POINT -> {
                keyBoardEvent.num = "."
                keyBoardEvent.type = KeyBoardType.NUM
                keyBoardCallback?.keyBoardEvent(keyBoardEvent)
                return true
            }
            ADD -> {
                keyBoardEvent.type = KeyBoardType.ADD
                keyBoardCallback?.keyBoardEvent(keyBoardEvent)
                return true
            }
            CONFIRM -> {
                keyBoardEvent.type = KeyBoardType.CONFIRM
                keyBoardCallback?.keyBoardEvent(keyBoardEvent)
                return true
            }
            else -> return true
        }
    }

    companion object {
        const val SEARCH = 131 //功能
        const val UP = 19 //上
        const val DOWN = 20 //下
        const val SETTING = 132 //设置
        const val SEVEN = 151 //7
        const val EIGHT = 152 //8
        const val NINE = 153 //9
        const val FOUR = 148 //4
        const val FIVE = 149 //5
        const val SIX = 150 //6
        const val DELETE = 67 // 删除
        const val ONE = 145 //1
        const val TWO = 146 //2
        const val THREE = 147 //3
        const val ZERO = 144 //0
        const val POINT = 158 //.
        const val ADD = 157 //add
        const val CONFIRM = 66 //确认
        const val CANCEL = 4 //取消
        const val CANCEL_TWO = 111 //取消
    }
}
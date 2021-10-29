package com.sam.canpoint.ecard.utils

import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar

inline fun AppCompatSeekBar.setChangeListener(
        crossinline onProgressChanged: (seekBar: SeekBar?, progress: Int, fromUser: Boolean) -> Unit = { _, _, _ -> },
        crossinline onStartTrackingTouch: (seekBar: SeekBar?) -> Unit = { _ -> },
        crossinline onStopTrackingTouch: (seekBar: SeekBar?) -> Unit = { _ -> },
) {
    val listener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            onProgressChanged.invoke(seekBar, progress, fromUser)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            onStartTrackingTouch.invoke(seekBar)
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            onStopTrackingTouch.invoke(seekBar)
        }
    }
    this.setOnSeekBarChangeListener(listener)
}
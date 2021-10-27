package com.sam.canpoint.ecard.ui.init

import android.content.Context
import android.os.Bundle
import android.view.Display
import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.ui.presentation.BasePresentation

class RegisterPresentation(mContext: Context, display: Display) : BasePresentation(mContext, display) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.presentation_register)
    }
}
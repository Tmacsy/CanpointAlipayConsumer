package com.sam.canpoint.ecard.ui.splash;

import android.content.Context;
import android.os.Bundle;
import android.view.Display;

import com.sam.canpoint.ecard.R;
import com.sam.canpoint.ecard.presentation.BasePresentation;

public class SplashPresentation extends BasePresentation {

    public SplashPresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.presentation_splash);
    }
}

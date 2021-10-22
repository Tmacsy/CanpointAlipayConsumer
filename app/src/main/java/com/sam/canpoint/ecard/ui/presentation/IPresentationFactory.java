package com.sam.canpoint.ecard.ui.presentation;

import android.app.Activity;

public interface IPresentationFactory {
    BasePresentation createPresentation(Activity activity, String type);
}

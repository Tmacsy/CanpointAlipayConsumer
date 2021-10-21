package com.sam.canpoint.ecard.presentation;

import android.app.Activity;
import android.app.Presentation;

public interface IPresentationFactory {
    BasePresentation createPresentation(Activity activity, String type);
}

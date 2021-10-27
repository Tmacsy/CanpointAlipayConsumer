package com.sam.canpoint.ecard.ui.pay

import android.animation.Animator
import android.animation.ObjectAnimator

inline fun ObjectAnimator.addListener(
        crossinline onAnimationStart: (animation: Animator?) -> Unit = { _ -> },
        crossinline onAnimationEnd: (animation: Animator?) -> Unit = { _ -> },
        crossinline onAnimationCancel: (animation: Animator?) -> Unit = { _ -> },
        crossinline onAnimationRepeat: (animation: Animator?) -> Unit = { _ -> },
) {
    val listener = object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator?) {
            onAnimationStart.invoke(animation)
        }

        override fun onAnimationEnd(animation: Animator?) {
            onAnimationEnd.invoke(animation)
        }

        override fun onAnimationCancel(animation: Animator?) {
            onAnimationCancel.invoke(animation)
        }

        override fun onAnimationRepeat(animation: Animator?) {
            onAnimationRepeat.invoke(animation)
        }
    }
    addListener(listener)
}
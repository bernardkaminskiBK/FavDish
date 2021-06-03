package com.udemy_jetpack.favdish.utils

import android.animation.*
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView

object Animations {

    fun shower(view: View, context: Context, pic: Int) {
        val container = view.parent as ViewGroup
        val containerW = container.width
        val containerH = container.height
        var imgW: Float = view.width.toFloat()
        var imgH: Float = view.height.toFloat()

        val newImg = AppCompatImageView(context)
        newImg.setImageResource(pic)
        newImg.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        container.addView(newImg)

        newImg.scaleX = Math.random().toFloat() * 1.5f + .1f
        newImg.scaleY = newImg.scaleX
        imgW *= newImg.scaleX
        imgH *= newImg.scaleY

        newImg.translationX = Math.random().toFloat() *
                containerW - imgW / 20

        val mover = ObjectAnimator.ofFloat(
            newImg, View.TRANSLATION_Y,
            -imgH, containerH + imgH
        )
        mover.interpolator = AccelerateInterpolator(1f)
        val rotator = ObjectAnimator.ofFloat(
            newImg, View.ROTATION,
            (Math.random() * 1080).toFloat()
        )
        rotator.interpolator = LinearInterpolator()

        val set = AnimatorSet()
        set.playTogether(mover, rotator)
        set.duration = (Math.random() * 3000 + 1500).toLong()

        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                container.removeView(newImg)
            }
        })
        set.start()
    }

    fun scale(imageView: ImageView, view: View) {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.20f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.20f)
        val animator = ObjectAnimator.ofPropertyValuesHolder(
            imageView, scaleX, scaleY
        )
        animator.repeatCount = 1
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.duration = 150
        animator.disableViewDuringAnimation(view)
        animator.start()
    }

    private fun ObjectAnimator.disableViewDuringAnimation(view: View) {
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                view.isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator?) {
                view.isEnabled = true
            }
        })
    }
}
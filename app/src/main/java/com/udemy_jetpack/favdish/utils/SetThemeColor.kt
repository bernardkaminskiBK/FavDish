package com.udemy_jetpack.favdish.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import com.udemy_jetpack.favdish.R

object SetThemeColor {

    private lateinit var sharedPreferences: SharedPreferences

    fun themePreferences(context: Context, theme: Resources.Theme) {
        sharedPreferences =
            context.getSharedPreferences(Constants.themePref, Context.MODE_PRIVATE)

        when (sharedPreferences.getString(Constants.themeKey, Constants.colorOrange)) {
            Constants.colorLime -> theme.applyStyle(R.style.OverlayThemeLime, true)
            Constants.colorRed -> theme.applyStyle(R.style.OverlayThemeRed, true)
            Constants.colorGray -> theme.applyStyle(R.style.OverlayThemeGray, true)
            Constants.colorPink -> theme.applyStyle(R.style.OverlayThemePink, true)
            Constants.colorLightBlue -> theme.applyStyle(R.style.OverlayThemeLightBlue, true)
            Constants.colorOrange -> theme.applyStyle(R.style.OverlayThemeLightOrange, true)
            Constants.colorDeepOrange -> theme.applyStyle(R.style.OverlayThemeDeepOrange, true)
            Constants.colorDeepPink -> theme.applyStyle(R.style.OverlayThemeDeepPink, true)
            Constants.colorTeal -> theme.applyStyle(R.style.OverlayThemeTeal, true)
            Constants.colorBrown -> theme.applyStyle(R.style.OverlayThemeBrown, true)
        }
    }

}
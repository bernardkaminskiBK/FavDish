package com.udemy_jetpack.favdish.view.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.*
import com.udemy_jetpack.favdish.R
import com.udemy_jetpack.favdish.databinding.ActivityMainBinding
import com.udemy_jetpack.favdish.model.notification.NotifyWorker
import com.udemy_jetpack.favdish.utils.Constants
import com.udemy_jetpack.favdish.utils.SetThemeColor
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mNavController: NavController
    private var isAppSettingOpen = true

    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences =
            getSharedPreferences(Constants.themePref, Context.MODE_PRIVATE)

        SetThemeColor.themePreferences(this, theme)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setSupportActionBar(mBinding.toolbar)
        setContentView(mBinding.root)

        selectColorSetOnClickListeners()
        mBinding.viewDisableLayout.setOnClickListener(this)

        mNavController = findNavController(R.id.nav_host_fragment)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_all_dishes,
                R.id.navigation_favorite_dishes,
                R.id.navigation_random_dishes
            )
        )

        setupActionBarWithNavController(mNavController, appBarConfiguration)
        mBinding.navView.setupWithNavController(mNavController)

        if (intent.hasExtra(Constants.NOTIFICATION_ID)) {
            val notificationId = intent.getIntExtra(Constants.NOTIFICATION_ID, 0)
            mBinding.navView.selectedItemId = R.id.navigation_random_dishes
        }

        startWork()

    }

    override fun onClick(click: View) {
        when (click.id) {
            R.id.red_theme -> {
                editThemeColorPreferences(Constants.colorRed)
                return
            }
            R.id.lime_theme -> {
                editThemeColorPreferences(Constants.colorLime)
                return
            }
            R.id.gray_theme -> {
                editThemeColorPreferences(Constants.colorGray)
                return
            }
            R.id.pink_theme -> {
                editThemeColorPreferences(Constants.colorPink)
                return
            }
            R.id.lightBlue_theme -> {
                editThemeColorPreferences(Constants.colorLightBlue)
                return
            }
            R.id.orange_theme -> {
                editThemeColorPreferences(Constants.colorOrange)
                return
            }
            R.id.deepOrange_theme -> {
                editThemeColorPreferences(Constants.colorDeepOrange)
                return
            }
            R.id.deepPink_theme -> {
                editThemeColorPreferences(Constants.colorDeepPink)
                return
            }
            R.id.teal_theme -> {
                editThemeColorPreferences(Constants.colorTeal)
                return
            }
            R.id.brown_theme -> {
                editThemeColorPreferences(Constants.colorBrown)
                return
            }
            R.id.viewDisableLayout -> {
                closeAppSetting()
                return
            }
        }
    }

    private fun selectColorSetOnClickListeners() {
        mBinding.limeTheme.setOnClickListener(this)
        mBinding.redTheme.setOnClickListener(this)
        mBinding.grayTheme.setOnClickListener(this)
        mBinding.pinkTheme.setOnClickListener(this)
        mBinding.lightBlueTheme.setOnClickListener(this)
        mBinding.orangeTheme.setOnClickListener(this)
        mBinding.deepOrangeTheme.setOnClickListener(this)
        mBinding.deepPinkTheme.setOnClickListener(this)
        mBinding.tealTheme.setOnClickListener(this)
        mBinding.brownTheme.setOnClickListener(this)
    }

    private fun editThemeColorPreferences(string: String) {
        sharedPreferences.edit().putString(Constants.themeKey, string).apply()
        val intent = intent
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        finish()
        startActivity(intent)
    }

    fun appSettings() {
        if (isAppSettingOpen) {
            openAppSetting()
        } else {
            closeAppSetting()
        }
    }

    private fun openAppSetting() {
        isAppSettingOpen = false
        mBinding.settingTheme.animate().apply {
            duration = 700
            translationY(0f)
        }
        mBinding.viewDisableLayout.animate().alpha(0.25f).duration = 700
        mBinding.viewDisableLayout.isClickable = true
    }

    fun closeAppSetting() {
        isAppSettingOpen = true
        mBinding.settingTheme.animate().apply {
            duration = 700
            translationY(-950f)
        }
        mBinding.viewDisableLayout.animate().alpha(0.0f).duration = 700
        mBinding.viewDisableLayout.isClickable = false
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(mNavController, null)
    }

    fun changeActionBarColor(color: Int) {
        mBinding.toolbar.setBackgroundColor(color)
    }

    fun changeActionBarTitle(title: String) {
        mBinding.toolbar.title = title
    }

    fun changeActionBarTitleTextColor(color: Int) {
        mBinding.toolbar.setTitleTextColor(color)
    }

    fun hideActionBarDivider(hide: Int) {
        mBinding.toolbarDividerLine.visibility = hide
    }

    fun hideBottomNavigationView() {
        mBinding.navView.clearAnimation()
        mBinding.navView.animate().translationY(mBinding.navView.height.toFloat()).duration = 300
        mBinding.navView.visibility = View.GONE
        mBinding.toolbarDividerLineNavView.visibility = View.GONE
    }

    fun showBottomNavigationView() {
        mBinding.navView.clearAnimation()
        mBinding.navView.animate().translationY(0f).duration = 300
        mBinding.toolbarDividerLineNavView.visibility = View.VISIBLE
        mBinding.navView.visibility = View.VISIBLE
    }

    private fun createConstraints() = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
        .setRequiresCharging(false)
        .setRequiresBatteryNotLow(true)
        .build()

    private fun createWorkRequest() =
        PeriodicWorkRequestBuilder<NotifyWorker>(6, TimeUnit.HOURS)
            .setConstraints(createConstraints()).build()

    private fun startWork() {
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "FavDish Notify Work",
                ExistingPeriodicWorkPolicy.KEEP,
                createWorkRequest()
            )
    }

}
package com.udemy_jetpack.favdish.view.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
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
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mNavController: NavController
    private var isAppSettingOpen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mNavController = findNavController(R.id.nav_host_fragment)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_all_dishes,
                R.id.navigation_favorite_dishes,
                R.id.navigation_random_dishes
            )
        )

        setSupportActionBar(mBinding.toolbar)

        setupActionBarWithNavController(mNavController, appBarConfiguration)
        mBinding.navView.setupWithNavController(mNavController)

        if (intent.hasExtra(Constants.NOTIFICATION_ID)) {
            val notificationId = intent.getIntExtra(Constants.NOTIFICATION_ID, 0)
            mBinding.navView.selectedItemId = R.id.navigation_random_dishes
        }

        startWork()

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
            duration = 300
            translationY(0f)
        }
    }

    fun closeAppSetting() {
        isAppSettingOpen = true
        mBinding.settingTheme.animate().apply {
            duration = 150
            translationY(-950f)
        }
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
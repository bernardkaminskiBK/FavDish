package com.udemy_jetpack.favdish.application

import android.app.Application
import com.udemy_jetpack.favdish.model.database.FavDishRepository
import com.udemy_jetpack.favdish.model.database.FavDishRoomDatabase

class FavDishApplication : Application() {

    private val database by lazy {
        FavDishRoomDatabase.getDatabase((this@FavDishApplication))
    }

    val repository by lazy { FavDishRepository(database.favDishDao()) }

}
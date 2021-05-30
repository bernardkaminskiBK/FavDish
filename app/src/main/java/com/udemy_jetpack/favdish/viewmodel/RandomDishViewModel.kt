package com.udemy_jetpack.favdish.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udemy_jetpack.favdish.model.entities.RandomDish
import com.udemy_jetpack.favdish.model.network.RandomDishApiService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class RandomDishViewModel : ViewModel() {

    private val randomRecipeApiService = RandomDishApiService()
    private val compositeDisposable = CompositeDisposable()

    private val _loadRandomDish = MutableLiveData<Boolean>()
    private val _randomDishResponse = MutableLiveData<RandomDish.Recipes>()
    private val _randomDishLoadingError = MutableLiveData<Boolean>()

    val loadRandomDish: LiveData<Boolean>
        get() = _loadRandomDish

    val randomDishResponse: LiveData<RandomDish.Recipes>
        get() = _randomDishResponse

    val randomDishLoadingError: LiveData<Boolean>
        get() = _randomDishLoadingError

    fun getRandomRecipeFromAPI() {
        _loadRandomDish.value = true

        compositeDisposable.add(
            randomRecipeApiService.getRandomDish()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<RandomDish.Recipes>() {
                    override fun onSuccess(value: RandomDish.Recipes?) {
                        _loadRandomDish.value = false
                        _randomDishResponse.value = value!!
                        _randomDishLoadingError.value = false
                    }

                    override fun onError(e: Throwable?) {
                        _loadRandomDish.value = false
                        _randomDishLoadingError.value = true
                        e!!.printStackTrace()
                    }
                })
        )
    }

}
package com.udemy_jetpack.favdish.view.fragments

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.udemy_jetpack.favdish.R
import com.udemy_jetpack.favdish.application.FavDishApplication
import com.udemy_jetpack.favdish.databinding.FragmentRandomDishBinding
import com.udemy_jetpack.favdish.model.entities.FavDish
import com.udemy_jetpack.favdish.model.entities.RandomDish
import com.udemy_jetpack.favdish.utils.Constants
import com.udemy_jetpack.favdish.viewmodel.FavDishViewModel
import com.udemy_jetpack.favdish.viewmodel.FavDishViewModelFactory
import com.udemy_jetpack.favdish.viewmodel.RandomDishViewModel

class RandomDishFragment : Fragment() {

    private var mBinding: FragmentRandomDishBinding? = null

    private lateinit var mRandomDishViewModel: RandomDishViewModel

    private var mProgressDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentRandomDishBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRandomDishViewModel = ViewModelProvider(this).get(RandomDishViewModel::class.java)

        mRandomDishViewModel.getRandomRecipeFromAPI()

        randomDishViewModelObserver()

        mBinding!!.swipeRefreshLayoutRandomDish.setOnRefreshListener {
            mRandomDishViewModel.getRandomRecipeFromAPI()
        }
    }

    private fun randomDishViewModelObserver() {
        mRandomDishViewModel.randomDishResponse.observe(viewLifecycleOwner,
            { randomDishResponse ->
                randomDishResponse?.let {
                    if (mBinding!!.swipeRefreshLayoutRandomDish.isRefreshing) {
                        mBinding!!.swipeRefreshLayoutRandomDish.isRefreshing = false
                    }
                    setRandomDishResponseInUI(randomDishResponse.recipes[0])
                }
            })

        mRandomDishViewModel.randomDishLoadingError.observe(viewLifecycleOwner,
            { randomDishLoadingError ->
                randomDishLoadingError?.let {
                    Log.e("Data error", "$randomDishLoadingError")
                    if (mBinding!!.swipeRefreshLayoutRandomDish.isRefreshing) {
                        mBinding!!.swipeRefreshLayoutRandomDish.isRefreshing = false
                    }
                }
            })

        mRandomDishViewModel.loadRandomDish.observe(viewLifecycleOwner,
            { loadRandomDish ->
                loadRandomDish?.let {
                    Log.i("Load random dish", "$loadRandomDish")

                    if (loadRandomDish && !mBinding!!.swipeRefreshLayoutRandomDish.isRefreshing) {
                        showCustomProgressDialog()
                    } else {
                        hideCustomProgressDialog()
                    }
                }
            })
    }

    private fun setRandomDishResponseInUI(recipe: RandomDish.Recipe) {
        Glide.with(requireActivity())
            .load(recipe.image)
            .centerCrop()
            .into(mBinding!!.ivDishImage)

        mBinding!!.tvTitle.text = recipe.title

        var dishType: String = "other"

        if (recipe.dishTypes.isNotEmpty()) {
            dishType = recipe.dishTypes[0]
            mBinding!!.tvType.text = dishType
        }

        mBinding!!.tvCategory.text = "Other"
        var ingredients = ""
        for (value in recipe.extendedIngredients) {
            if (ingredients.isEmpty()) {
                ingredients = value.original
            } else {
                ingredients = ingredients + ", \n" + value.original
            }
        }
        mBinding!!.tvIngredients.text = ingredients

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mBinding!!.tvCookingDirection.text = Html.fromHtml(
                recipe.instructions,
                Html.FROM_HTML_MODE_COMPACT
            )
        } else {
            @Suppress("DEPRECATION")
            mBinding!!.tvCookingDirection.text = Html.fromHtml(recipe.instructions)
        }

        mBinding!!.tvCookingTime.text =
            getString(R.string.label_estimate_cooking_time, recipe.readyInMinutes.toString())

        mBinding!!.ivFavoriteDish.setImageDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.ic_favorite_unselected
            )
        )

        var addedToFavorite = false

        mBinding!!.ivFavoriteDish.setOnClickListener {
            if (addedToFavorite) {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.msg_already_added_to_favorites),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val randomDishDetails = FavDish(
                    0,
                    recipe.image,
                    Constants.DISH_IMAGE_SOURCE_ONLINE,
                    recipe.title,
                    dishType,
                    "Other",
                    ingredients,
                    recipe.readyInMinutes.toString(),
                    recipe.instructions,
                    true
                )

                val mFavDishViewModel: FavDishViewModel by viewModels {
                    FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
                }
                mFavDishViewModel.insert(randomDishDetails)

                addedToFavorite = true

                mBinding!!.ivFavoriteDish.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.ic_favorite_selected
                    )
                )
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.msg_added_to_favorites),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showCustomProgressDialog() {
        mProgressDialog = Dialog(requireActivity())
        mProgressDialog?.let {
            it.setContentView(R.layout.dialog_custom_progress)
            it.show()
        }
    }

    private fun hideCustomProgressDialog() {
        mProgressDialog?.let {
            it.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }
}
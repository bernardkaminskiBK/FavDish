package com.udemy_jetpack.favdish.view.fragments

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.udemy_jetpack.favdish.R
import com.udemy_jetpack.favdish.application.FavDishApplication
import com.udemy_jetpack.favdish.databinding.FragmentDishDetailsBinding
import com.udemy_jetpack.favdish.model.entities.FavDish
import com.udemy_jetpack.favdish.utils.Animations
import com.udemy_jetpack.favdish.utils.Constants
import com.udemy_jetpack.favdish.view.activities.MainActivity
import com.udemy_jetpack.favdish.viewmodel.FavDishViewModel
import com.udemy_jetpack.favdish.viewmodel.FavDishViewModelFactory
import java.io.IOException
import java.util.*


class DishDetailsFragment : Fragment() {

    private var mFavDishDetails: FavDish? = null
    private var mBinding: FragmentDishDetailsBinding? = null

    private var primaryDarkColor: Int? = null
    private var primaryColor: Int? = null
    private var whiteColor: Int? = null

    private var resourceDrawable: Drawable? = null

    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory(((requireActivity().application) as FavDishApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_share, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_share_dish -> {
                shareDishDetails()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentDishDetailsBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: DishDetailsFragmentArgs by navArgs()
        mFavDishDetails = args.dishDetails

        primaryDarkColor = requireActivity().window.statusBarColor
        primaryColor = requireActivity().window.navigationBarColor
        whiteColor = ContextCompat.getColor(requireContext(), R.color.white)

        args.let {
            try {
                loadImageToUIView(it)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            setDishDetailsInfoToUI(it)

            if (args.dishDetails.favoriteDish)
                setIvFavoriteDishImage(R.drawable.ic_favorite_selected)
            else
                setIvFavoriteDishImage(R.drawable.ic_favorite_unselected)
        }
        addDishToFavorites(args)
    }

    override fun onResume() {
        changeDishDetailsThemeColor()
        super.onResume()
    }

    override fun onStop() {
        requireActivity().window.statusBarColor = primaryDarkColor!!
        requireActivity().window.navigationBarColor = primaryColor!!

        changeToolbarAndTextColor(whiteColor!!, primaryDarkColor!!)
        visibleToolbarDivider(View.VISIBLE)
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }

    private fun loadImageToUIView(it: DishDetailsFragmentArgs) {
        Glide.with(requireActivity())
            .load(it.dishDetails.image)
            .centerCrop()
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.e("TAG", "Failed load image", e)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    resourceDrawable = resource
                    changeDishDetailsThemeColor()
                    return false
                }
            }).into(mBinding!!.ivDishImage)
    }

    private fun addDishToFavorites(args: DishDetailsFragmentArgs) {
        mBinding!!.ivFavoriteDish.setOnClickListener {

            args.dishDetails.favoriteDish = !args.dishDetails.favoriteDish

            mFavDishViewModel.update(args.dishDetails)

            if (args.dishDetails.favoriteDish) {
                Animations.scale(mBinding!!.ivFavoriteDish, mBinding!!.ivDishImage)
                makeShowerAnim(150)
                setIvFavoriteDishImage(R.drawable.ic_favorite_selected)
                makeToastText(getString(R.string.msg_added_to_favorites))
            } else {
                Animations.scale(mBinding!!.ivFavoriteDish, mBinding!!.ivDishImage)
                setIvFavoriteDishImage(R.drawable.ic_favorite_unselected)
                makeToastText(getString(R.string.msg_removed_from_favorites))
            }
        }
    }

    private fun setDishDetailsInfoToUI(it: DishDetailsFragmentArgs) {
        mBinding!!.tvTitle.text = it.dishDetails.title
        mBinding!!.tvType.text =
            it.dishDetails.type.capitalize(Locale.ROOT) // Used to make first letter capital
        mBinding!!.tvCategory.text = it.dishDetails.category
        mBinding!!.tvIngredients.text = it.dishDetails.ingredients
        // mBinding!!.tvCookingDirection.text = it.dishDetails.directionToCook

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mBinding!!.tvCookingDirection.text = Html.fromHtml(
                it.dishDetails.directionToCook,
                Html.FROM_HTML_MODE_COMPACT
            )
        } else {
            @Suppress("DEPRECATION")
            mBinding!!.tvCookingDirection.text = Html.fromHtml(it.dishDetails.directionToCook)
        }

        mBinding!!.tvCookingTime.text =
            getString(R.string.label_estimate_cooking_time, it.dishDetails.cookingTime)
    }

    private fun changeDishDetailsThemeColor() {
        resourceDrawable?.let {
            Palette.from(resourceDrawable!!.toBitmap())
                .generate { palette ->
                    val lightColor = palette?.lightVibrantSwatch?.rgb ?: 0
                    val darkColor = palette?.darkVibrantSwatch?.rgb ?: 0

                    if (lightColor != 0 && darkColor != 0) {
                        mBinding!!.rlDishDetailMain.setBackgroundColor(
                            lightColor
                        )
                        requireActivity().window.navigationBarColor = darkColor
                        requireActivity().window.statusBarColor = lightColor
                        changeToolbarAndTextColor(darkColor, whiteColor!!)
                        visibleToolbarDivider(View.GONE)
                    } else {
                        changeToolbarAndTextColor(primaryColor!!, whiteColor!!)
                    }
                }
        }
    }

    private fun makeShowerAnim(numOfShapes: Int) {
        for (i in 1..numOfShapes) {
            Animations.shower(
                mBinding!!.ivDishImage,
                requireContext(),
                R.drawable.ic_favorite_selected
            )
        }
    }

    private fun setIvFavoriteDishImage(image: Int) {
        mBinding!!.ivFavoriteDish.setImageDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                image
            )
        )
    }

    private fun makeToastText(text: String) {
        Toast.makeText(
            requireActivity(),
            text, Toast.LENGTH_SHORT
        ).show()
    }

    private fun visibleToolbarDivider(hide: Int) {
        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)?.hideActionBarDivider(
                hide
            )
        }
    }

    private fun changeToolbarAndTextColor(actionBarColor: Int, actionBarTitleColor: Int) {
        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)?.changeActionBarColor(actionBarColor)
            (activity as MainActivity?)?.changeActionBarTitleTextColor(actionBarTitleColor)
        }
    }

    private fun shareDishDetails() {
        val type = "text/plain"
        val subject = "Checkout this dish recipe"
        var extraText = ""
        val shareWith = "Share with"

        mFavDishDetails?.let {
            var image = ""
            if (it.imageSource == Constants.DISH_IMAGE_SOURCE_ONLINE) {
                image = it.image
            }

            val cookingInstruction: String

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                cookingInstruction = Html.fromHtml(
                    it.directionToCook,
                    Html.FROM_HTML_MODE_COMPACT
                ).toString()
            } else {
                @Suppress("DEPRECATION")
                cookingInstruction = Html.fromHtml(it.directionToCook).toString()
            }

            extraText =
                "$image \n" +
                        "\n Title:  ${it.title} \n\n Type: ${it.type} \n\n Category: ${it.category}" +
                        "\n\n Ingredients: \n ${it.ingredients} \n\n Instructions To Cook: \n $cookingInstruction" +
                        "\n\n Time required to cook the dish approx ${it.cookingTime} minutes."
        }
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = type
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, extraText)
        startActivity(Intent.createChooser(intent, shareWith))
    }

}
package com.udemy_jetpack.favdish.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.udemy_jetpack.favdish.application.FavDishApplication
import com.udemy_jetpack.favdish.databinding.FragmentFavoriteDishesBinding
import com.udemy_jetpack.favdish.model.entities.FavDish
import com.udemy_jetpack.favdish.view.activities.MainActivity
import com.udemy_jetpack.favdish.view.adapters.FavDishAdapter
import com.udemy_jetpack.favdish.viewmodel.FavDishViewModel
import com.udemy_jetpack.favdish.viewmodel.FavDishViewModelFactory

class FavoriteDishesFragment : Fragment() {

    private var mBinding: FragmentFavoriteDishesBinding? = null
    private var lastPosition: Int = 0

    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentFavoriteDishesBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mFavDishViewModel.favoriteDishes.observe(viewLifecycleOwner) { dishes ->
            dishes.let {

                mBinding!!.recyclerViewFavDishes.layoutManager =
                    GridLayoutManager(requireActivity(), 2)
                val favDishAdapter = FavDishAdapter(
                    this@FavoriteDishesFragment
                )

                mBinding!!.recyclerViewFavDishes.adapter = favDishAdapter
                mBinding!!.recyclerViewFavDishes.scrollToPosition(lastPosition)

                if (it.isNotEmpty()) {
                    mBinding!!.recyclerViewFavDishes.visibility = View.VISIBLE
                    mBinding!!.tvNoAvailableFavDishes.visibility = View.GONE
                    favDishAdapter.dishesList(it)
                } else {
                    mBinding!!.recyclerViewFavDishes.visibility = View.GONE
                    mBinding!!.tvNoAvailableFavDishes.visibility = View.VISIBLE
                }
            }
        }
    }

    fun dishDetails(position: Int, dish: FavDish) {
        findNavController().navigate(
            FavoriteDishesFragmentDirections.actionNavigationFavoriteDishesToNavigationDishDetails(
                dish
            )
        )
        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)!!.hideBottomNavigationView()
        }
        lastPosition = position
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)!!.showBottomNavigationView()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }

}
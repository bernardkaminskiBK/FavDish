package com.udemy_jetpack.favdish.view.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.udemy_jetpack.favdish.R
import com.udemy_jetpack.favdish.application.FavDishApplication
import com.udemy_jetpack.favdish.databinding.DialogCustomListBinding
import com.udemy_jetpack.favdish.databinding.FragmentAllDishesBinding
import com.udemy_jetpack.favdish.model.entities.FavDish
import com.udemy_jetpack.favdish.utils.Constants
import com.udemy_jetpack.favdish.view.activities.AddUpdateDishActivity
import com.udemy_jetpack.favdish.view.activities.MainActivity
import com.udemy_jetpack.favdish.view.adapters.CustomListItemAdapter
import com.udemy_jetpack.favdish.view.adapters.FavDishAdapter
import com.udemy_jetpack.favdish.viewmodel.FavDishViewModel
import com.udemy_jetpack.favdish.viewmodel.FavDishViewModelFactory
import java.util.*

class AllDishesFragment : Fragment() {

    private lateinit var mBinding: FragmentAllDishesBinding

    private lateinit var mFavDishAdapter: FavDishAdapter
    private lateinit var mCustomListDialog: Dialog

    private var actionAddDishMenuItem: MenuItem? = null

    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentAllDishesBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mFavDishViewModel.allDishesList.observe(viewLifecycleOwner) { dishes ->
            dishes.let {

                mBinding.rvDishesList.layoutManager = GridLayoutManager(requireActivity(), 2)
                mFavDishAdapter = FavDishAdapter(this@AllDishesFragment)
                mBinding.rvDishesList.adapter = mFavDishAdapter

                if (it.isNotEmpty()) {
                    mBinding.rvDishesList.visibility = View.VISIBLE
                    mBinding.tvNoDishesAddedYet.visibility = View.GONE
                    mFavDishAdapter.dishesList(it)
                } else {
                    mBinding.rvDishesList.visibility = View.GONE
                    mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
                    mBinding.tvNoDishesAddedYet.text = getString(R.string.list_of_dish_empty)
                }
            }
        }
    }

    fun dishDetails(favDish: FavDish) {
        findNavController().navigate(
            AllDishesFragmentDirections.actionNavigationAllDishesToNavigationDishDetails(
                favDish
            )
        )
        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)?.hideBottomNavigationView()
        }
    }

    fun deleteDish(dish: FavDish) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_delete_dish))
        builder.setMessage(getString(R.string.msg_delete_dish_dialog, dish.title))
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton(getString(R.string.lbl_yes)) { dialogInterface, _ ->
            mFavDishViewModel.delete(dish)
            dialogInterface.dismiss()
        }

        builder.setNegativeButton(getString(R.string.lbl_no)) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun filterDishesListDialog() {
        mCustomListDialog = Dialog(requireActivity())
        val binding: DialogCustomListBinding =
            DialogCustomListBinding.inflate(layoutInflater)

        mCustomListDialog.setContentView(binding.root)
        binding.tvTitle.text = getString(R.string.title_select_item_to_filter)

        val dishTypes = Constants.dishTypes()
        dishTypes.add(0, Constants.ALL_ITEMS)
        binding.rvList.layoutManager = LinearLayoutManager(requireActivity())

        val adapter =
            CustomListItemAdapter(
                requireActivity(),
                this@AllDishesFragment,
                dishTypes,
                Constants.FILTER_SELECTION
            )

        binding.rvList.adapter = adapter
        mCustomListDialog.show()
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)?.showBottomNavigationView()
        }
        actionAddDishMenuItem?.let {
            it.isEnabled = true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_all_dishes, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_dish -> {
                actionAddDishMenuItem = item
                actionAddDishMenuItem?.isEnabled = false
                startActivity(Intent(requireActivity(), AddUpdateDishActivity::class.java))
                return true
            }
            R.id.action_filter_dishes -> {
                if (requireActivity() is MainActivity) {
                    (activity as MainActivity?)?.closeAppSetting()
                }
                filterDishesListDialog()
                return true
            }
            R.id.action_theme_setting -> {
                if (requireActivity() is MainActivity) {
                    (activity as MainActivity?)?.appSettings()
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)?.closeAppSetting()
        }
    }

    fun filterSelection(filterItemSelection: String) {
        mCustomListDialog.dismiss()

        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)?.changeActionBarTitle(filterItemSelection.capitalize(Locale.ROOT))
        }

        if (filterItemSelection == Constants.ALL_ITEMS) {
            mFavDishViewModel.allDishesList.observe(viewLifecycleOwner) { dishes ->
                dishes.let {

                    if (it.isNotEmpty()) {
                        mBinding.rvDishesList.visibility = View.VISIBLE
                        mBinding.tvNoDishesAddedYet.visibility = View.GONE
                        mFavDishAdapter.dishesList(it)
                    } else {
                        mBinding.rvDishesList.visibility = View.GONE
                        mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
                        mBinding.tvNoDishesAddedYet.text = getString(R.string.list_of_dish_empty)
                    }
                }
            }
        } else {
            mFavDishViewModel.getFilteredList(filterItemSelection)
                .observe(viewLifecycleOwner) { dishes ->
                    dishes.let {
                        if (it.isNotEmpty()) {
                            mBinding.rvDishesList.visibility = View.VISIBLE
                            mBinding.tvNoDishesAddedYet.visibility = View.GONE
                            mFavDishAdapter.dishesList(it)
                        } else {
                            mBinding.rvDishesList.visibility = View.GONE
                            mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
                            mBinding.tvNoDishesAddedYet.text =
                                getString(R.string.label_no_dishes_added_yet, filterItemSelection)
                        }
                    }
                }
        }
    }

}
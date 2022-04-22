package com.skillbox.ascent.ui.fragments.sport_activities

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.skillbox.ascent.R
import com.skillbox.ascent.data.ascent.models.AscentUser
import com.skillbox.ascent.ui.fragments.sport_activities.adapter.ActivitiesAdapter
import com.skillbox.ascent.databinding.FragmentActivitiesBinding
import com.skillbox.ascent.di.UserDataPreferences
import com.skillbox.ascent.oauth_data.AuthConfig
import com.skillbox.ascent.ui.fragments.profile_fragment.ProfileFragment
import com.skillbox.ascent.ui.fragments.sport_activities.adapter.AscentLoaderStateAdapter
import com.skillbox.ascent.utils.autoCleared
import com.skillbox.ascent.utils.initSwipeToRefresh
import com.skillbox.ascent.utils.withArguments
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ActivitiesFragment : Fragment(R.layout.fragment_activities) {

    private val binding by viewBinding(FragmentActivitiesBinding::bind)

    private val viewModel: ActivitiesViewModel by viewModels()

    private var activityAdapter: ActivitiesAdapter by autoCleared()






    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navOptions = navigateWithAnimation()

        initList()
        bindViewModel()

        binding.swipeRefreshActivities.initSwipeToRefresh(activityAdapter)

        binding.addActivityButton.setOnClickListener {
            val action =
                ActivitiesFragmentDirections.actionActivitiesFragment2ToCreateActivityFragment()
            findNavController().navigate(action, navOptions)
        }


    }

    private fun navigateWithAnimation(): NavOptions {
        return NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setEnterAnim(R.anim.enter_anim)
            .setExitAnim(R.anim.exit_anim)
            .setPopEnterAnim(R.anim.pop_enter_anim)
            .setPopExitAnim(R.anim.pop_exit_anim)
            .build()
    }


    private fun initList() {
        activityAdapter = ActivitiesAdapter()
        with(binding.activitiesList) {
            adapter = activityAdapter
            adapter = activityAdapter.withLoadStateHeaderAndFooter(
                header = AscentLoaderStateAdapter(),
                footer = AscentLoaderStateAdapter()
            )
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
        activityAdapter.addLoadStateListener { state ->
            with(binding) {
                activitiesList.isVisible =
                    state.refresh != LoadState.Loading && (activityAdapter.itemCount != 0)
                progress.isVisible = state.refresh == LoadState.Loading
                noActivitiesAvailable.isVisible =
                    state.refresh != LoadState.Loading && activityAdapter.itemCount < 1
            }
        }
        activityAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) binding.activitiesList.scrollToPosition(0)
            }
        })
    }

    private fun bindViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.activityUIState.collectLatest { itemUIState ->
                    when (itemUIState) {
                        is ActivityItemUIState.Success -> {
                            activityAdapter.submitData(itemUIState.itemPagingData as PagingData)
                        }
                        is ActivityItemUIState.Error -> {
                        }
                    }

                }
            }
        }
    }


}
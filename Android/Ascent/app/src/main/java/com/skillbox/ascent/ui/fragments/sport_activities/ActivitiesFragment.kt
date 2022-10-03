package com.skillbox.ascent.ui.fragments.sport_activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.skillbox.ascent.R
import com.skillbox.ascent.data.ascent.models.ui_messages.ErrorUIMessage
import com.skillbox.ascent.data.ascent.models.ui_messages.UIMessage
import com.skillbox.ascent.data.ascent.models.ui_messages.WarningUIMessage
import com.skillbox.ascent.databinding.FragmentActivitiesBinding
import com.skillbox.ascent.di.networking.NetworkStatus
import com.skillbox.ascent.ui.fragments.sport_activities.adapter.ActivitiesAdapter
import com.skillbox.ascent.ui.fragments.sport_activities.adapter.AscentLoaderStateAdapter
import com.skillbox.ascent.utils.ImageOffsetDecoration
import com.skillbox.ascent.utils.autoCleared
import com.skillbox.ascent.utils.setAnimationTransit
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

@AndroidEntryPoint
class ActivitiesFragment : Fragment(R.layout.fragment_activities) {

    private val binding by viewBinding(FragmentActivitiesBinding::bind)

    private val viewModel: ActivitiesViewModel by viewModels()

    private val args: ActivitiesFragmentArgs by navArgs()

    private var activityAdapter: ActivitiesAdapter by autoCleared()

    private val handler = Handler(Looper.getMainLooper())

    private val animOptions = NavOptions.Builder().setAnimationTransit()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleOnBackPressed()
        initList()
        bindViewModel()
        manageNetworkState()
        hideFABonScrolling()



        viewModel.provideInitialNavigation {
            findNavController().navigate(
                ActivitiesFragmentDirections.actionActivitiesFragment2ToLoginFragment(),
                animOptions
            )
        }

        binding.closeNotification.setOnClickListener {
            binding.notifyLayout.visibility = View.GONE
        }


        binding.addActivityButton.setOnClickListener {
            summonBottomSheetDialog()
        }

        binding.closeNotification.setOnClickListener {
            binding.notifyLayout.visibility = View.GONE
        }

        viewModel.activitiesLoadSuccess.observe(viewLifecycleOwner) {
            binding.notifyLayout.visibility = View.VISIBLE
            showNotificationMessage(it)
        }


    }


    override fun onPause() {
        super.onPause()
        viewModel.cancelCheckNetworkJob()
    }

    private fun summonBottomSheetDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.layout_bottomsheet_dialog, null)
        val registerBtn = dialogView.findViewById<MaterialButton>(R.id.registerBtn)
        val recordBtn = dialogView.findViewById<MaterialButton>(R.id.recordBtn)
        registerBtn.setOnClickListener {
            val action =
                ActivitiesFragmentDirections.actionActivitiesFragment2ToCreateActivityFragment()
            findNavController().navigate(action, animOptions)
            dialog.dismiss()
        }
        recordBtn.setOnClickListener {
            val action = ActivitiesFragmentDirections.actionActivitiesFragment2ToStartActivityFragment()
            findNavController().navigate(action, animOptions)
            dialog.dismiss()
        }

        dialog.setContentView(dialogView)
        dialog.show()
    }

    private fun handleOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.cancelCheckNetworkJob()
                    findNavController().popBackStack()
                }
            })
    }


    private fun hideFABonScrolling() {
        binding.activitiesList.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            val fab = binding.addActivityButton
            val deltaY = scrollY - oldScrollY
            when {
                //двигаем кнопку вниз при скроллинге, но не сразу)
                deltaY > 20 && fab.isShown -> {
                    fab.translationY = max(
                        0f,
                        min(
                            4 * fab.height.toFloat(),
                            fab.translationY + deltaY
                        )
                    )
                    return@setOnScrollChangeListener
                }
                fab.translationY <= 0 -> {
                    //ограничитель, при достижении нулевой(первоначальной) отметки, чтобы не уползал вверх
                    fab.translationY = 0f
                    return@setOnScrollChangeListener
                }
                deltaY < 0 -> {
                    //двигаем кнопку вверх
                    fab.translationY = min(
                        fab.height.toFloat(),
                        min(
                            fab.height.toFloat(),
                            fab.translationY + deltaY
                        )
                    )
                    return@setOnScrollChangeListener
                }
            }
        }

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
            addItemDecoration(ImageOffsetDecoration(requireContext()))
            if (isVisible) viewModel.showUIMessage(args.isUploadSuccess?.isDataUploaded)

        }
        activityAdapter.addLoadStateListener { state ->
            handleLoading(state)
            handleErrorState(state)
        }
        activityAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) binding.activitiesList.scrollToPosition(0)
            }
        })

        with(binding.swipeRefreshLayout) {
            setOnRefreshListener {
                isRefreshing = false
                activityAdapter.refresh()
            }
        }


    }


    private fun handleLoading(state: CombinedLoadStates) {
        with(binding) {
            activitiesList.isVisible =
                state.refresh != LoadState.Loading && (activityAdapter.itemCount != 0)
            progress.isVisible = state.refresh == LoadState.Loading
            noActivitiesAvailable.isVisible =
                state.refresh != LoadState.Loading && activityAdapter.itemCount == 0
        }
    }

    private fun handleErrorState(state: CombinedLoadStates) {
        val errorState = state.source.append as? LoadState.Error
            ?: state.source.prepend as? LoadState.Error
            ?: state.append as? LoadState.Error
            ?: state.prepend as? LoadState.Error

        errorState?.let {
            showNotificationMessage(
                ErrorUIMessage(textRes = R.string.activityLoadFailedTxt)
            )
        }
    }

    private fun bindViewModel() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
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

    private fun manageNetworkState() {
        viewModel.checkNetworkConnections()
        viewModel.isConnected.observe(viewLifecycleOwner) { networkStatus ->

            when (networkStatus) {

                is NetworkStatus.ConnectSuccess -> handler.postDelayed({
                    if (this.isAdded) binding.notifyLayout.visibility = View.GONE
                }, 3000)

                is NetworkStatus.ConnectError -> showNotificationMessage(
                    WarningUIMessage(networkStatus.error)
                )
            }


        }
    }


    private fun showNotificationMessage(message: UIMessage) {
        with(binding) {
            notifyLayout.visibility = View.VISIBLE
            notificationMessage.setTextColor(resources.getColor(message.textColorRes, null))
            notifyCard.setCardBackgroundColor(
                resources.getColor(
                    message.backColorRes,
                    null
                )
            )
            notificationMessage.text = resources.getText(message.textRes)
            notificationIcon.setImageResource(message.pictureRes)
        }
    }


}
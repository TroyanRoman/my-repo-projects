package com.example.yourdrive

import android.os.Bundle
import androidx.lifecycle.ViewModelStoreOwner
import com.example.yourdrive.databinding.ActivityMainBinding
import com.github.johnnysc.coremvvm.presentation.BackPress
import com.github.johnnysc.coremvvm.presentation.FragmentFactory
import com.github.johnnysc.coremvvm.sl.ProvideViewModel

class MainActivity : BackPress.Activity<MainViewModel>(), ProvideViewModel {

    private lateinit var fragmentFactory: FragmentFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fragmentFactory = BaseFragmentFactory(R.id.mainContainer, supportFragmentManager)

        viewModel = provideViewModel(MainViewModel::class.java, this)
        viewModel.observeNavigation(this) { screen ->
            screen.show(R.id.mainContainer, supportFragmentManager)
        }
        viewModel.observeProgress(this) { visibility ->
            visibility.apply(binding.progressLayout)
        }
    }

    override fun <T : androidx.lifecycle.ViewModel> provideViewModel(
        clazz: Class<T>,
        owner: ViewModelStoreOwner
    ): T = (application as ProvideViewModel).provideViewModel(clazz, owner)
}
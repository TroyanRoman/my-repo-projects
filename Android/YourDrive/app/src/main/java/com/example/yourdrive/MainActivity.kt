package com.example.yourdrive

import android.os.Bundle
import com.example.yourdrive.core_add.BaseActivity
import com.example.yourdrive.databinding.ActivityMainBinding

class MainActivity : BaseActivity<MainViewModel.Base>() {

    override val viewModelClass: Class<MainViewModel.Base> = MainViewModel.Base::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.observe(this) { screen ->
            screen.show(R.id.mainContainer, supportFragmentManager)
        }
        if (savedInstanceState == null) {
            viewModel.init()
        }
    }

}
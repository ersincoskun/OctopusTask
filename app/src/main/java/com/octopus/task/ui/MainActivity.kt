package com.octopus.task.ui

import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.octopus.task.R
import com.octopus.task.base.BaseActivity
import com.octopus.task.databinding.ActivityMainBinding
import com.octopus.task.ui.fragment.SplashFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun onLayoutReady() {
        super.onLayoutReady()
        supportFragmentManager.commit {
            add<SplashFragment>(R.id.fragmentContainerView)
        }
    }
}
package com.octopus.task.ui.fragment

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.octopus.task.base.BaseFragment
import com.octopus.task.databinding.FragmentSplashBinding
import com.octopus.task.helpers.PreferencesHelper
import com.octopus.task.utils.remove
import com.octopus.task.utils.show
import com.octopus.task.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    @Inject
    lateinit var preferencesHelper: PreferencesHelper
    private val viewModel: SplashViewModel by viewModels()

    override fun subLivData() {
        super.subLivData()
        viewModel.isReadyToStart.observe(viewLifecycleOwner) { isReadyToStart ->
            if (isReadyToStart) {
                binding.pbSplash.remove()
                viewModel.stopRequestLoop()
                //navigate to media show fragment
            } else {
                if (!binding.tvId.isVisible) binding.tvId.show()
            }
        }
    }

    override fun onLayoutReady() {
        super.onLayoutReady()
        if (preferencesHelper.deviceId.isEmpty()) preferencesHelper.deviceId = viewModel.generateAlphaNumericId()
        binding.tvId.text = preferencesHelper.deviceId
        viewModel.startRequestLoop()
    }
}
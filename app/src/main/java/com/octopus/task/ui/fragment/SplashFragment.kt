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
    private val mViewModel: SplashViewModel by viewModels()

    override fun subLivData() {
        super.subLivData()
        mViewModel.isReadyToStart.observe(viewLifecycleOwner) { isReadyToStart ->
            if (isReadyToStart) {
                if (binding.pbSplash.isVisible) binding.pbSplash.remove()
                if (binding.tvId.isVisible) binding.tvId.remove()
                mViewModel.stopRequestLoop()
                //navigate to media show fragment
            } else {
                if (!binding.tvId.isVisible) binding.tvId.show()
            }
        }

        mViewModel.isTherePlaylist.observe(viewLifecycleOwner) { isTherePlaylist ->
            if (isTherePlaylist) {
                //navigateToMediaShow
            } else {
                mViewModel.startRequestLoop()
            }
        }
    }

    override fun onLayoutReady() {
        super.onLayoutReady()
        if (preferencesHelper.deviceId.isEmpty()) {
            preferencesHelper.deviceId = mViewModel.generateAlphaNumericId()
            mViewModel.startRequestLoop(isMustDeletePlaylist = true)
        } else {
            mViewModel.checkIsTherePlaylist()
        }
        binding.tvId.text = preferencesHelper.deviceId
    }
}
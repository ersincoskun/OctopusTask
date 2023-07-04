package com.octopus.task.ui.fragment

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.octopus.task.base.BaseFragment
import com.octopus.task.databinding.FragmentSplashBinding
import com.octopus.task.helpers.PreferencesHelper
import com.octopus.task.utils.remove
import com.octopus.task.utils.setWorkManager
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
                navigate(MediaShowFragment())
                setWorkManager(preferencesHelper, requireContext())
            } else {
                if (!binding.tvId.isVisible) binding.tvId.show()
                mViewModel.sendRequest()
            }
        }

        mViewModel.isTherePlaylist.observe(viewLifecycleOwner) { isTherePlaylist ->
            if (isTherePlaylist) {
                navigate(MediaShowFragment())
                setWorkManager(preferencesHelper, requireContext())
            } else {
                mViewModel.sendRequest()
            }
        }
    }

    override fun onLayoutReady() {
        super.onLayoutReady()
        if (preferencesHelper.deviceId.isEmpty()) {
            preferencesHelper.deviceId = mViewModel.generateAlphaNumericId()
            mViewModel.sendRequest(isMustDeletePlaylist = true)
        } else {
            mViewModel.checkIsTherePlaylist()
        }
        binding.tvId.text = preferencesHelper.deviceId
    }
}
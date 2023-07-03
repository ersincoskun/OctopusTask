package com.octopus.task.ui.fragment

import androidx.fragment.app.viewModels
import com.octopus.task.base.BaseFragment
import com.octopus.task.databinding.FragmentMediaShowBinding
import com.octopus.task.viewmodel.MediaShowViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MediaShowFragment : BaseFragment<FragmentMediaShowBinding>() {

    private val viewModel:MediaShowViewModel by viewModels()

    override fun subLivData() {
        super.subLivData()
        viewModel.playlist.observe(viewLifecycleOwner){

        }
    }

}
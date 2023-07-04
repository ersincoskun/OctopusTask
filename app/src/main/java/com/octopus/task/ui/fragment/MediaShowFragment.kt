package com.octopus.task.ui.fragment

import androidx.fragment.app.viewModels
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.octopus.task.base.BaseFragment
import com.octopus.task.databinding.FragmentMediaShowBinding
import com.octopus.task.viewmodel.MediaShowViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MediaShowFragment : BaseFragment<FragmentMediaShowBinding>() {

    private var exoPlayer: ExoPlayer? = null
    private val viewModel: MediaShowViewModel by viewModels()

    override fun assignObjects() {
        super.assignObjects()
        context?.let { safeContext ->
            exoPlayer = ExoPlayer.Builder(safeContext).build()
        }
    }

    override fun subLivData() {
        super.subLivData()
        viewModel.playlist.observe(viewLifecycleOwner) {

        }
    }

    override fun prepareUI() {
        super.prepareUI()
        exoPlayer?.let { safeExoPlayer ->
            binding.playerView.player = safeExoPlayer
        }

        val mediaItems = listOf<MediaItem>()
      /*  MediaItem.fromUri(Uri.fromFile(File(context.filesDir, "video1.mp4"))),
        MediaItem.fromUri(Uri.fromFile(File(context.filesDir, "video2.mp4")))*/

        val mediaSources = mediaItems.map {
            ProgressiveMediaSource.Factory(DefaultDataSource.Factory(requireContext())).createMediaSource(it)
        }

        val concatenatedSource = ConcatenatingMediaSource(*mediaSources.toTypedArray())
        exoPlayer?.setMediaSource(concatenatedSource)
        exoPlayer?.prepare()
        exoPlayer?.playWhenReady = true

        exoPlayer?.seekTo(2, C.TIME_UNSET)
    }

    override fun onLayoutReady() {
        super.onLayoutReady()

    }

    override fun onStopped() {
        super.onStopped()
        binding.playerView.player = null
        exoPlayer?.release()
        exoPlayer = null
    }

}
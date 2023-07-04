package com.octopus.task.ui.fragment

import android.graphics.Bitmap
import android.net.Uri
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.octopus.task.base.BaseFragment
import com.octopus.task.databinding.FragmentMediaShowBinding
import com.octopus.task.helpers.PreferencesHelper
import com.octopus.task.model.DataItem
import com.octopus.task.utils.Constants.VIDEO_TYPE
import com.octopus.task.utils.printErrorLog
import com.octopus.task.utils.remove
import com.octopus.task.utils.show
import com.octopus.task.viewmodel.MediaShowViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class MediaShowFragment : BaseFragment<FragmentMediaShowBinding>() {

    @Inject
    lateinit var preferencesHelper: PreferencesHelper
    private var mExoPlayer: ExoPlayer? = null
    private val mViewModel: MediaShowViewModel by viewModels()

    override fun subLivData() {
        super.subLivData()
        mViewModel.playlist.observe(viewLifecycleOwner) { playlist ->
            printErrorLog("playlist order: ${preferencesHelper.playlistOrder} video order: ${preferencesHelper.videoOrder}")
            val currentItem = playlist[preferencesHelper.playlistOrder]
            if (preferencesHelper.playlistOrder + 1 >= playlist.size) {
                preferencesHelper.playlistOrder = 0
            } else {
                preferencesHelper.playlistOrder = preferencesHelper.playlistOrder + 1
            }
            currentItem.end_date?.let { safeEndDate ->
                currentItem.start_date?.let { safeStartDate ->
                    if (mViewModel.isItemInInterval(safeStartDate, safeEndDate)) playMedia(currentItem, playlist)
                    else mViewModel.getPlaylistFromDb()
                } ?: kotlin.run {
                    playMedia(currentItem, playlist)
                }
            } ?: kotlin.run {
                playMedia(currentItem, playlist)
            }
        }
    }

    override fun onLayoutReady() {
        super.onLayoutReady()
        mViewModel.getPlaylistFromDb()
    }

    override fun onStopped() {
        super.onStopped()
        releasePlayer()
    }

    private fun playMedia(currentItem: DataItem, playlist: List<DataItem>) {
        if (currentItem.type == VIDEO_TYPE) {
            val videoCount = playlist.count { it.type == VIDEO_TYPE }
            binding.playerView.show()
            binding.imageView.remove()
            prepareExoPlayer(playlist, videoCount)
        } else {
            releasePlayer()
            binding.playerView.remove()
            binding.imageView.show()
            currentItem.name?.let { safeImageName ->
                loadImageByFileName(safeImageName)
            }
        }
        postRunnable({
            mViewModel.getPlaylistFromDb()
        }, 10 * 1000)
    }

    private fun playVideoByOrder(videoCount: Int) {
        mExoPlayer?.seekTo(preferencesHelper.videoOrder, C.TIME_UNSET)
        if (preferencesHelper.videoOrder + 1 >= videoCount) {
            preferencesHelper.videoOrder = 0
        } else {
            preferencesHelper.videoOrder = preferencesHelper.videoOrder + 1
        }
    }

    private fun prepareExoPlayer(playlist: List<DataItem>, videoCount: Int) {
        context?.let { safeContext ->
            mExoPlayer = ExoPlayer.Builder(safeContext).build()
            mExoPlayer?.let { safeExoPlayer ->
                binding.playerView.player = safeExoPlayer
                safeExoPlayer.addListener(object : Player.Listener {
                    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                        if (playbackState == Player.STATE_READY) {
                            playVideoByOrder(videoCount)
                        }
                    }
                })
            }
            val mediaItems = mutableListOf<MediaItem>()
            playlist.forEach { playlistItem ->
                if (playlistItem.type == VIDEO_TYPE) {
                    playlistItem.name?.let { safeName ->
                        mediaItems.add(MediaItem.fromUri(Uri.fromFile(File(safeContext.filesDir, safeName))))
                    }
                }
            }
            val mediaSources = mediaItems.map {
                ProgressiveMediaSource.Factory(DefaultDataSource.Factory(requireContext())).createMediaSource(it)
            }

            val concatenatedSource = ConcatenatingMediaSource(*mediaSources.toTypedArray())
            mExoPlayer?.setMediaSource(concatenatedSource)
            mExoPlayer?.prepare()
        }
    }

    private fun loadImageByFileName(fileName: String) {
        val file =
            File(requireContext().filesDir.toString() + "/MediaFiles/${fileName}")
        val uri = Uri.fromFile(file)
        Glide.with(requireActivity().applicationContext).asBitmap()
            .load(uri)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(object : BitmapImageViewTarget(binding.imageView) {
                override fun setResource(resource: Bitmap?) {
                    super.setResource(resource)
                }
            })
    }

    private fun releasePlayer() {
        binding.playerView.player = null
        mExoPlayer?.release()
        mExoPlayer = null
    }

}
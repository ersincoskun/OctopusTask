package com.octopus.task.ui.fragment

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.octopus.task.base.BaseFragment
import com.octopus.task.databinding.FragmentMediaShowBinding
import com.octopus.task.helpers.PreferencesHelper
import com.octopus.task.model.DataItem
import com.octopus.task.utils.Constants.VIDEO_TYPE
import com.octopus.task.utils.printErrorLog
import com.octopus.task.utils.remove
import com.octopus.task.utils.setFullScreen
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
            printErrorLog("playlist order: ${preferencesHelper.playlistOrder}")
            printErrorLog("playlist: $playlist")
            val currentItem = playlist[preferencesHelper.playlistOrder]
            printErrorLog("current item: $currentItem")
            currentItem.end_date?.let { safeEndDate ->
                currentItem.start_date?.let { safeStartDate ->
                    if (mViewModel.isItemInInterval(safeStartDate, safeEndDate)){
                        playMedia(currentItem, playlist)
                    }
                    else {
                        mViewModel.getPlaylistFromDb()
                    }
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

    override fun onResumed() {
        super.onResumed()
        requireActivity().setFullScreen()
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
            prepareExoPlayer(currentItem)
            val retriever = MediaMetadataRetriever()
            val videoPath = requireContext().filesDir.toString() + "/MediaFiles/${currentItem.name}"
            retriever.setDataSource(videoPath)
            val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val timeInMilliSec = time?.toLong()
            postRunnable({
                if (preferencesHelper.playlistOrder + 1 >= playlist.size) {
                    preferencesHelper.playlistOrder = 0
                } else {
                    preferencesHelper.playlistOrder = preferencesHelper.playlistOrder + 1
                }
                mViewModel.getPlaylistFromDb()
            }, timeInMilliSec ?: (10 * 1000))
        } else {
            releasePlayer()
            binding.playerView.remove()
            binding.imageView.show()
            currentItem.name?.let { safeImageName ->
                loadImageByFileName(safeImageName)
            }
            postRunnable({
                if (preferencesHelper.playlistOrder + 1 >= playlist.size) {
                    preferencesHelper.playlistOrder = 0
                } else {
                    preferencesHelper.playlistOrder = preferencesHelper.playlistOrder + 1
                }
                mViewModel.getPlaylistFromDb()
            }, 10 * 1000)
        }
    }

    private fun prepareExoPlayer(currentItem: DataItem) {
        context?.let { safeContext ->
            mExoPlayer = ExoPlayer.Builder(safeContext).build()
            mExoPlayer?.let { safeExoPlayer ->
                binding.playerView.player = safeExoPlayer
                val file =
                    File(requireContext().filesDir.toString() + "/MediaFiles/${currentItem.name}")
                val uri = Uri.fromFile(file)
                val mediaItem = MediaItem.fromUri(uri)
                safeExoPlayer.setMediaItem(mediaItem)
                safeExoPlayer.prepare()
                mExoPlayer?.playWhenReady = true
            }
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
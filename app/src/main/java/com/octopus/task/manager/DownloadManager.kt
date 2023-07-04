package com.octopus.task.manager

import android.content.Context
import com.octopus.task.utils.printErrorLog
import com.octopus.task.utils.printLog
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.*
import java.net.URL
import java.net.URLConnection
import javax.inject.Inject

class DownloadManager @Inject constructor(@ApplicationContext val context: Context) {
    fun downloadMedia(urlString: String?, mediaName: String?): DownloadResult {
        try {
            val url = URL(urlString)
            val urlConnection: URLConnection = url.openConnection()
            urlConnection.connect()

            val path: String =
                context.filesDir.toString() + "/MediaFiles"

            val dir = File(path)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val total: Int = urlConnection.contentLength
            var count: Int
            val input: InputStream = BufferedInputStream(url.openStream(), 8192)
            val output: OutputStream = FileOutputStream("$path/$mediaName")

            val data = ByteArray(4096)
            var current: Long = 0
            var progress = 0L
            var progressForCheck = 0L
            while (input.read(data).also { count = it } != -1) {
                current += count.toLong()
                progress = current * 100 / total
                if (progressForCheck != progress) {
                    progressForCheck = progress
                    printLog("downloading $progress")
                }
                output.write(data, 0, count)
            }

            output.flush()
            output.close()
            input.close()
            printErrorLog("Download process done")
            return DownloadResult.Successful
        } catch (e: Exception) {
            printErrorLog("Download exception: $e")
            return DownloadResult.Fail
        } catch (e: IOException) {
            printErrorLog("Download exception: $e")
            return DownloadResult.Fail
        }
    }

    sealed class DownloadResult {
        object Successful : DownloadResult()
        object Fail : DownloadResult()
    }
}
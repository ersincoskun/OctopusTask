package com.octopus.task.base

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.octopus.task.utils.printErrorLog

abstract class BaseUtilityFragment<T : ViewBinding?>: BaseTemplateFragment<T>() {

    private lateinit var mActivePostRunnableHandlers: ArrayList<Handler>

    override fun onCreated() {
        super.onCreated()
        printErrorLog("current fragment: $this")
        mActivePostRunnableHandlers = ArrayList()
    }

    override fun onDestroyed() {
        super.onDestroyed()
        mActivePostRunnableHandlers.forEach { handler -> handler.removeMessages(0) }
        mActivePostRunnableHandlers.clear()
    }

    fun showToast(message: String, duration: Int) {
        context?.let { safeContext ->
            Toast.makeText(safeContext, message, duration).show()
        }
    }

    fun showSnackBar(message: String, duration: Int = 2000) {
        view?.let { safeView ->
            Snackbar.make(safeView, message, duration).show()
        }
    }

    fun navigateBackStack() {
        Navigation.findNavController(binding.root).popBackStack()
    }

    fun navigate(resId: Int? = null, navDirections: NavDirections? = null) {
        resId?.let {
            Navigation.findNavController(binding.root).navigate(it)
        }
        navDirections?.let {
            Navigation.findNavController(binding.root).navigate(it)
        }
    }

    /*   fun showProgressForHomePage() {
           requireActivity().findViewById<ProgressBar>(R.id.mainPagePB).show()
           requireActivity().findViewById<View>(R.id.mainPageDarknessView).show()
       }

       fun hideProgressForHomePage() {
           requireActivity().findViewById<ProgressBar>(R.id.mainPagePB).remove()
           requireActivity().findViewById<View>(R.id.mainPageDarknessView).remove()
       }*/

    fun postRunnable(runnable: Runnable, delay: Long): Handler {
        val handler = Handler(Looper.getMainLooper()).apply {
            postDelayed({
                mActivePostRunnableHandlers.remove(this)
                runnable.run()
            }, delay)
        }
        mActivePostRunnableHandlers.add(handler)
        return handler
    }
}
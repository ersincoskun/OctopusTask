package com.octopus.task.base

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.octopus.task.R
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

    fun navigate(fragment: Fragment) {
        val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView, fragment)
        transaction.commit()
    }

    fun navigateBackStack() {
        this.fragmentManager?.popBackStack()
    }


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
package com.octopus.task.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

open class BaseTemplateFragment<VB : ViewBinding?>:Fragment() {

    val binding get() = mBinding!!
    private var mBinding: VB? = null

    private var vgRoot: ViewGroup? = null
    private var vgFragmentContainer: ViewGroup? = null

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vgFragmentContainer = container
        onCreated()
        onCreateWithSavedInstance(savedInstanceState)
        initialize()
        return vgRoot
    }

    private fun prepareBinding() {
        var genericSuperclass = javaClass.genericSuperclass
        while ((genericSuperclass is ParameterizedType).not()) {
            genericSuperclass = (genericSuperclass as Class<*>).genericSuperclass
        }
        val viewBindingClassType = (genericSuperclass as ParameterizedType).actualTypeArguments[0]
        val viewBindingClass = viewBindingClassType as Class<VB>
        val inflateMethod = viewBindingClass.getMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
        val layoutInflater = LayoutInflater.from(context)
        mBinding = inflateMethod.invoke(null, layoutInflater, vgFragmentContainer, false) as VB
        vgRoot = mBinding?.root as ViewGroup
    }

    private fun initialize() {
        prepareBinding()
        assignObjects()
        subLivData()
        setListeners()
        prepareUI()
        listenOnLayoutReady()
    }

    @CallSuper
    open fun subLivData() {}
    @CallSuper
    open fun assignObjects() {}
    @CallSuper
    open fun setListeners() {}
    @CallSuper
    open fun prepareUI() {}
    @CallSuper
    open fun onLayoutReady() {}
    @CallSuper
    open fun onCreateWithSavedInstance(savedInstanceState: Bundle?){}
    @CallSuper
    open fun onCreated() {}
    @CallSuper
    open fun onStarted() {}
    @CallSuper
    open fun onResumed() {}
    @CallSuper
    open fun onPaused() {}
    @CallSuper
    open fun onStopped() {}
    @CallSuper
    open fun onDestroyed() {}


    final override fun onStart() {
        super.onStart()
        onStarted()
    }

    final override fun onResume() {
        super.onResume()
        onResumed()
    }

    final override fun onPause() {
        super.onPause()
        onPaused()
    }

    final override fun onStop() {
        super.onStop()
        onStopped()
    }

    final override fun onDestroyView() {
        super.onDestroyView()
        onDestroyed()
    }

    final override fun onDestroy() {
        super.onDestroy()
    }

    private fun listenOnLayoutReady() {
        vgRoot?.run {
            viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    viewTreeObserver?.removeOnGlobalLayoutListener(this)
                    post { onLayoutReady() }
                }
            })
        }
    }

}
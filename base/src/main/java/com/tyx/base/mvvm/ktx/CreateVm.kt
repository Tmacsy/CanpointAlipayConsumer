package com.tyx.base.mvvm.ktx

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * activity扩展创建viewModel
 */
fun <C : ViewModel?> AppCompatActivity.createVM(vmClass: Class<C>): C {
    return ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(vmClass)
}

/**
 * fragment扩展 创建viewModel 与父activity viewModel共用
 */
fun <C : ViewModel?> Fragment.createVM(vmClass: Class<C>, activity: Context?): C? {
    if (activity != null && activity is AppCompatActivity) {
        return ViewModelProvider(activity, ViewModelProvider.NewInstanceFactory()).get(vmClass)
    }
    return null
}

/**
 * fragment扩展 创建viewModel
 */
fun <C : ViewModel?> Fragment.fragmentCreateVm(vmClass: Class<C>): C? {
    return ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(vmClass)
}
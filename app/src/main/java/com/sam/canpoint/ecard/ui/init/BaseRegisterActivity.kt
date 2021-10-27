package com.sam.canpoint.ecard.ui.init

import androidx.databinding.ViewDataBinding
import com.sam.canpoint.ecard.ui.presentation.BasePresentation
import com.sam.canpoint.ecard.ui.presentation.PresentationFactory
import com.tyx.base.mvvm.BaseMVVMActivity
import com.tyx.base.mvvm.BaseViewModel

abstract class BaseRegisterActivity<V : ViewDataBinding, M : BaseViewModel<*>> : BaseMVVMActivity<V, M>() {

    private var mPresentation: BasePresentation? = null

    override fun initView() {
        super.initView()
        showRegisterPresentation()
    }

    private fun showRegisterPresentation() {
        if (mPresentation != null) {
            mPresentation?.show()
            return
        }
        val factory = PresentationFactory.getFactory()
        mPresentation = factory.createPresentation(this, PresentationFactory.REGISTER)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresentation?.cancelPresentation()
    }
}
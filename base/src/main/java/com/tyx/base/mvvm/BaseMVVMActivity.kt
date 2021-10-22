package com.tyx.base.mvvm

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.sam.dialog.D.makeRotateLoadingDialog
import com.sam.system.toast.T
import com.tyx.base.BaseNetWorkActivity

abstract class BaseMVVMActivity<V : ViewDataBinding, M : BaseViewModel<*>> : BaseNetWorkActivity(),
    View.OnClickListener {

    protected lateinit var mBinding: V
    protected lateinit var mViewModel: M
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, getLayoutId())
        mViewModel = getViewModel()
        mBinding.run {
            setLifecycleOwner(this@BaseMVVMActivity)
            lifecycle.addObserver(mViewModel)
        }
        observer()
        initView()
    }

    @LayoutRes
    abstract fun getLayoutId(): Int

    abstract fun getViewModel(): M

    open fun initView() {

    }

    private fun observer() {
        mViewModel.viewChange.run {
            showToast.observe(this@BaseMVVMActivity, Observer {
                T.normal(this@BaseMVVMActivity, it ?: "").show()
            })
            showLoadingDialog.observe(this@BaseMVVMActivity, Observer {
                showDialog()
            })
            dismissDialog.observe(this@BaseMVVMActivity, Observer {
                if (this@BaseMVVMActivity::dialog.isInitialized && dialog.isShowing) {
                    dialog.dismiss()
                }
            })
        }
    }

    private fun showDialog() {
        if (!this::dialog.isInitialized) {
            dialog = makeRotateLoadingDialog(this).widthScale(0.5f)
        }
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this@BaseMVVMActivity::dialog.isInitialized && dialog.isShowing) dialog.dismiss()
    }

    override fun onClick(v: View?) {

    }
}
package com.sam.canpoint.ecard.ui.init

import android.view.View
import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.api.bean.GetStoreInfoResponse
import com.sam.canpoint.ecard.databinding.ActivityBindEcardAddressBinding
import com.sam.dialog.widget.ActionSheetDialog
import com.tyx.base.mvvm.BaseMVVMActivity
import com.tyx.base.mvvm.ktx.createVM

class BindAddressActivity : BaseMVVMActivity<ActivityBindEcardAddressBinding, BindAddressViewModel>() {
    override fun getLayoutId(): Int = R.layout.activity_bind_ecard_address
    override fun getViewModel(): BindAddressViewModel = createVM(BindAddressViewModel::class.java)
    private val storeInfo = ArrayList<GetStoreInfoResponse>()
    private val storeList = ArrayList<String>()
    private var storeDialog: ActionSheetDialog? = null

    override fun initView() {
        super.initView()
        mBinding.vm = mViewModel
        mBinding.tvNext.setOnClickListener(this)
        mBinding.tvBack.setOnClickListener(this)
        observer()
        mViewModel.getStoreInfo()
    }

    private fun observer() {

    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v?.id) {
            R.id.tv_next -> {

            }
            R.id.ll_build_store -> initBuildStore()
            R.id.tv_back -> finish()
        }
    }

    private fun initBuildStore() {
        if (storeInfo.isNullOrEmpty()) return
        if (storeDialog == null) {

        }
    }
}
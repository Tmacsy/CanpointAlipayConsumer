package com.sam.canpoint.ecard.ui.home

import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.databinding.ActivityHomeBinding
import com.tyx.base.mvvm.BaseMVVMActivity
import com.tyx.base.mvvm.ktx.createVM

class HomeActivity : BaseMVVMActivity<ActivityHomeBinding,HomeViewModel>() {
    override fun getLayoutId(): Int = R.layout.activity_home

    override fun getViewModel(): HomeViewModel = createVM(HomeViewModel::class.java)

    override fun initView() {
        super.initView()

    }
}
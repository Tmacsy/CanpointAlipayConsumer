package com.sam.canpoint.ecard.ui.init

import android.content.Intent
import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.databinding.ActivitySynDatasSucBinding
import com.sam.canpoint.ecard.ui.home.HomeActivity
import com.sam.canpoint.ecard.utils.Utils
import com.sam.canpoint.ecard.utils.sp.CanPointSp
import com.tyx.base.mvvm.BaseMVVMActivity
import com.tyx.base.mvvm.ktx.createVM

class SyncDataSucActivity : BaseMVVMActivity<ActivitySynDatasSucBinding, SyncDataSucViewModel>() {
    override fun getLayoutId(): Int = R.layout.activity_syn_datas_suc

    override fun getViewModel(): SyncDataSucViewModel = createVM(SyncDataSucViewModel::class.java)

    override fun initView() {
        super.initView()
        mBinding.tvBtn.setOnClickListener {
            Utils.resetFourRandomMinute()
            CanPointSp.appRunCount = CanPointSp.appRunCount + 1
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
package com.sam.canpoint.ecard.ui.init

import android.content.Context
import android.content.Intent
import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.databinding.ActivitySetSchoolCodeBinding
import com.tyx.base.mvvm.BaseMVVMActivity
import com.tyx.base.mvvm.ktx.createVM

class SyncDataActivity : BaseMVVMActivity<ActivitySetSchoolCodeBinding, SyncDataViewModel>() {
    override fun getLayoutId(): Int = R.layout.activity_syn_datas

    override fun getViewModel(): SyncDataViewModel = createVM(SyncDataViewModel::class.java)

    override fun initView() {
        super.initView()

    }

    companion object {
        private const val IS_AUTO = "isAuto"
        fun start(context: Context, isAuto: Boolean = false) {
            val intent = Intent(context, SyncDataActivity::class.java)
            context.startActivity(intent.apply {
                putExtra(IS_AUTO, isAuto)
            })
        }
    }
}
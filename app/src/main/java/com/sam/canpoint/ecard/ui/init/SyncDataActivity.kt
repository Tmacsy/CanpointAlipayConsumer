package com.sam.canpoint.ecard.ui.init

import android.content.Context
import android.content.Intent
import androidx.lifecycle.Observer
import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.databinding.ActivitySetSchoolCodeBinding
import com.sam.utils.app.ActivityUtils
import com.tyx.base.mvvm.BaseMVVMActivity
import com.tyx.base.mvvm.ktx.createVM

class SyncDataActivity : BaseMVVMActivity<ActivitySetSchoolCodeBinding, SyncDataViewModel>() {
    override fun getLayoutId(): Int = R.layout.activity_syn_datas
    override fun getViewModel(): SyncDataViewModel = createVM(SyncDataViewModel::class.java)
    private var isAuto: Boolean = false

    override fun initView() {
        super.initView()
        mViewModel.cleanDB()
        isAuto = intent.getBooleanExtra(IS_AUTO, false)
        observer()
        if (isAuto) {
            mViewModel.autoInitDevice()
        } else {
            //手动
            mViewModel.getData()
        }
    }

    private fun observer() {
        mViewModel.run {
            success.observe(this@SyncDataActivity, Observer {
                val intent = Intent(this@SyncDataActivity, SyncDataSucActivity::class.java)
                startActivity(intent)
                ActivityUtils.finishAllActivities()
            })
            error.observe(this@SyncDataActivity, Observer {
                viewChange.showToast.value = it
                val intent = Intent(this@SyncDataActivity, SyncDataFailedActivity::class.java)
                startActivity(intent.apply {
                    putExtra("isAuto", isAuto)
                    putExtra("errorStr", it)
                })
            })
        }
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
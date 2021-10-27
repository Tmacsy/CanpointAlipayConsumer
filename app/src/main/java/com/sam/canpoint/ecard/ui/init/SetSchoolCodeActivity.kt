package com.sam.canpoint.ecard.ui.init

import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.databinding.ActivitySetSchoolCodeBinding
import com.tyx.base.mvvm.BaseMVVMActivity
import com.tyx.base.mvvm.ktx.createVM

class SetSchoolCodeActivity : BaseRegisterActivity<ActivitySetSchoolCodeBinding, SetSchoolCodeViewModel>() {
    override fun getLayoutId(): Int {
        return R.layout.activity_set_school_code
    }

    override fun getViewModel(): SetSchoolCodeViewModel {
        return createVM(SetSchoolCodeViewModel::class.java)
    }

    override fun initView() {
        super.initView()
        mBinding.vm = mViewModel
        mBinding.tvNext.setOnClickListener(this)
        mBinding.tvBack.setOnClickListener(this)
        mViewModel.startClass.observe(this, Observer {
            val intent = Intent(this, it)
            startActivity(intent)
        })
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v?.id) {
            R.id.tv_back -> finish()
            R.id.tv_next -> mViewModel.getSchoolName()
        }
    }
}
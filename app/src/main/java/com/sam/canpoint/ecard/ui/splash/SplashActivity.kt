package com.sam.canpoint.ecard.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.databinding.ActivitySplashBinding
import com.tyx.base.mvvm.BaseMVVMActivity
import com.tyx.base.mvvm.ktx.createVM

class SplashActivity : BaseMVVMActivity<ActivitySplashBinding, SplashViewModel>() {
    override fun getViewModel(): SplashViewModel = createVM(SplashViewModel::class.java)

    override fun getLayoutId(): Int = R.layout.activity_splash

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.SplashStyle)
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        super.initView()
        mViewModel.initSplash()
        observer()
    }

    private fun observer() {
        mViewModel.run {
            startClass.observe(this@SplashActivity, Observer {
                val intent = Intent(this@SplashActivity, it)
                startActivity(intent)
            })
        }
    }

    override fun onStop() {
        super.onStop()
        finish()
    }
}
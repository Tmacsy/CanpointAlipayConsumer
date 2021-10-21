package com.sam.canpoint.ecard.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.databinding.ActivitySplashBinding
import com.sam.canpoint.ecard.presentation.BasePresentation
import com.sam.canpoint.ecard.presentation.PresentationFactory
import com.sam.canpoint.ecard.utils.Utils
import com.tyx.base.mvvm.BaseMVVMActivity
import com.tyx.base.mvvm.ktx.createVM

class SplashActivity : BaseMVVMActivity<ActivitySplashBinding, SplashViewModel>() {
    override fun getViewModel(): SplashViewModel = createVM(SplashViewModel::class.java)

    override fun getLayoutId(): Int = R.layout.activity_splash
    private var mPresentation: BasePresentation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.SplashStyle)
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        super.initView()
        showPresentation()
        mViewModel.initSplash()
        observer()
    }

    private fun observer() {
        mViewModel.run {
            startClass.observe(this@SplashActivity, Observer {
                Utils.resetFourRandomMinute()
                val intent = Intent(this@SplashActivity, it)
                startActivity(intent)
            })
        }
    }

    private fun showPresentation() {
        if (mPresentation != null) {
            mPresentation?.show()
            return
        }
        val factory = PresentationFactory.getFactory()
        mPresentation = factory.createPresentation(this, PresentationFactory.SPLASH);
    }

    override fun onStop() {
        super.onStop()
        mPresentation?.cancelPresentation()
        finish()
    }
}
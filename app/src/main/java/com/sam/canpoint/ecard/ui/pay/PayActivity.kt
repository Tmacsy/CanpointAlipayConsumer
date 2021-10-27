package com.sam.canpoint.ecard.ui.pay

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import androidx.lifecycle.Observer
import com.alipay.zoloz.smile2pay.verify.Smile2PayResponse
import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.databinding.ActivityAlipayResultBinding
import com.sam.canpoint.ecard.manager.IOTManager
import com.sam.canpoint.ecard.ui.presentation.BasePresentation
import com.sam.canpoint.ecard.ui.presentation.PresentationFactory
import com.tyx.base.mvvm.BaseMVVMActivity
import com.tyx.base.mvvm.ktx.createVM

class PayActivity : BaseMVVMActivity<ActivityAlipayResultBinding, PayViewModel>() {
    override fun getLayoutId(): Int = R.layout.activity_alipay_result
    override fun getViewModel(): PayViewModel = createVM(PayViewModel::class.java)
    private var mPresentation: BasePresentation? = null
    private var mAnimator: ObjectAnimator? = null

    override fun initView() {
        super.initView()
        mBinding.vm = mViewModel
        mBinding.click = this
        initData()
        observer()
    }

    private fun initData() {
        val smile2PayResponse: Smile2PayResponse = intent.getParcelableExtra(SMILE)
        val payPrice = intent.getStringExtra(PRICE)
        mViewModel.pay(smile2PayResponse, payPrice)
        startLoading()
    }

    private fun observer() {
        mViewModel.run {
            aliPayResult.observe(this@PayActivity, Observer { result ->
                IOTManager.get().showLed(if (result.payResultCode == Smile2PayResponse.CODE_SUCCESS) "blue" else "red", 2)
                mAnimator?.cancel()
                when (result.payResultCode) {
                    Smile2PayResponse.CODE_SUCCESS -> IOTManager.get().voice("ZFDZ", result.arriveAmount.toString())
                    1501 -> IOTManager.get().voice("e9") //初始化失败，请重启设备，后台定义的错误码
                    1502, -4 -> IOTManager.get().voice("insuffi_bal") //余额不足，后台定义的错误码 //限制消费，客户端本地脱机消费有笔欠款定义的错误码
                    -5 -> IOTManager.get().voice("restrict_consumption") //限制消费，客户端本地脱机消费脱机限次定义的错误码
                    else -> IOTManager.get().voice("f23")
                }
                showPresentation()
            })
        }
    }

    @SuppressLint("WrongConstant")
    private fun startLoading() {
        mAnimator = ObjectAnimator.ofFloat(mBinding.loadingIv, "rotation", 0f, 360f)
        mAnimator?.run {
            interpolator = LinearInterpolator()
            repeatMode = Animation.RESTART
            repeatCount = Animation.INFINITE
            addListener(onAnimationStart = {
                mBinding.loadingConsLt.visibility = View.VISIBLE
            }, onAnimationCancel = {
                mBinding.loadingCountTv.visibility = View.GONE
            }, onAnimationEnd = {
                mBinding.loadingCountTv.visibility = View.GONE
            })
            start()
        }
    }

    private fun showPresentation() {
        val factory = PresentationFactory.getFactory()
        mPresentation = factory.createPresentation(this, PresentationFactory.ALI_PAY_RESULT)
        mPresentation?.showPresentation()
    }

    override fun onNetWorkChange(state: Int) {
        mViewModel.netWorkState.value = state
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresentation?.cancelPresentation()
        mAnimator = null
        IOTManager.get().showLed("blue", 1)
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        finish()
    }

    companion object {
        private const val SMILE = "smile"
        private const val PRICE = "price"
        fun start(mContext: Context, smileResponse: Smile2PayResponse, price: String) {
            val intent = Intent(mContext, PayActivity::class.java)
            mContext.startActivity(intent.apply {
                putExtra(SMILE, smileResponse)
                putExtra(PRICE, price)
            })
        }
    }
}
package com.sam.canpoint.ecard.ui.refund

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import androidx.lifecycle.Observer
import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.api.bean.EventBean
import com.sam.canpoint.ecard.api.bean.RefundResult
import com.sam.canpoint.ecard.databinding.ActivityRefundResultBinding
import com.sam.canpoint.ecard.manager.IOTManager
import com.sam.canpoint.ecard.ui.pay.addListener
import com.sam.canpoint.ecard.ui.presentation.BasePresentation
import com.sam.canpoint.ecard.ui.presentation.PresentationFactory
import com.sam.canpoint.ecard.utils.presentationNetWorkImg
import com.sam.canpoint.ecard.utils.presenterNetWorkStr
import com.tyx.base.mvvm.BaseMVVMActivity
import com.tyx.base.mvvm.ktx.createVM
import org.greenrobot.eventbus.EventBus

class RefundActivity : BaseMVVMActivity<ActivityRefundResultBinding, RefundViewModel>() {
    override fun getLayoutId(): Int = R.layout.activity_refund_result
    override fun getViewModel(): RefundViewModel = createVM(RefundViewModel::class.java)
    private var mAnimator: ObjectAnimator? = null
    private var mPresentation: BasePresentation? = null

    override fun initView() {
        super.initView()
        initData()
        observer()
    }

    private fun initData() {
        val orderId = intent.getStringExtra(ORDER_ID)
        mViewModel.refund(orderId)
        startLoading()
    }

    private fun observer() {
        mViewModel.run {
            close.observe(this@RefundActivity, Observer {
                finish()
            })
            refundResult.observe(this@RefundActivity, Observer { result ->
                IOTManager.get().showLed(if (result.refundResultCode == RefundResult.CODE_SUCCESS) "blue" else "red", 2)
                IOTManager.get().voice(if (result.refundResultCode == RefundResult.CODE_SUCCESS) "f7" else "f8")
                mAnimator?.cancel()
                EventBus.getDefault().post(EventBean.ChangeLocalRecord(result.refundResultCode == RefundResult.CODE_SUCCESS))
                showPresentation()
                EventBus.getDefault().post(EventBean.CloseConfirmOrderActivity())
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
                mBinding.loadingConsLt.visibility = View.GONE
            }, onAnimationEnd = {
                mBinding.loadingConsLt.visibility = View.GONE
            })
            start()
        }
    }

    private fun showPresentation() {
        val factory = PresentationFactory.getFactory()
        mPresentation = factory.createPresentation(this, PresentationFactory.REFUND_RESULT)
        mPresentation?.showPresentation()
    }

    override fun onNetWorkChange(state: Int) {
        super.onNetWorkChange(state)
        mViewModel.netWorkState.value = state
        mPresentation?.showNetworkImg(state.presenterNetWorkStr(), state.presentationNetWorkImg())
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresentation?.cancelPresentation()
        mAnimator?.cancel()
    }

    companion object {
        private const val ORDER_ID = "orderId"
        fun start(mContext: Context, orderId: String) {
            val intent = Intent(mContext, RefundActivity::class.java)
            mContext.startActivity(intent.apply {
                putExtra(ORDER_ID, orderId)
            })
        }
    }
}
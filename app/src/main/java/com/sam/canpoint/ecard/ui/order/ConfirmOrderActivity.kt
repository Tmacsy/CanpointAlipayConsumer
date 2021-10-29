package com.sam.canpoint.ecard.ui.order

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.KeyEvent
import androidx.lifecycle.Observer
import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.api.bean.EventBean
import com.sam.canpoint.ecard.api.bean.OrderDetailResponse
import com.sam.canpoint.ecard.databinding.ActivityConfirmOrderBinding
import com.sam.canpoint.ecard.keyboard.IKeyBoardCallback
import com.sam.canpoint.ecard.keyboard.KeyBoardEvent
import com.sam.canpoint.ecard.keyboard.KeyBoardManager
import com.sam.canpoint.ecard.keyboard.KeyBoardType
import com.sam.canpoint.ecard.ui.presentation.BasePresentation
import com.sam.canpoint.ecard.ui.presentation.PresentationFactory
import com.sam.canpoint.ecard.ui.refund.RefundActivity
import com.sam.canpoint.ecard.utils.presentationNetWorkImg
import com.sam.canpoint.ecard.utils.presenterNetWorkStr
import com.tyx.base.mvvm.BaseMVVMActivity
import com.tyx.base.mvvm.ktx.createVM
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class ConfirmOrderActivity : BaseMVVMActivity<ActivityConfirmOrderBinding, ConfirmOrderViewModel>() {
    override fun getLayoutId(): Int = R.layout.activity_confirm_order
    override fun getViewModel(): ConfirmOrderViewModel = createVM(ConfirmOrderViewModel::class.java)
    private var mPresentation: BasePresentation? = null
    private val keyBoardManager = KeyBoardManager() //键盘管理

    override fun initView() {
        super.initView()
        EventBus.getDefault().register(this)
        mBinding.vm = mViewModel
        showPresentation()
        initData()
    }

    private fun initData() {
        val response = intent.getParcelableExtra<OrderDetailResponse>(DETAIL)
        response.consumeTime = response.consumeTime.substring(5)
        mViewModel.queryOrderResult.value = response
    }

    private fun showPresentation() {
        val factory = PresentationFactory.getFactory()
        mPresentation = factory.createPresentation(this, PresentationFactory.CONFIRM_ORDER)
        mPresentation?.showPresentation()
    }

    override fun onResume() {
        super.onResume()
        keyBoardManager.keyBoardCallback = keyBoardCallback
    }

    override fun onPause() {
        super.onPause()
        keyBoardManager.keyBoardCallback = null
    }

    @Subscribe
    fun closeConfirm(event: EventBean.CloseConfirmOrderActivity) {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresentation?.cancelPresentation()
    }

    private val keyBoardCallback = object : IKeyBoardCallback {
        override fun keyBoardEvent(event: KeyBoardEvent) {
            val interceptKeyEvent = mPresentation?.interceptKeyEvent(event) ?: false
            if (interceptKeyEvent) return
            if (event.type == KeyBoardType.CONFIRM) {
                val queryOrderResult = mViewModel.queryOrderResult.value ?: return
                if (TextUtils.isEmpty(queryOrderResult.orderId)) return
                RefundActivity.start(this@ConfirmOrderActivity, queryOrderResult.orderId)
            } else if (event.type == KeyBoardType.CANCEL) {
                finish()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return keyBoardManager.onKeyDown(keyCode, event)
    }

    override fun onNetWorkChange(state: Int) {
        mViewModel.netWorkState.value = state
        mPresentation?.showNetworkImg(state.presenterNetWorkStr(), state.presentationNetWorkImg())
    }

    companion object {
        private const val DETAIL = "detail"
        fun start(mContext: Context, orderDetail: OrderDetailResponse?) {
            val intent = Intent(mContext, ConfirmOrderActivity::class.java)
            intent.putExtra(DETAIL, orderDetail)
            mContext.startActivity(intent)
        }
    }
}
package com.sam.canpoint.ecard.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.databinding.PresentationStatisticsBinding
import com.sam.canpoint.ecard.keyboard.KeyBoardEvent
import com.sam.canpoint.ecard.keyboard.KeyBoardType
import com.sam.canpoint.ecard.ui.presentation.BasePresentation
import com.sam.canpoint.ecard.ui.presentation.PresentationFactory
import com.sam.canpoint.ecard.utils.presentationNetWorkImg
import com.sam.canpoint.ecard.utils.presenterNetWorkStr
import com.tyx.base.BaseNetWorkActivity
import com.tyx.base.mvvm.ktx.createVM
import java.lang.ref.WeakReference

class StatisticsPresentation(private val mContext: Context, display: Display) : BasePresentation(mContext, display) {

    private lateinit var mBinding: PresentationStatisticsBinding
    private lateinit var mViewModel: HomeViewModel
    private lateinit var mHandler: StatisticsHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.presentation_statistics, null, false)
        setContentView(mBinding.root)
        mViewModel = (mContext as HomeActivity).createVM(HomeViewModel::class.java)
        initView()
        initData()
    }

    private fun initView() {
        val netWorkState = mViewModel.netWorkState.value ?: BaseNetWorkActivity.NetworkStateDef.WIFI
        showNetworkImg(netWorkState.presenterNetWorkStr(), netWorkState.presentationNetWorkImg())
    }

    @SuppressLint("SetTextI18n")
    private fun initData() {
        mViewModel.getDeviceStatisticsByDay(success = { statisticsByDayResponse ->
            mBinding.tvRealIncome.text = statisticsByDayResponse.actualAmount.toString()
            mBinding.tvPayNumber.text = statisticsByDayResponse.paymentCount.toString()
            mBinding.tvPayTotalAmount.text = String.format("¥%s", statisticsByDayResponse.paymentAmount)
            mBinding.tvRefundNumber.text = statisticsByDayResponse.refundCount.toString()
            mBinding.tvRefundTotalAmount.text = String.format("¥%s", statisticsByDayResponse.refundAmount)
            mBinding.tvDebtNumber.text = String.format("¥%s", if (null == statisticsByDayResponse.debtAmount) "0" else statisticsByDayResponse.debtAmount)
            mBinding.tvArriveAmount.text = String.format("¥%s", if (null == statisticsByDayResponse.arriveAmount) "0" else statisticsByDayResponse.arriveAmount)
        }, error = {
            mBinding.ffQueryErrorTips.visibility = View.VISIBLE
            if (!this::mHandler.isInitialized) {
                mHandler = StatisticsHandler(this)
            }
            mHandler.removeMessages(1)
            mHandler.sendMessageDelayed(mHandler.obtainMessage(1), 3000)
        })
    }

    private class StatisticsHandler(mPresentation: StatisticsPresentation) : Handler(Looper.getMainLooper()) {
        private val presentation = WeakReference(mPresentation)
        override fun dispatchMessage(msg: Message?) {
            super.dispatchMessage(msg)
            val get = presentation.get()
            if (get != null && get.isShowing) {
                get.mBinding.ffQueryErrorTips.visibility = View.GONE
            }
        }
    }

    override fun interceptKeyEvent(event: KeyBoardEvent): Boolean {
        if (event.type == KeyBoardType.CANCEL) {
            cancelPresentation()
            mViewModel.showPresentation.value = PresentationFactory.FUNCTION
        }
        return true
    }

    override fun showNetworkImg(text: String, value: Int) {
        mBinding.tvNetStatus.text = text
        mBinding.ivNetStatus.setImageResource(value)
    }
}
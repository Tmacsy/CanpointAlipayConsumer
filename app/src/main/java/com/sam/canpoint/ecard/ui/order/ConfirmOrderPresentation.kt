package com.sam.canpoint.ecard.ui.order

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.api.bean.OrderDetailResponse
import com.sam.canpoint.ecard.databinding.PresentationConfirmOrderBinding
import com.sam.canpoint.ecard.keyboard.KeyBoardEvent
import com.sam.canpoint.ecard.keyboard.KeyBoardType
import com.sam.canpoint.ecard.ui.presentation.BasePresentation
import com.sam.canpoint.ecard.utils.presentationNetWorkImg
import com.sam.canpoint.ecard.utils.presenterNetWorkStr
import com.tyx.base.BaseNetWorkActivity
import com.tyx.base.mvvm.ktx.createVM
import java.lang.Exception

class ConfirmOrderPresentation(private val mContext: Context, display: Display) : BasePresentation(mContext, display) {

    private lateinit var mBinding: PresentationConfirmOrderBinding
    private lateinit var mViewModel: ConfirmOrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.presentation_confirm_order, null, false)
        setContentView(mBinding.root)
        mViewModel = (mContext as ConfirmOrderActivity).createVM(ConfirmOrderViewModel::class.java)
        initView()
        observer()
    }

    private fun initView() {
        val netWorkState = mViewModel.netWorkState.value ?: BaseNetWorkActivity.NetworkStateDef.WIFI
        showNetworkImg(netWorkState.presenterNetWorkStr(), netWorkState.presentationNetWorkImg())
    }

    @SuppressLint("SetTextI18n")
    private fun observer() {
        mViewModel.queryOrderResult.observe(mContext as LifecycleOwner, Observer {
            try {
                mBinding.tvOrderAmount.text = it.arriveAmount.toString()
                mBinding.tvOrderEnd.text = it.orderId.substring(it.orderId.length - 8)
                mBinding.tvAccount.text = it.userName
                mBinding.tvTime.text = it.consumeTime
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }

    override fun showNetworkImg(text: String, value: Int) {
        mBinding.tvNetStatus.text = text
        mBinding.ivNetStatus.setImageResource(value)
    }

    override fun interceptKeyEvent(event: KeyBoardEvent): Boolean {
        if (event.type == KeyBoardType.CONFIRM) {
            if (!mBinding.ffRefunding.isShown) {
                mBinding.llSureKey.visibility = View.GONE
                mBinding.ffRefunding.visibility = View.VISIBLE
            }
        }
        return super.interceptKeyEvent(event)
    }
}
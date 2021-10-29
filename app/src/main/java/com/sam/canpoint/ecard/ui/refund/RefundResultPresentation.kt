package com.sam.canpoint.ecard.ui.refund

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.api.bean.RefundResult
import com.sam.canpoint.ecard.databinding.PresentationRefundResultBinding
import com.sam.canpoint.ecard.ui.presentation.BasePresentation
import com.sam.canpoint.ecard.utils.presentationNetWorkImg
import com.sam.canpoint.ecard.utils.presenterNetWorkStr
import com.tyx.base.BaseNetWorkActivity
import com.tyx.base.mvvm.ktx.createVM
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class RefundResultPresentation(private val mContext: Context, display: Display) : BasePresentation(mContext, display) {

    private lateinit var mBinding: PresentationRefundResultBinding
    private lateinit var mViewModel: RefundViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.presentation_refund_result, null, false)
        setContentView(mBinding.root)
        mViewModel = (mContext as RefundActivity).createVM(RefundViewModel::class.java)
        initView()
        observer()
    }

    private fun initView() {
        val netWorkState = mViewModel.netWorkState.value ?: BaseNetWorkActivity.NetworkStateDef.WIFI
        showNetworkImg(netWorkState.presenterNetWorkStr(), netWorkState.presentationNetWorkImg())
    }

    private fun observer() {
        mViewModel.refundResult.observe(mContext as LifecycleOwner, Observer { refundResult ->
            try {
                mBinding.refundSuccessCl.visibility = if (refundResult.refundResultCode == RefundResult.CODE_SUCCESS) View.VISIBLE else View.GONE
                mBinding.refundErrorCl.visibility = if (refundResult.refundResultCode == RefundResult.CODE_SUCCESS) View.GONE else View.VISIBLE
                mBinding.refundResultErrorTv.visibility = if (refundResult.refundResultCode == RefundResult.CODE_SUCCESS) View.GONE else View.VISIBLE
                mBinding.refundResultErrorTv.text = refundResult.refundResultStr
                mBinding.refundResultTv.visibility = if (refundResult.refundResultCode == RefundResult.CODE_SUCCESS) View.GONE else View.VISIBLE
                mBinding.refundResultTv.text = refundResult.refundResultStr
                mBinding.refundPriceTv.text = java.lang.String.valueOf(refundResult.refundPrice)
                mBinding.orderEndTv.text = refundResult.orderId.substring(refundResult.orderId.length - 8)
                mBinding.accountTv.text = refundResult.refundName
                mBinding.refundTimeTv.text = if (TextUtils.isEmpty(refundResult.createTime)) SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(Date()) else refundResult.createTime
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }

    override fun showNetworkImg(text: String, value: Int) {
        mBinding.netStateTv.text = text
        mBinding.netStateIv.setImageResource(value)
    }
}
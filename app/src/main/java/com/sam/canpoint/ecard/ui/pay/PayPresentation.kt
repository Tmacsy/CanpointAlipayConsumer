package com.sam.canpoint.ecard.ui.pay

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.alipay.zoloz.smile2pay.verify.Smile2PayResponse
import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.databinding.PresentationAliPayResultBinding
import com.sam.canpoint.ecard.ui.presentation.BasePresentation
import com.sam.canpoint.ecard.utils.mobile
import com.sam.canpoint.ecard.utils.presentationNetWorkImg
import com.sam.canpoint.ecard.utils.presenterNetWorkStr
import com.sam.utils.network.NetworkUtils
import com.tyx.base.BaseNetWorkActivity
import com.tyx.base.mvvm.ktx.createVM
import java.text.SimpleDateFormat
import java.util.*

class PayPresentation(private val mContext: Context, display: Display) : BasePresentation(mContext, display) {

    private lateinit var mBinding: PresentationAliPayResultBinding
    private lateinit var mViewModel: PayViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.presentation_ali_pay_result, null, false)
        setContentView(mBinding.root)
        mViewModel = (mContext as PayActivity).createVM(PayViewModel::class.java)
        initView()
        observer()
    }

    private fun initView() {
        val netWorkState = mViewModel.netWorkState.value ?: BaseNetWorkActivity.NetworkStateDef.WIFI
        showNetworkImg(netWorkState.presenterNetWorkStr(), netWorkState.presentationNetWorkImg())
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun observer() {
        mViewModel.aliPayResult.observe(mContext as LifecycleOwner, Observer { aliPayResultBean ->
            if (!NetworkUtils.isNetAvailable()) mBinding.offlineStatusTv.visibility = View.VISIBLE
            else mBinding.offlineStatusTv.visibility = View.GONE
            mBinding.paySuccessGroup.visibility = if (aliPayResultBean.payResultCode == Smile2PayResponse.CODE_SUCCESS) View.VISIBLE else View.GONE
            mBinding.payStatusTv.text = if (aliPayResultBean.payResultCode == Smile2PayResponse.CODE_SUCCESS) "支付成功" else "支付失败"
            mBinding.payResultTv.visibility = if (aliPayResultBean.payResultCode == Smile2PayResponse.CODE_SUCCESS) View.GONE else View.VISIBLE
            mBinding.payResultIv.setImageResource(if (aliPayResultBean.payResultCode == Smile2PayResponse.CODE_SUCCESS) R.drawable.big_screen_home_ic_success else R.drawable.big_screen_home_ic_fail)
            mBinding.payResultTv.text = aliPayResultBean.payResultStr
            mBinding.payPriceTv.text = aliPayResultBean.arriveAmount.toString()
            mBinding.accountTv.text = aliPayResultBean.payName.mobile()
            mBinding.payTimeTv.text = SimpleDateFormat("MM-dd HH:mm").format(if (aliPayResultBean.createTime == 0L) Date() else aliPayResultBean.createTime)
        })
    }

    override fun showNetworkImg(text: String, value: Int) {
        mBinding.netStateTv.text = text
        mBinding.netStateIv.setImageResource(value)
    }
}
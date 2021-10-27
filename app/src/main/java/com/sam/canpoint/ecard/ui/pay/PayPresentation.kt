package com.sam.canpoint.ecard.ui.pay

import android.content.Context
import android.os.Bundle
import android.view.Display
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.databinding.PresentationAliPayResultBinding
import com.sam.canpoint.ecard.ui.presentation.BasePresentation
import com.sam.canpoint.ecard.utils.presentationNetWorkImg
import com.sam.canpoint.ecard.utils.presenterNetWorkStr
import com.tyx.base.BaseNetWorkActivity
import com.tyx.base.mvvm.ktx.createVM

class PayPresentation(private val mContext: Context, display: Display) : BasePresentation(mContext, display) {

    private lateinit var mBinding: PresentationAliPayResultBinding
    private lateinit var mViewModel: PayViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.presentation_ali_pay_result, null, false)
        setContentView(mBinding.root)
        mViewModel = (mContext as PayActivity).createVM(PayViewModel::class.java)

        observer()
    }

    private fun initView(){
        val netWorkState = mViewModel.netWorkState.value ?: BaseNetWorkActivity.NetworkStateDef.WIFI
        showNetworkImg(netWorkState.presenterNetWorkStr(), netWorkState.presentationNetWorkImg())
    }

    private fun observer() {

    }

    override fun showNetworkImg(text: String, value: Int) {
        mBinding.netStateTv.text = text
        mBinding.netStateIv.setImageResource(value)
    }
}
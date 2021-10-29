package com.sam.canpoint.ecard.ui.home

import android.content.Context
import android.os.Bundle
import android.view.Display
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.databinding.PresentationDebtSettingBinding
import com.sam.canpoint.ecard.keyboard.KeyBoardEvent
import com.sam.canpoint.ecard.keyboard.KeyBoardType
import com.sam.canpoint.ecard.ui.presentation.BasePresentation
import com.sam.canpoint.ecard.ui.presentation.PresentationFactory
import com.sam.canpoint.ecard.utils.CanPointSp
import com.sam.canpoint.ecard.utils.presentationNetWorkImg
import com.sam.canpoint.ecard.utils.presenterNetWorkStr
import com.tyx.base.BaseNetWorkActivity
import com.tyx.base.mvvm.ktx.createVM

class DebtSettingPresentation(private val mContext: Context, display: Display) : BasePresentation(mContext, display) {

    private lateinit var mBinding: PresentationDebtSettingBinding
    private lateinit var mViewModel: HomeViewModel
    private var debtCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.presentation_debt_setting, null, false)
        setContentView(mBinding.root)
        mViewModel = (mContext as HomeActivity).createVM(HomeViewModel::class.java)
        initView()
    }

    private fun initView() {
        val netWorkState = mViewModel.netWorkState.value ?: BaseNetWorkActivity.NetworkStateDef.WIFI
        showNetworkImg(netWorkState.presenterNetWorkStr(), netWorkState.presentationNetWorkImg())
    }

    override fun interceptKeyEvent(event: KeyBoardEvent): Boolean {
        when (event.type) {
            KeyBoardType.CANCEL -> {
                cancelPresentation()
                mViewModel.showPresentation.value = PresentationFactory.FUNCTION
            }
            KeyBoardType.UP -> {
                if (debtCount > 2) return true
                debtCount++
                setDebtCount(debtCount)
            }
            KeyBoardType.DOWN -> {
                if (debtCount < 2) return true
                debtCount--
                setDebtCount(debtCount)
            }
            KeyBoardType.CONFIRM -> {
                CanPointSp.debtCount = debtCount
                cancelPresentation()
                mViewModel.showPresentation.value = PresentationFactory.FUNCTION
            }
        }
        return true
    }

    override fun showNetworkImg(text: String, value: Int) {
        mBinding.tvNetStatus.text = text
        mBinding.ivNetStatus.setImageResource(value)
    }

    override fun show() {
        super.show()
        debtCount = CanPointSp.debtCount
        setDebtCount(debtCount)
    }

    private fun setDebtCount(debtCount: Int) {
        mBinding.tvDebtCount.text = debtCount.toString()
    }
}
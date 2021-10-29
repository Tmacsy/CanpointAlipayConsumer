package com.sam.canpoint.ecard.ui.home

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.databinding.PresentationCashRegisterPatternBinding
import com.sam.canpoint.ecard.keyboard.KeyBoardEvent
import com.sam.canpoint.ecard.keyboard.KeyBoardType
import com.sam.canpoint.ecard.manager.IOTManager
import com.sam.canpoint.ecard.ui.presentation.BasePresentation
import com.sam.canpoint.ecard.ui.presentation.PresentationFactory
import com.sam.canpoint.ecard.utils.CanPointSp
import com.sam.canpoint.ecard.utils.presentationNetWorkImg
import com.sam.canpoint.ecard.utils.presenterNetWorkStr
import com.tyx.base.BaseNetWorkActivity
import com.tyx.base.mvvm.ktx.createVM

class CashRegisterPatternPresentation(private val mContext: Context, display: Display) : BasePresentation(mContext, display) {

    private lateinit var mBinding: PresentationCashRegisterPatternBinding
    private lateinit var mViewModel: HomeViewModel
    private var maxPrice = 0.0
    private var isFreeSelected = false
    private var isQuotaSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.presentation_cash_register_pattern, null, false)
        setContentView(mBinding.root)
        mViewModel = (mContext as HomeActivity).createVM(HomeViewModel::class.java)
        initView()
        observer()
    }

    private fun initView() {
        maxPrice = CanPointSp.maxSingleConsume.toDouble()
        val netWorkState = mViewModel.netWorkState.value ?: BaseNetWorkActivity.NetworkStateDef.WIFI
        showNetworkImg(netWorkState.presenterNetWorkStr(), netWorkState.presentationNetWorkImg())
        //进来时的旧消费类型
        val oldPatternType: String = CanPointSp.patternType
        isFreeSelected = CanPointSp.FREE_MODE == oldPatternType
        isQuotaSelected = CanPointSp.FREE_MODE != oldPatternType
        updateButtonBg(oldPatternType)
        //进来时的旧定额
        val oldQuotaMoney: String = CanPointSp.quotaMoney
        mBinding.tvQuotaAmount.text = String.format("￥%s", oldQuotaMoney)
        setOnCancelListener {
            //退出时的新消费类型
            val newPatternType: String = CanPointSp.patternType
            isFreeSelected = CanPointSp.FREE_MODE == newPatternType
            isQuotaSelected = CanPointSp.FREE_MODE != newPatternType
            updateButtonBg(newPatternType)
        }
    }

    private fun observer() {
        mViewModel.run {
            inputPrice.observe(mContext as HomeActivity, Observer { price ->
                mBinding.inputEd.setText(price)
                if (TextUtils.isEmpty(price)) return@Observer
                mBinding.inputEd.setSelection(price.length)
                mBinding.inputEd.textSize = if (price.length > 5) 60f else 76f
                mBinding.inputEd.setTextColor(if (price.toDouble() > maxPrice) ContextCompat.getColor(mContext, R.color.text_failed) else ContextCompat.getColor(mContext, R.color.text_333))
            })
        }
    }

    override fun interceptKeyEvent(event: KeyBoardEvent): Boolean {
        when (event.type) {
            KeyBoardType.NUM -> {
                if (!mBinding.inputAmountCl.isShown) return true
                val num = event.num
                if (num.isNullOrEmpty()) return true
                IOTManager.get().voice(if (num == ".") "e18" else "e24")
                mViewModel.initInputPrice(num)
            }
            KeyBoardType.DELETE -> {
                if (!mBinding.inputAmountCl.isShown) return true
                var price = mViewModel.inputPrice.value
                if (!price.isNullOrEmpty()) {
                    price = price.substring(0, price.length - 1)
                    mViewModel.inputPrice.value = price
                }
            }
            KeyBoardType.DOWN -> {
                if (mBinding.inputAmountCl.isShown) return true
                if (!isQuotaSelected) {
                    isFreeSelected = false
                    isQuotaSelected = true
                    updateButtonBg(CanPointSp.QUOTA_MODE)
                }
            }
            KeyBoardType.UP -> {
                if (mBinding.inputAmountCl.isShown) return true
                if (!isFreeSelected) {
                    isFreeSelected = true
                    isQuotaSelected = false
                    updateButtonBg(CanPointSp.FREE_MODE)
                }
            }
            KeyBoardType.CONFIRM -> {
                if (isFreeSelected) {
                    updateSelectType(CanPointSp.FREE_MODE)
                    return true
                }
                if (mBinding.inputAmountCl.isShown) {
                    val price = mViewModel.inputPrice.value
                    if (price.isNullOrEmpty()) return true
                    try {
                        if (price.toDouble() <= 0 || price.toDouble() > maxPrice) return false
                    } catch (e: Exception) {
                        return true
                    }
                    updateSelectType(CanPointSp.QUOTA_MODE)
                }
            }
            KeyBoardType.CANCEL -> {
                if (mBinding.inputAmountCl.isShown) {
                    mViewModel.inputPrice.value = ""
                    mBinding.inputAmountCl.visibility = View.GONE
                    mBinding.inputEd.setText("")
                    return true
                }
                mViewModel.showPresentation.value = PresentationFactory.FUNCTION
                cancelPresentation()
            }
        }
        return true
    }

    private fun updateSelectType(type: String) {
        if (type == CanPointSp.patternType) return
        mBinding.progress.visibility = View.VISIBLE
        mViewModel.changeDeviceBind(type, success = {
            mBinding.progress.visibility = View.GONE
            CanPointSp.patternType = type
            mViewModel.presentationMode.value = type.toInt()
            updateButtonBg(type)
            if (type == CanPointSp.QUOTA_MODE) {
                if (mBinding.tv5.isShown) {
                    mBinding.tvQuotaAmount.visibility = View.VISIBLE
                    mBinding.tv5.visibility = View.GONE
                }
                mBinding.inputAmountCl.visibility = View.GONE
                mBinding.inputEd.setText("")
                mViewModel.inputPrice.value = ""
            }
        }, error = {
            mBinding.progress.visibility = View.GONE
        })
    }

    private fun updateButtonBg(patternType: String) {
        mBinding.rlFree.setBackgroundResource(if (patternType == CanPointSp.FREE_MODE) R.drawable.free_schema_bg else R.drawable.free_schema_no_bg)
        mBinding.rlQuota.setBackgroundResource(if (patternType == CanPointSp.FREE_MODE) R.drawable.fixed_model_no_bg else R.drawable.fixed_model_bg)
        mBinding.tv7.setBackgroundResource(if (patternType == CanPointSp.FREE_MODE) R.drawable.shape_blue_sure_bg else R.drawable.shape_white_stroke_bg)
        mBinding.ivFreeSelect.setImageResource(if (isFreeSelected) R.drawable.icon_select else R.drawable.icon_select2)
        mBinding.ivQuotaSelect.setImageResource(if (isQuotaSelected) R.drawable.icon_select else R.drawable.icon_select2)
        mBinding.ivFreeSelect.visibility = if (CanPointSp.FREE_MODE == CanPointSp.patternType) View.VISIBLE else View.GONE
        mBinding.ivQuotaSelect.visibility = if (CanPointSp.FREE_MODE == CanPointSp.patternType) View.GONE else View.VISIBLE
        mBinding.tv1.setTextColor(if (patternType == CanPointSp.FREE_MODE) ContextCompat.getColor(mContext, R.color.white) else ContextCompat.getColor(mContext, R.color.text_up))
        mBinding.tv2.setTextColor(if (patternType == CanPointSp.FREE_MODE) ContextCompat.getColor(mContext, R.color.text_white_80) else ContextCompat.getColor(mContext, R.color.text_333))
        mBinding.tv3.setTextColor(if (patternType == CanPointSp.FREE_MODE) ContextCompat.getColor(mContext, R.color.text_white_80) else ContextCompat.getColor(mContext, R.color.text_333))
        mBinding.tv4.setTextColor(if (patternType == CanPointSp.FREE_MODE) ContextCompat.getColor(mContext, R.color.text_up) else ContextCompat.getColor(mContext, R.color.white))
        mBinding.tv5.setTextColor(if (patternType == CanPointSp.FREE_MODE) ContextCompat.getColor(mContext, R.color.text_333) else ContextCompat.getColor(mContext, R.color.text_white_80))
        mBinding.tv6.setTextColor(if (patternType == CanPointSp.FREE_MODE) ContextCompat.getColor(mContext, R.color.text_333) else ContextCompat.getColor(mContext, R.color.text_white_80))
        mBinding.tv8.setTextColor(if (patternType == CanPointSp.FREE_MODE) ContextCompat.getColor(mContext, R.color.text_333) else ContextCompat.getColor(mContext, R.color.text_white_80))
        mBinding.tv5.visibility = if (TextUtils.isEmpty(CanPointSp.quotaMoney)) View.VISIBLE else View.GONE
        mBinding.tvQuotaAmount.visibility = if (TextUtils.isEmpty(CanPointSp.quotaMoney)) View.GONE else View.VISIBLE
    }

    override fun showNetworkImg(text: String, value: Int) {
        mBinding.tvNetStatus.text = text
        mBinding.ivNetStatus.setImageResource(value)
    }
}
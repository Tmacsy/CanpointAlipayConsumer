package com.sam.canpoint.ecard.ui.home

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.api.request.ConsumptionLocalRecordsRequest
import com.sam.canpoint.ecard.databinding.PresentationInputAmountPriceBinding
import com.sam.canpoint.ecard.keyboard.KeyBoardEvent
import com.sam.canpoint.ecard.keyboard.KeyBoardType
import com.sam.canpoint.ecard.manager.IOTManager
import com.sam.canpoint.ecard.ui.presentation.BasePresentation
import com.sam.canpoint.ecard.utils.presentationNetWorkImg
import com.sam.canpoint.ecard.utils.presenterNetWorkStr
import com.sam.canpoint.ecard.utils.CanPointSp
import com.sam.canpoint.ecard.utils.checkPrice
import com.sam.db.SamDBManager
import com.sam.db.info.WhereInfo
import com.sam.utils.device.DeviceUtils
import com.tyx.base.BaseNetWorkActivity
import com.tyx.base.mvvm.ktx.createVM
import java.math.BigDecimal

class InputAmountPresentation(private val mContext: Context, display: Display) :
        BasePresentation(mContext, display) {

    private lateinit var mBinding: PresentationInputAmountPriceBinding
    private lateinit var mViewModel: HomeViewModel
    private val maxPrice = CanPointSp.maxSingleConsume

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.presentation_input_amount_price, null, false)
        setContentView(mBinding.root)
        mViewModel = (mContext as AppCompatActivity).createVM(HomeViewModel::class.java)
        observer()
        initView()
    }

    private fun initView() {
        mBinding.snTv.text = String.format("SN:%s", DeviceUtils.getAndroidID())
        val netWorkState = mViewModel.netWorkState.value ?: BaseNetWorkActivity.NetworkStateDef.WIFI
        showNetworkImg(netWorkState.presenterNetWorkStr(), netWorkState.presentationNetWorkImg())
    }

    private fun observer() {
        mViewModel.run {
            inputPrice.observe(mContext as HomeActivity, Observer {
                try {
                    val max = maxPrice.toDouble()
                    mBinding.inputEd.setText(it)
                    if (it.isNullOrEmpty()) {
                        mBinding.snTv.visibility = View.VISIBLE
                        mBinding.groupTv.visibility = View.GONE
                        mBinding.errorPriceTv.visibility = View.GONE
                        return@Observer
                    }
                    mBinding.snTv.visibility = View.GONE
                    mBinding.inputEd.setSelection(it.length)
                    mBinding.inputEd.textSize = if (it.length > 5) 60f else 76f
                    if (it.toDouble() > max) {
                        mBinding.inputEd.setTextColor(ContextCompat.getColor(mContext, R.color.text_failed))
                        mBinding.errorPriceTv.visibility = View.VISIBLE
                        mBinding.groupTv.visibility = View.GONE
                    } else {
                        mBinding.inputEd.setTextColor(ContextCompat.getColor(mContext, R.color.text_333))
                        mBinding.errorPriceTv.visibility = View.GONE
                        mBinding.groupTv.visibility = View.VISIBLE
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
            addPrice.observe(mContext, Observer { price ->
                mBinding.addTv.visibility =
                        if (TextUtils.isEmpty(price)) View.GONE else View.VISIBLE
                if (!TextUtils.isEmpty(price)) "${mViewModel.initialPrice}${price}".also {
                    mBinding.addTv.text = it
                }
                computeAddPrice(price)
            })
            showCancelView.observe(mContext, Observer { type ->
                when (type) {
                    0 -> mBinding.cancelCl.visibility = View.GONE
                    1 -> {  //取消弹窗
                        mBinding.cancelCl.visibility = View.VISIBLE
                        mBinding.tv12.text = "键返回"
                        mBinding.tvPopContext.textSize = 32f
                        "交易已取消\n请重新发起收款".also { mBinding.tvPopContext.text = it }
                    }
                    2 -> { //提示消费
                        mBinding.cancelCl.visibility = View.VISIBLE
                        mBinding.tvPopContext.textSize = 32f
                        mBinding.tv12.text = "键继续"
                        "脱机消费已达到${CanPointSp.offlinePopupNum}笔,请联系运维人员".also {
                            mBinding.tvPopContext.text = it
                        }
                    }
                    3 -> {
                        mBinding.cancelCl.visibility = View.VISIBLE
                        mBinding.tvPopContext.textSize = 26f
                        mBinding.tv12.text = "键返回"
                        "脱机消费已达到${CanPointSp.offlineStopNum}笔,设备已停止使用，请联系运维人员".also {
                            mBinding.tvPopContext.text = it
                        }
                    }
                }
            })
            presentationMode.observe(mContext, Observer {
                when (it) {
                    FREE_MODE_1 -> {
                        mBinding.skTv.text = "请输入金额："
                        mBinding.waitPayCy.visibility = View.GONE
                        showPromptStatement(true)
                    }
                    FREE_MODE_2 -> {
                        mBinding.skTv.text = "请输入金额："
                        mBinding.waitPayCy.visibility = View.VISIBLE
                        mBinding.waitPayPriceTv.text = mViewModel.inputPrice.value
                        showPromptStatement(true)
                    }
                    QUOTA_MODE_1 -> {
                        mBinding.skTv.text = "固定金额收款"
                        mBinding.waitPayCy.visibility = View.GONE
                        showPromptStatement(true)
                        mBinding.inputEd.isFocusable = true
                    }
                    QUOTA_MODE_2 -> {
                        mBinding.skTv.text = "固定金额收款"
                        mBinding.waitPayCy.visibility = View.GONE
                        mBinding.inputEd.setText(mViewModel.inputPrice.value)
                        showPromptStatement(false)
                        mBinding.inputEd.isFocusable = false
                    }
                }
            })
        }
    }

    private fun showPromptStatement(isDefault: Boolean) {
        if (isDefault) {
            if (mBinding.tv1.text != "确认") {
                mBinding.tv.text = "按"
                mBinding.tv1.text = "确认"
                mBinding.tv1.isEnabled = true
                mBinding.tv3.text = "键收款"
            }
        } else {
            if (mBinding.tv1.text != "取消") {
                mBinding.tv.text = "按"
                mBinding.tv1.text = "取消"
                mBinding.tv1.isEnabled = false
                mBinding.tv3.text = "键返回首页"
            }
        }
    }

    override fun showNetworkImg(text: String, value: Int) {
        mBinding.netStateTv.text = text
        mBinding.netStateIv.setImageResource(value)
    }

    override fun interceptKeyEvent(event: KeyBoardEvent): Boolean {
        val mode = mViewModel.presentationMode.value
        val showCancelView = mViewModel.showCancelView.value ?: 0
        //正在刷脸中 拦截除取消和确认外的事件
        if ((mode == QUOTA_MODE_2 || mode == FREE_MODE_2) && event.type != KeyBoardType.CANCEL && event.type != KeyBoardType.CONFIRM) return true
        //显示弹窗后 只有确认可响应
        if (showCancelView != 0 && event.type != KeyBoardType.CONFIRM) return true
        when (event.type) {
            KeyBoardType.NUM -> {
                voiceKeyBoardNum(event.num)
                initInput(event.num)
                return true
            }
            KeyBoardType.DELETE -> {
                initInput(NUM_DEL)
                return true
            }
            KeyBoardType.ADD -> {
                voiceKeyBoardNum(NUM_ADD)
                initInput(NUM_ADD)
                return true
            }
            KeyBoardType.CANCEL -> {
                if (showCancelView != 0) return true
                val presentationMode = mViewModel.presentationMode.value
                if (presentationMode == FREE_MODE_2 || presentationMode == QUOTA_MODE_2) {
                    mViewModel.showCancelView.value = 1
                    return true
                }
                if (!TextUtils.isEmpty(mViewModel.addPrice.value)) {
                    mViewModel.addPrice.value = ""
                } else {
                    mViewModel.inputPrice.value = ""
                }
            }
            KeyBoardType.CONFIRM -> {
                //如果当前显示的是提示消费或者未显示弹窗  直接放行
                if (showCancelView != 0) mViewModel.showCancelView.value = 0
                if (showCancelView == 1 || showCancelView == 3) {
                    mViewModel.inputPrice.value = ""
                    mViewModel.addPrice.value = ""
                    if (mode == FREE_MODE_2) mViewModel.presentationMode.value = FREE_MODE_1
                    else if (mode == QUOTA_MODE_2) mViewModel.presentationMode.value = QUOTA_MODE_1
                    return true
                } else if (showCancelView == 2) {
                    return false
                }
                val offlinePopupNum = CanPointSp.offlinePopupNum
                val offlineStopNum = CanPointSp.offlineStopNum
                val offlineRecordCount = SamDBManager.getInstance().dao(ConsumptionLocalRecordsRequest::class.java)
                        .countOf(WhereInfo.get().equal(ConsumptionLocalRecordsRequest._isSynchronized, false))
                if (offlineRecordCount >= offlinePopupNum) {
                    val price = mViewModel.inputPrice.value ?: ""
                    if (price.checkPrice()) {
                        mViewModel.showCancelView.value = if (offlineRecordCount >= offlineStopNum) 3 else 2
                    }
                    return true
                }
                return false
            }
            else -> super.interceptKeyEvent(event)
        }
        return super.interceptKeyEvent(event)
    }

    private fun voiceKeyBoardNum(num: String?) {
        if (num.isNullOrEmpty()) return
        when (num) {
            "." -> IOTManager.get().voice("e18")
            NUM_ADD -> IOTManager.get().voice("e19")
            else -> IOTManager.get().voice("e24", num)
        }
    }

    private fun initInput(num: String?) {
        if (num.isNullOrEmpty()) return
        when (num) {
            NUM_DEL -> initDel()
            else -> initInputPrice(num)
        }
    }

    private fun initDel() {
        try {
            val addPrice = mViewModel.addPrice.value
            if (!TextUtils.isEmpty(addPrice)) {
                mViewModel.addPrice.value = addPrice?.substring(0, addPrice.length - (if (addPrice.endsWith(NUM_ADD)) 3 else 1))
            } else {
                var value = mViewModel.inputPrice.value
                if (!TextUtils.isEmpty(value)) {
                    value = value?.substring(0, value.length - 1)
                }
                mViewModel.inputPrice.value = value
                mViewModel.initialPrice = if (TextUtils.isEmpty(value)) "0" else value ?: "0"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initInputPrice(num: String) {
        try {
            val add = mViewModel.addPrice.value ?: ""
            if (!TextUtils.isEmpty(add) || num == NUM_ADD) {
                //加号运算
                initAddInput(num)
                return
            }
            mViewModel.initInputPrice(num)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initAddInput(num: String) {
        try {
            var inputNum = num
            val price = mViewModel.inputPrice.value ?: ""
            var addValue = mViewModel.addPrice.value ?: ""
            val isPoint = inputNum == "."
            if (addValue.endsWith(" + ") && inputNum == " + " && !TextUtils.isEmpty(addValue)) return
            if (isPoint && addValue.endsWith(".")) return
            var afterLastAddString = ""
            if (addValue.contains(" + ")) {
                afterLastAddString = addValue.substring(addValue.lastIndexOf(NUM_ADD) + 3)
            }
            //排除非0开头的00连续出现，但100可以出现
            if (inputNum == "0" && addValue.endsWith("0") && afterLastAddString.startsWith("0")) return
            if (inputNum == NUM_ADD && addValue.endsWith(".")) return
            if (inputNum == NUM_ADD && price.endsWith(".")) return
            if (inputNum == "." && addValue.endsWith(NUM_ADD)) inputNum = "0."
            if (!TextUtils.isEmpty(price) && BigDecimal(price).compareTo(BigDecimal(maxPrice)) == 1) return
            addValue += inputNum
            val numArray = addValue.split(NUM_ADD).toTypedArray()
            if (numArray.size > 16) return
            val lastNum = numArray[numArray.size - 1]
            if (!addValue.endsWith(NUM_ADD)) {
                if (lastNum.contains(".") && !lastNum.endsWith(".")) {
                    val split = lastNum.split("\\.".toRegex()).toTypedArray()
                    if (split[1].length > 2 || split[0].length > 3) return
                } else {
                    if (lastNum.toDouble() > 200 && !isPoint) return
                }
            }
            mViewModel.addPrice.value = addValue
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun computeAddPrice(addStr: String) {
        val inputPrice = mViewModel.inputPrice.value ?: ""
        val addValue = if (TextUtils.isEmpty(addStr)) "0" else addStr
        var addPrice = "0" //累计金额
        if (TextUtils.isEmpty(inputPrice)) mViewModel.initialPrice = "0"
        val inputPriceValue = mViewModel.initialPrice
        try {
            if (TextUtils.isEmpty(addValue)) return
            val addArray = addValue.split(NUM_ADD).toTypedArray()
            val iterator = addArray.iterator()
            while (iterator.hasNext()) {
                val next = iterator.next()
                if (TextUtils.isEmpty(next)) continue
                addPrice = BigDecimal(addPrice).add(BigDecimal(next)).toString()
            }
            val toString = BigDecimal(inputPriceValue).add(BigDecimal(addPrice)).toString()
            mViewModel.inputPrice.value = if (toString == "0") "" else toString
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val NUM_ADD = " + "
        private const val NUM_DEL = "del"
        const val FREE_MODE_1 = 1 //自由模式
        const val FREE_MODE_2 = 3 //自由模式 等待付款
        const val QUOTA_MODE_1 = 2 //定额模式 等待输入金额
        const val QUOTA_MODE_2 = 4 //定额模式 正在刷脸
    }
}
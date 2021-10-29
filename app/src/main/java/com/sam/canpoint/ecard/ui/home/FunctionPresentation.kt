package com.sam.canpoint.ecard.ui.home

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.Display
import android.view.LayoutInflater
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.api.bean.FunctionBean
import com.sam.canpoint.ecard.databinding.PresentationFunctionBinding
import com.sam.canpoint.ecard.keyboard.KeyBoardEvent
import com.sam.canpoint.ecard.keyboard.KeyBoardType
import com.sam.canpoint.ecard.ui.device.DeviceActivity
import com.sam.canpoint.ecard.ui.presentation.BasePresentation
import com.sam.canpoint.ecard.ui.presentation.PresentationFactory
import com.sam.canpoint.ecard.utils.CanPointSp
import com.sam.canpoint.ecard.utils.presentationNetWorkImg
import com.sam.canpoint.ecard.utils.presenterNetWorkStr
import com.sam.recycleview.adapter.BaseQuickAdapter
import com.sam.recycleview.adapter.BaseViewHolder
import com.tyx.base.BaseNetWorkActivity
import com.tyx.base.mvvm.ktx.createVM

class FunctionPresentation(private val mContext: Context, display: Display) :
        BasePresentation(mContext, display) {

    private lateinit var mBinding: PresentationFunctionBinding
    private lateinit var mViewModel: HomeViewModel
    private var selectPosition = 0
    private val beans = ArrayList<FunctionBean>()
    private lateinit var mAdapter: FunctionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.presentation_function, null, false)
        setContentView(mBinding.root)
        mViewModel = (mContext as HomeActivity).createVM(HomeViewModel::class.java)
        initView()
        initData()
    }

    private fun initView() {
        val netWorkState = mViewModel.netWorkState.value ?: BaseNetWorkActivity.NetworkStateDef.WIFI
        showNetworkImg(netWorkState.presenterNetWorkStr(), netWorkState.presentationNetWorkImg())
        initAdapter()
        setOnCancelListener {
            selectPosition = 0
            mAdapter.notifyDataSetChanged()
            if (CanPointSp.patternType == CanPointSp.QUOTA_MODE && !TextUtils.isEmpty(CanPointSp.quotaMoney)) {
                mViewModel.updateQuotaMoneySucceed.call()
            }
        }
    }

    private fun initAdapter() {
        mBinding.rcl.layoutManager = LinearLayoutManager(mContext)
        mAdapter = FunctionAdapter(beans)
        mBinding.rcl.adapter = mAdapter
    }

    private fun initData() {
        beans.clear()
        beans.add(FunctionBean("1", "快速退款"))
        beans.add(FunctionBean("2", "设备信息"))
        beans.add(FunctionBean("3", "收银模式"))
        beans.add(FunctionBean("4", "交易统计"))
        beans.add(FunctionBean("5", "脱机设置"))
        mAdapter.notifyDataSetChanged()
    }

    override fun showNetworkImg(text: String, value: Int) {
        mBinding.tvNetStatus.text = text
        mBinding.ivNetStatus.setImageResource(value)
    }

    override fun interceptKeyEvent(event: KeyBoardEvent): Boolean {
        when (event.type) {
            KeyBoardType.NUM -> {
                val num = event.num
                if (num != "1" && num != "2" && num != "3" || num != "4" || num != "5") return true
                selectPosition = num.toInt() - 1
                mAdapter.notifyDataSetChanged()
                functionJump(num)
            }
            KeyBoardType.UP -> {
                if (selectPosition > 0) {
                    selectPosition--
                    mAdapter.notifyDataSetChanged()
                }
            }
            KeyBoardType.DOWN -> {
                if (selectPosition < beans.size - 1) {
                    selectPosition++
                    mAdapter.notifyDataSetChanged()
                }
            }
            KeyBoardType.CONFIRM -> functionJump((selectPosition + 1).toString())
            KeyBoardType.CANCEL -> {
                cancelPresentation()
                mViewModel.showPresentation.value = PresentationFactory.INPUT_AMOUNT
            }
            else -> {
            }
        }
        return true
    }

    private fun functionJump(num: String) {
        when (num) {
            "1" -> mViewModel.showPresentation.value = PresentationFactory.LOCAL_RECORD
            "2" -> DeviceActivity.start(mContext)
            "3" -> mViewModel.showPresentation.value = PresentationFactory.CASH_REGISTER_PATTERN
            "4" -> mViewModel.showPresentation.value = PresentationFactory.STATISTICS
            "5" -> mViewModel.showPresentation.value = PresentationFactory.DEBT_SETTING
        }
    }

    inner class FunctionAdapter(data: List<FunctionBean>) :
            BaseQuickAdapter<FunctionBean, BaseViewHolder>(R.layout.layout_function_item, data) {
        override fun convert(helper: BaseViewHolder, item: FunctionBean) {
            val tvNumber = helper.getView<TextView>(R.id.tv_number)
            val tvName = helper.getView<TextView>(R.id.tv_name)
            tvNumber.text = item.number
            tvName.text = item.name
            val itemView = helper.getView<RelativeLayout>(R.id.root_rl)
            if (helper.adapterPosition == selectPosition) {
                itemView.setBackgroundResource(R.drawable.shape_function_item_select_bg)
                tvName.setTextColor(ContextCompat.getColor(mContext, R.color.white))
                tvNumber.setBackgroundResource(R.drawable.shape_number_select_bg)
                tvNumber.setTextColor(ContextCompat.getColor(mContext, R.color.text_up))
            } else {
                itemView.setBackgroundResource(R.drawable.shape_function_item_unselect_bg)
                tvName.setTextColor(ContextCompat.getColor(mContext, R.color.text_333))
                tvNumber.setBackgroundResource(R.drawable.shape_number_unselect_bg)
                tvNumber.setTextColor(ContextCompat.getColor(mContext, R.color.white))
            }
        }
    }
}
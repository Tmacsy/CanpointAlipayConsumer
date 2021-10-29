package com.sam.canpoint.ecard.ui.home

import android.content.Context
import android.os.Bundle
import android.view.Display
import android.view.LayoutInflater
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.api.bean.FunctionBean
import com.sam.canpoint.ecard.databinding.PresentationSettingBinding
import com.sam.canpoint.ecard.keyboard.KeyBoardEvent
import com.sam.canpoint.ecard.keyboard.KeyBoardType
import com.sam.canpoint.ecard.ui.presentation.BasePresentation
import com.sam.canpoint.ecard.ui.presentation.PresentationFactory
import com.sam.canpoint.ecard.ui.setting.NetWorkSettingActivity
import com.sam.canpoint.ecard.ui.setting.VolumeSettingsActivity
import com.sam.canpoint.ecard.utils.CanPointSp
import com.sam.canpoint.ecard.utils.presentationNetWorkImg
import com.sam.canpoint.ecard.utils.presenterNetWorkStr
import com.sam.recycleview.adapter.BaseQuickAdapter
import com.sam.recycleview.adapter.BaseViewHolder
import com.tyx.base.BaseNetWorkActivity
import com.tyx.base.mvvm.ktx.createVM

class AppSettingPresentation(private val mContext: Context, display: Display) : BasePresentation(mContext, display) {

    private lateinit var mBinding: PresentationSettingBinding
    private lateinit var mViewModel: HomeViewModel
    private var selectPosition = 0//当前选中的位置
    private val beans = ArrayList<FunctionBean>()
    private lateinit var mAdapter: SettingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.presentation_setting, null, false)
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
        }
    }

    private fun initAdapter() {
        mBinding.rcl.layoutManager = LinearLayoutManager(mContext)
        mAdapter = SettingAdapter(beans)
        mBinding.rcl.adapter = mAdapter
    }

    private fun initData() {
        beans.clear()
        beans.add(FunctionBean("1", "网络设置"))
        beans.add(FunctionBean("2", "音量设置"))
        beans.add(FunctionBean("3", "修改密码"))
        beans.add(FunctionBean("4", "退出系统"))
        mAdapter.notifyDataSetChanged()
    }

    override fun interceptKeyEvent(event: KeyBoardEvent): Boolean {
        when (event.type) {
            KeyBoardType.NUM -> {
                val num = event.num
                if (num != "1" && num != "2" && num != "3" || num != "4") return true
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
                mViewModel.showPresentation.value = PresentationFactory.SETTING
            }
        }
        return true
    }

    private fun functionJump(num: String) {
        when (num) {
            "1" -> NetWorkSettingActivity.start(mContext)
            "2" -> VolumeSettingsActivity.start(mContext)
            "3" -> {
                mViewModel.showPresentation.value = PresentationFactory.INPUT_PASS_WORD
                mViewModel.verifyType = CanPointSp.VERIFY_CHANGE_PASSWORD
            }
            "4" -> {
                mViewModel.showPresentation.value = PresentationFactory.INPUT_PASS_WORD
                mViewModel.verifyType = CanPointSp.VERIFY_EXIT_APP
            }
        }
    }

    override fun showNetworkImg(text: String, value: Int) {
        mBinding.tvNetStatus.text = text
        mBinding.ivNetStatus.setImageResource(value)
    }

    internal inner class SettingAdapter(data: List<FunctionBean?>?) : BaseQuickAdapter<FunctionBean, BaseViewHolder>(R.layout.layout_setting_item, data) {
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
package com.sam.canpoint.ecard.ui.home

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.api.bean.LocalRecordBean
import com.sam.canpoint.ecard.api.request.ConsumptionLocalRecordsRequest
import com.sam.canpoint.ecard.databinding.PresentationConsumeRecordBinding
import com.sam.canpoint.ecard.keyboard.KeyBoardEvent
import com.sam.canpoint.ecard.keyboard.KeyBoardType
import com.sam.canpoint.ecard.ui.presentation.BasePresentation
import com.sam.canpoint.ecard.ui.presentation.PresentationFactory
import com.sam.canpoint.ecard.utils.CanPointSp
import com.sam.canpoint.ecard.utils.presentationNetWorkImg
import com.sam.canpoint.ecard.utils.presenterNetWorkStr
import com.sam.db.SamDBManager
import com.sam.db.info.WhereInfo
import com.sam.recycleview.adapter.BaseQuickAdapter
import com.sam.recycleview.adapter.BaseViewHolder
import com.sam.utils.display.SizeUtils
import com.tyx.base.BaseNetWorkActivity
import com.tyx.base.mvvm.ktx.createVM
import java.lang.ref.WeakReference

class LocalRecordPresentation(private val mContext: Context, display: Display) : BasePresentation(mContext, display) {

    private lateinit var mBinding: PresentationConsumeRecordBinding
    private lateinit var mViewModel: HomeViewModel
    private var rlHeight = 0
    private var pageNum = 1 //页数
    private var totalCount = 0L
    private lateinit var mAdapter: LocalRecordAdapter
    private val beans = ArrayList<LocalRecordBean>()
    private lateinit var mHandler: LocalHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.presentation_consume_record, null, false)
        setContentView(mBinding.root)
        mViewModel = (mContext as HomeActivity).createVM(HomeViewModel::class.java)
        observer()
        initView()
        initData()
    }

    private fun initView() {
        mBinding.rcl.post {
            val rlTop = mBinding.rcl.top
            val rlGroupTop = mBinding.rlGroupLl.top
            val rlGroupBottom = mBinding.rlGroupLl.bottom
            rlHeight = rlGroupBottom - (rlTop + rlGroupTop + SizeUtils.px2dp(37f))
        }
        val netWorkState = mViewModel.netWorkState.value ?: BaseNetWorkActivity.NetworkStateDef.WIFI
        showNetworkImg(netWorkState.presenterNetWorkStr(), netWorkState.presentationNetWorkImg())
        initAdapter()
    }

    private fun initData() {
        pageNum = 1
        totalCount = SamDBManager.getInstance().dao(ConsumptionLocalRecordsRequest::class.java).countOf()
        pageNum.searchHistory(true)
    }

    private fun Int.searchHistory(isDown: Boolean) {
        mViewModel.searchLocalHistory(this, isDown, result = {
            beans.clear()
            beans.addAll(it)
            mAdapter.notifyDataSetChanged()
        })
    }

    private fun initAdapter() {
        mAdapter = LocalRecordAdapter(beans)
        mBinding.rcl.layoutManager = LinearLayoutManager(mContext)
        mAdapter.setEmptyView(R.layout.layout_local_record_empty, mBinding.rcl)
        mBinding.rcl.adapter = mAdapter
    }

    private fun observer() {
        mViewModel.run {
            isUpdateLocalRecord.observe(mContext as LifecycleOwner, Observer {
                if (it) {
                    val selectPosition = getSelectPosition()
                    beans[selectPosition].businessChannel = CanPointSp.REFUND
                    mAdapter.notifyItemChanged(selectPosition)
                }
            })
        }
    }

    override fun showNetworkImg(text: String, value: Int) {
        super.showNetworkImg(text, value)
        mBinding.tvNetStatus.text = text
        mBinding.ivNetStatus.setImageResource(value)
    }

    override fun interceptKeyEvent(event: KeyBoardEvent): Boolean {
        if (mBinding.noOrderFoundCl.isShown && event.type != KeyBoardType.CONFIRM) return false
        var selectPosition = getSelectPosition()
        when (event.type) {
            KeyBoardType.UP -> {
                if (selectPosition == -1) return false
                if (pageNum == 1 && selectPosition == 0) return false
                if (selectPosition > 0) {
                    selectPosition -= 1
                    setAdapterSelect(selectPosition)
                } else {
                    pageNum -= 1
                    pageNum.searchHistory(false)
                }
            }
            KeyBoardType.DOWN -> {
                if (selectPosition == -1) return false
                if (pageNum.toDouble() == Math.ceil(totalCount / 6.0) && selectPosition == beans.size - 1) return false
                if (selectPosition < beans.size - 1) {
                    selectPosition += 1
                    setAdapterSelect(selectPosition)
                } else {
                    pageNum += 1
                    pageNum.searchHistory(true)
                }
            }
            KeyBoardType.CONFIRM -> return initConfirm()
            KeyBoardType.CANCEL -> {
                cancelPresentation()
                mViewModel.showPresentation.value = PresentationFactory.FUNCTION
            }
        }
        return true
    }

    private fun initConfirm(): Boolean {
        if (mBinding.noOrderFoundCl.isShown) {
            mBinding.noOrderFoundCl.visibility = View.GONE
            return true
        }
        val selectPosition = getSelectPosition()
        if (beans.size == 0) return true
        val businessChannel = beans[selectPosition].businessChannel
        if (businessChannel == CanPointSp.WITHHOLD) {
            queryOrder(selectPosition)
        } else if (businessChannel == CanPointSp.SUBSIDY) {
            if (!this::mHandler.isInitialized) mHandler = LocalHandler(this)
            mBinding.ffNonRefundableTips.visibility = View.VISIBLE
            mHandler.removeMessages(1)
            mHandler.sendMessageDelayed(mHandler.obtainMessage(1), 1000)
        }
        return true
    }

    private fun queryOrder(selectPosition: Int) {
        val orderId = if (TextUtils.isEmpty(beans[selectPosition].orderId)) "" else beans[selectPosition].orderId
        mViewModel.startQueryOrder(orderId, success = {
            if (it != null) {
                //消费状态(0:已提交,1:处理中,2:已成功,3:已失败,4:已退款)
                if (it.consumeStatus == "2") {
                    mViewModel.startConfirmOrder.value = it
                } else {
                    mBinding.noOrderFoundCl.visibility = View.VISIBLE
                }
                if (it.consumeStatus == "4") {
                    SamDBManager.getInstance().dao(ConsumptionLocalRecordsRequest::class.java)
                            .update(WhereInfo.get().equal(ConsumptionLocalRecordsRequest._orderId, orderId).update("businessChannel", CanPointSp.REFUND))
                    mViewModel.isUpdateLocalRecord.value = true
                }
            } else {
                mBinding.noOrderFoundCl.visibility = View.VISIBLE
            }
        }, error = {
            mBinding.noOrderFoundCl.visibility = View.VISIBLE
        })
    }

    private class LocalHandler(presentation: LocalRecordPresentation) : Handler(Looper.getMainLooper()) {
        private val weakPresentation = WeakReference(presentation)
        override fun dispatchMessage(msg: Message?) {
            super.dispatchMessage(msg)
            val get = weakPresentation.get()
            if (get != null && get.isShowing) {
                get.mBinding.ffNonRefundableTips.visibility = View.GONE
            }
        }
    }

    private fun setAdapterSelect(selectPosition: Int) {
        val data: List<LocalRecordBean> = mAdapter.data
        for (datum in data) {
            datum.isSelected = false
        }
        if (data.size > selectPosition) {
            data[selectPosition].isSelected = true
        }
        mAdapter.notifyDataSetChanged()
    }

    private fun getSelectPosition(): Int {
        val data: List<LocalRecordBean> = mAdapter.getData()
        if (data.isNotEmpty()) {
            for (i in data.indices) {
                if (data[i].isSelected) {
                    return i
                }
            }
        }
        return -1
    }

    inner class LocalRecordAdapter(data: List<LocalRecordBean>) : BaseQuickAdapter<LocalRecordBean, BaseViewHolder>(R.layout.layout_consume_record_item, data) {
        override fun convert(helper: BaseViewHolder, item: LocalRecordBean) {
            val tvName = helper.getView<TextView>(R.id.tv_name)
            val tvTime = helper.getView<TextView>(R.id.tv_time)
            val tvActualAmount = helper.getView<TextView>(R.id.tv_actual_amount)
            tvName.text = item.name
            tvTime.text = item.time
            tvActualAmount.text = item.actualAmount
            val itemView = helper.getView<RelativeLayout>(R.id.root_rl)
            val layoutParams = itemView.layoutParams
            layoutParams.height = rlHeight / 6
            itemView.layoutParams = layoutParams
            if (item.isSelected) {
                tvName.setTextColor(ContextCompat.getColor(mContext, R.color.white))
                tvTime.setTextColor(ContextCompat.getColor(mContext, R.color.white))
                tvActualAmount.setTextColor(if (item.isSynchronized) ContextCompat.getColor(mContext, R.color.white) else ContextCompat.getColor(mContext, R.color.text_failed))
                itemView.setBackgroundResource(R.drawable.shape_blue_sure_bg)
            } else {
                tvName.setTextColor(ContextCompat.getColor(mContext, R.color.text_333))
                tvTime.setTextColor(ContextCompat.getColor(mContext, R.color.text_aaa))
                tvActualAmount.setTextColor(if (item.isSynchronized) ContextCompat.getColor(mContext, R.color.text_333) else ContextCompat.getColor(mContext, R.color.text_failed))
                itemView.setBackgroundResource(R.drawable.offline_record_unselect_bg)
            }
            //选中的条目隐藏底部线
            helper.setVisible(R.id.line, !item.isSelected)
            var way = ""
            when (item.businessChannel) {
                CanPointSp.WITHHOLD -> way = "代扣"
                CanPointSp.SUBSIDY -> way = "补助"
                CanPointSp.REFUND -> way = "退款"
                CanPointSp.OFFLINE -> way = "脱机"
                else -> {
                }
            }
            helper.setText(R.id.tv_way, way)
            helper.setTextColor(R.id.tv_way, if (way == "退款") ContextCompat.getColor(mContext, R.color.text_failed) else ContextCompat.getColor(mContext, R.color.text_success))
        }
    }
}
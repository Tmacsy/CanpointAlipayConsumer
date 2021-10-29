package com.sam.canpoint.ecard.ui.setting

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import com.sam.canpoint.ecard.R
import com.sam.canpoint.ecard.databinding.ActivityVolumeSettingsBinding
import com.sam.canpoint.ecard.keyboard.IKeyBoardCallback
import com.sam.canpoint.ecard.keyboard.KeyBoardEvent
import com.sam.canpoint.ecard.keyboard.KeyBoardManager
import com.sam.canpoint.ecard.keyboard.KeyBoardType
import com.sam.canpoint.ecard.ui.presentation.BasePresentation
import com.sam.canpoint.ecard.ui.presentation.PresentationFactory
import com.sam.canpoint.ecard.utils.presentationNetWorkImg
import com.sam.canpoint.ecard.utils.presenterNetWorkStr
import com.sam.canpoint.ecard.utils.setChangeListener
import com.tyx.base.mvvm.BaseMVVMActivity
import com.tyx.base.mvvm.ktx.createVM
import java.util.*

class VolumeSettingsActivity : BaseMVVMActivity<ActivityVolumeSettingsBinding, VolumeSettingsViewModel>() {
    override fun getLayoutId(): Int = R.layout.activity_volume_settings
    override fun getViewModel(): VolumeSettingsViewModel = createVM(VolumeSettingsViewModel::class.java)
    private var mPresentation: BasePresentation? = null
    private var keyBoardManager = KeyBoardManager()
    private var volumeReceiver: VolumeReceiver? = null
    private lateinit var audioManager: AudioManager

    @SuppressLint("WrongConstant")
    override fun initView() {
        super.initView()
        audioManager = getSystemService("Context.AUDIO_SERVICE") as AudioManager
        mViewModel.maxVolume.value = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        showPresentation()
        registerReceiver()
        initSeekBar()
    }

    private fun initSeekBar() {
        mBinding.volumeSeekBar.max = mViewModel.maxVolume.value ?: 0
        mBinding.volumeSeekBar.setChangeListener(onProgressChanged = { _, progress, _ ->
            updateAllVolume(progress)
        })
    }

    private fun updateAllVolume(volume: Int) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
        mBinding.volumeSeekBar.progress = volume
        mBinding.volumeSeekBarText.text = String.format(Locale.getDefault(), "%d%%", volume * 100 / (mViewModel.maxVolume.value ?: 0))
        mViewModel.currentVolume.value = volume
    }

    private fun showPresentation() {
        val factory = PresentationFactory.getFactory()
        mPresentation = factory.createPresentation(this, PresentationFactory.VOLUME_SETTINGS)
        mPresentation?.showPresentation()
    }

    private fun registerReceiver() {
        volumeReceiver = VolumeReceiver()
        val filter = IntentFilter()
        filter.addAction("android.media.VOLUME_CHANGED_ACTION")
        registerReceiver(volumeReceiver, filter)
    }

    override fun onResume() {
        super.onResume()
        keyBoardManager.keyBoardCallback = keyBoardCallback
    }

    override fun onPause() {
        super.onPause()
        keyBoardManager.keyBoardCallback = null
    }

    override fun onNetWorkChange(state: Int) {
        mPresentation?.showNetworkImg(state.presenterNetWorkStr(), state.presentationNetWorkImg())
    }

    override fun onDestroy() {
        super.onDestroy()
        volumeReceiver?.let { unregisterReceiver(it) }
        mPresentation?.cancelPresentation()
        mBinding.volumeSeekBar.setOnSeekBarChangeListener(null)
    }

    inner class VolumeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "android.media.VOLUME_CHANGED_ACTION") {
                val streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                updateAllVolume(streamVolume)
            }
        }
    }

    private val keyBoardCallback = object : IKeyBoardCallback {
        override fun keyBoardEvent(event: KeyBoardEvent) {
            var streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            when (event.type) {
                KeyBoardType.UP -> {
                    if (streamVolume == 15) return
                    streamVolume += 1
                    updateAllVolume(streamVolume)
                }
                KeyBoardType.DOWN -> {
                    if (streamVolume == 0) return
                    streamVolume -= 1
                    updateAllVolume(streamVolume)
                }
                KeyBoardType.CANCEL -> finish()
            }
        }
    }

    companion object {
        fun start(mContext: Context) {
            val intent = Intent(mContext, VolumeSettingsActivity::class.java)
            mContext.startActivity(intent)
        }
    }
}
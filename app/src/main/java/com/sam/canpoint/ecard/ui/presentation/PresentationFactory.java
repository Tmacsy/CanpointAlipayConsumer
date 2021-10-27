package com.sam.canpoint.ecard.ui.presentation;

import android.app.Activity;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

import com.sam.canpoint.ecard.ui.home.InputAmountPresentation;
import com.sam.canpoint.ecard.ui.home.InputPassWordPresentation;
import com.sam.canpoint.ecard.ui.init.RegisterPresentation;
import com.sam.canpoint.ecard.ui.splash.SplashPresentation;

import java.util.Objects;

public class PresentationFactory implements IPresentationFactory {

    public static final String SPLASH = "splash";
    public static final String REGISTER = "register";
    public static final String INPUT_AMOUNT = "inputAmount";  //首页输入金额小屏
    public static final String ALI_PAY_RESULT = "aliPayResult"; //支付结果
    public static final String INPUT_PASS_WORD = "inputPassWord"; //输入密码
    public static final String SETTING = "setting"; //设置
    public static final String DEVICES_INFO = "devicesInfo"; //设备信息
    public static final String CONSUME_PATTERN = "consumePattern"; //消费模式
    public static final String VOLUME_SETTINGS = "volumeSettings"; //音量设置
    public static final String REFUND_RESULT = "refundResult"; //退款结果
    public static final String FUNCTION = "function"; //功能
    public static final String LOCAL_RECORD = "localRecord"; //本地记录（快速退款列表）
    public static final String CASH_REGISTER_PATTERN = "cashRegisterPattern"; //收银模式
    public static final String STATISTICS = "statistics"; //交易统计
    public static final String CONFIRM_ORDER = "confirmOrder"; //交易统计
    public static final String NETWORK_SETTING = "networkSetting"; //网络设置
    public static final String CHANGE_PASSWORD = "changePassword"; //修改密码
    public static final String DEBT_SETTING = "debtSetting"; //欠款设置
    public static final String CHANGE_PASSWORD_RESULT = "change_password_result"; //修改密码结果

    private static final PresentationFactory factory = new PresentationFactory();

    public static PresentationFactory getFactory() {
        return factory;
    }

    @Override
    public BasePresentation createPresentation(Activity activity, String type) {
        DisplayManager systemService = (DisplayManager) activity.getSystemService(Context.DISPLAY_SERVICE);
        Display[] displays = systemService.getDisplays();
        if (displays.length <= 1) return null;
        BasePresentation presentation;
        switch (type) {
            case SPLASH:
                presentation = new SplashPresentation(activity, displays[1]);
                break;
            case REGISTER:
                presentation = new RegisterPresentation(activity, displays[1]);
                break;
            case INPUT_AMOUNT:
                presentation = new InputAmountPresentation(activity, displays[1]);
                break;
//            case ALI_PAY_RESULT:
//                presentation = new AliPayResultPresentation(activity, displays[1]);
//                break;
            case INPUT_PASS_WORD:
                presentation = new InputPassWordPresentation(activity, displays[1]);
                break;
//            case SETTING:
//                presentation = new AppSettingPresentation(activity, displays[1]);
//                break;
//            case DEVICES_INFO:
//                presentation = new DeviceInfoPresentation(activity, displays[1]);
//                break;
//            case CONSUME_PATTERN:
//                presentation = new ConsumePatternPresentation(activity, displays[1]);
//                break;
//            case VOLUME_SETTINGS:
//                presentation = new VolumeSettingsPresentation(activity, displays[1]);
//                break;
//            case REFUND_RESULT:
//                presentation = new RefundResultPresentation(activity, displays[1]);
//                break;
//            case FUNCTION:
//                presentation = new FunctionPresentation(activity, displays[1]);
//                break;
//            case LOCAL_RECORD:
//                presentation = new LocalRecordPresentation(activity, displays[1]);
//                break;
//            case CASH_REGISTER_PATTERN:
//                presentation = new CashRegisterPatternPresentation(activity, displays[1]);
//                break;
//            case STATISTICS:
//                presentation = new StatisticsPresentation(activity, displays[1]);
//                break;
//            case CONFIRM_ORDER:
//                presentation = new ConfirmOrderPresentation(activity, displays[1]);
//                break;
//            case NETWORK_SETTING:
//                presentation = new NetworkSettingPresentation(activity, displays[1]);
//                break;
//            case CHANGE_PASSWORD:
//                presentation = new ChangePasswordPresentation(activity, displays[1]);
//                break;
//            case DEBT_SETTING:
//                presentation = new DebtSettingPresentation(activity, displays[1]);
//                break;
//            case CHANGE_PASSWORD_RESULT:
//                presentation = new ChangePasswordResultPresentation(activity, displays[1]);
//                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        presentation.setOwnerActivity(activity);
        Objects.requireNonNull(presentation.getWindow()).setType(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        presentation.show();
        return presentation;
    }
}

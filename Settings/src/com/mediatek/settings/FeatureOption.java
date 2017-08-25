
package com.mediatek.settings;

import android.os.SystemProperties;

public class FeatureOption {
    public static final boolean MTK_GEMINI_SUPPORT = getValue("ro.mtk_gemini_support");
    public static final boolean MTK_DHCPV6C_WIFI = getValue("ro.mtk_dhcpv6c_wifi");
    public static final boolean MTK_WAPI_SUPPORT = getValue("ro.mtk_wapi_support");
    public static final boolean MTK_OPEN_AP_WPS_SUPPORT = getValue("mediatek.wlan.openap.wps");
    public static final boolean MTK_IPO_SUPPORT = getValue("ro.mtk_ipo_support");
    public static final boolean MTK_WIFIWPSP2P_NFC_SUPPORT =
            getValue("ro.mtk_wifiwpsp2p_nfc_support");
    public static final boolean MTK_WFD_SUPPORT = getValue("ro.mtk_wfd_support");
    public static final boolean MTK_WFD_SINK_SUPPORT = getValue("ro.mtk_wfd_sink_support");
    public static final boolean MTK_WFD_SINK_UIBC_SUPPORT =
            getValue("ro.mtk_wfd_sink_uibc_support");
    public static final boolean MTK_AUDIO_PROFILES = getValue("ro.mtk_audio_profiles");
    public static final boolean MTK_MULTISIM_RINGTONE_SUPPORT =
            getValue("ro.mtk_multisim_ringtone");
    public static final boolean MTK_PRODUCT_IS_TABLET =
            SystemProperties.get("ro.build.characteristics").equals("tablet");
    public static final boolean MTK_BESLOUDNESS_SUPPORT = getValue("ro.mtk_besloudness_support");
    public static final boolean MTK_BESSURROUND_SUPPORT = getValue("ro.mtk_bessurround_support");
    public static final boolean MTK_LOSSLESS_SUPPORT = getValue("ro.mtk_lossless_bt_audio");
    public static final boolean MTK_NFC_ADDON_SUPPORT = getValue("ro.mtk_nfc_addon_support");
    public static final boolean MTK_BEAM_PLUS_SUPPORT = getValue("ro.mtk_beam_plus_support");

    public static final boolean MTK_TETHERING_EEM_SUPPORT =
            getValue("ro.mtk_tethering_eem_support");
    public static final boolean MTK_TETHERINGIPV6_SUPPORT =
            getValue("ro.mtk_tetheringipv6_support");

    public static final boolean MTK_SYSTEM_UPDATE_SUPPORT =
         getValue("ro.mtk_system_update_support");
    public static final boolean MTK_SCOMO_ENTRY = getValue("ro.mtk_scomo_entry");
    public static final boolean MTK_MDM_SCOMO = getValue("ro.mtk_mdm_scomo");
    public static final boolean MTK_FOTA_ENTRY = getValue("ro.mtk_fota_entry");
    public static final boolean MTK_MDM_FUMO = getValue("ro.mtk_mdm_fumo");
    public static final boolean MTK_DRM_APP = getValue("ro.mtk_oma_drm_support");
    public static final boolean MTK_MIRAVISION_SETTING_SUPPORT =
            getValue("ro.mtk_miravision_support");
    public static final boolean MTK_CLEARMOTION_SUPPORT = getValue("ro.mtk_clearmotion_support");

    public static final boolean MTK_AGPS_APP = getValue("ro.mtk_agps_app");
    public static final boolean MTK_OMACP_SUPPORT = getValue("ro.mtk_omacp_support");
    public static final boolean MTK_GPS_SUPPORT = getValue("ro.mtk_gps_support");
    public static final boolean MTK_VOICE_UI_SUPPORT = getValue("ro.mtk_voice_ui_support");
    public static final boolean MTK_BG_POWER_SAVING_SUPPORT =
            getValue("ro.mtk_bg_power_saving_support");
    public static final boolean MTK_BG_POWER_SAVING_UI_SUPPORT =
            getValue("ro.mtk_bg_power_saving_ui");
    public static final boolean MTK_GMO_RAM_OPTIMIZE = getValue("ro.mtk_gmo_ram_optimize");
    /// M: for Telephony settings @{
    public static final boolean MTK_VOLTE_SUPPORT = getValue("ro.mtk_volte_support");
    // for C2K
    public static final boolean PURE_AP_USE_EXTERNAL_MODEM =
        getValue("ro.pure_ap_use_external_modem");
    public static final boolean MTK_C2K_SUPPORT = getValue("ro.mtk_c2k_support");
    public static final boolean MTK_SVLTE_SUPPORT = getValue("ro.mtk_svlte_support");
    /// @}
    public static final boolean MTK_VOICE_UNLOCK_SUPPORT = getValue("ro.mtk_voice_unlock_support");

    /// M: Add for CT 6M. @ {
    public static final boolean MTK_CT6M_SUPPORT = getValue("ro.ct6m_support");
    /// @ }
    public static final boolean MTK_RUNTIME_PERMISSION_SUPPORT =
        getValue("ro.mtk_runtime_permission");

    /// M: [C2K solution 1.5]
    public static final boolean MTK_C2K_SLOT2_SUPPORT = getValue("ro.mtk.c2k.slot2.support");
    public static final boolean MTK_LTE_SUPPORT = getValue("ro.mtk_lte_support");
    public static final boolean MTK_DISABLE_CAPABILITY_SWITCH =
            getValue("ro.mtk_disable_cap_switch");
    /// @}

    /// M: [A1] @{
    public static final boolean MTK_A1_FEATURE = getValue("ro.mtk_a1_feature");
    /// @}
	
    /// M: Add for EMMC and FLT. @ {
    public static final boolean MTK_EMMC_SUPPORT = getValue("ro.mtk_emmc_support");
    public static final boolean MTK_CACHE_MERGE_SUPPORT = getValue("ro.mtk_cache_merge_support");
    public static final boolean MTK_NAND_FTL_SUPPORT = getValue("ro.mtk_nand_ftl_support");
    /// @}
	/* shengbotao@wind-mobi.com 20150120 for #96089 start */
    public static final boolean WIND_PHONE_NAME_SUPPORT = getValue("ro.wind_optr_phonename");
    /* shengbotao@wind-mobi.com 20150120 for #96089 end */
    // Important!!!  the SystemProperties key's length must less than 31 , or will have JE
    /* get the key's value*/
    private static boolean getValue(String key) {
        return SystemProperties.get(key).equals("1");
    }

    //Feature#96024 lijingwei@wind-mobi.com 2016/1/21 start
    public static final boolean WIND_DEF_PRO_E183L= SystemProperties.get("ro.wind_def_pro_e183l").equals("1");
    public static final boolean MTK_SHARED_SDCARD = getValue("ro.mtk_shared_sdcard");
    public static final boolean WIND_DEF_PRO_E188F= SystemProperties.get("ro.wind_def_pro_e188f").equals("1");
    //Feature#96024 lijingwei@wind-mobi.com 2016/1/21 end
    //fankaijian@wind-mobi.com 2016/8/30 start
    public static final boolean WIND_DEF_PRO_E181L = SystemProperties.get("ro.wind_def_pro_e181l").equals("1");
    public static final boolean WIND_DEF_OPTR_E181L_RUS = SystemProperties.get("ro.wind_def_optr_e181l_rus").equals("1");
    //fankaijian@wind-mobi.com 2016/8/30 end
    // chusuxia@wind-mobi.com 20160215 begin
    public static final boolean WIND_DEF_OPTR_E183L_TEL= SystemProperties.get("ro.wind_def_optr_e183l_tel").equals("1");
    public static final boolean WIND_DEF_SUPPORT_ZTE_FOTA = SystemProperties.get("ro.wind_def_support_zte_fota").equals("1");
    public static final boolean WIND_CUSTOM_SV_NUMBER = SystemProperties.get("ro.wind_custom_sv_number").equals("1");
    // chusuxia@wind-mobi.com 20160215 end
    //fengsong@wind-mobi.com add for show battery percentage start
    public static final boolean WIND_SHOW_BATTERY_PERCENTAGE_SUPPORT = SystemProperties.get("ro.wind_battery_percentage").equals("1");
    //fengsong@wind-mobi.com add for show battery percentage end
    //chengyonghui@wind-mobi.com add 20160418 start
    public static final boolean WIND_DEF_OPTR_E183L_RUS = SystemProperties.get("ro.wind_def_optr_e183l_rus").equals("1");
    //chengyonghui@wind-mobi.com add 20160418 end
    //fengsong@wind-mobi.com add for bug 110495 start
    public static final boolean WIND_DEF_OPTR_E183L_THA= SystemProperties.get("ro.wind_def_optr_e183l_tha").equals("1");
    public static final boolean WIND_DEF_OPTR_E183L_MAL= SystemProperties.get("ro.wind_def_optr_e183l_mal").equals("1");
    public static final boolean WIND_DEF_OPTR_E183L_LIFE= SystemProperties.get("ro.wind_def_optr_e183l_life").equals("1");
    //fengsong@wind-mobi.com add for bug 110495 end
    //lijingwei@wind-mobi.com 2016/5/17 start
    public static final boolean WIND_DEF_PRO_E169F= SystemProperties.get("ro.wind_def_pro_e169f").equals("1");
    //lijingwei@wind-mobi.com 2016/5/17 end
    //xushuhao@wind-mobi.com 2016/5/25 start
    public static final boolean WIND_DEF_PRO_E169F_RUS= SystemProperties.get("ro.wind_def_optr_e169f_rus").equals("1");
    //xushuhao@wind-mobi.com 2016/5/25 end
    //yexumin@wind-mobi.com add for #113113 20160530 start
    public static final boolean WIND_DEF_OPTR_E183L_CL= SystemProperties.get("ro.wind_def_optr_e183l_cl").equals("1");
    //yexumin@wind-mobi.com add for #113113 20160530 start
    // liuyangyao@wind-mobi.com for 117110 20160628 begin
    public static final boolean WIND_DEF_OPTR_E183L_CLA= SystemProperties.get("ro.wind_def_optr_e183l_cla").equals("1");
    // liuyangyao@wind-mobi.com for 117110 20160628 end
    //Feature#96024 lijingwei@wind-mobi.com 2016/6/1 start
    public static final boolean WIND_ADD_STORAGE_SHOW_ITEMS= SystemProperties.get("ro.wind_add_storage_showitems").equals("1");
    //Feature#96024 lijingwei@wind-mobi.com 2016/6/1 end
    //hanweiwei@wind-mobi.com 2016-06-03 start
    public static final boolean WIND_DEF_OPTR_E188F_RUS = SystemProperties.get("ro.wind_def_optr_e188f_rus").equals("1");
    //hanweiwei@wind-mobi.com 2016-06-03 end
    //xushuhao@wind-mobi.com 2016-06-14 start
    public static final boolean WIND_DEF_OPTR_E169F_RUS = SystemProperties.get("ro.wind_def_optr_e169f_rus").equals("1");
    //xushuhao@wind-mobi.com 2016-06-14 end
    //xiangjuncheng@wind-mobi.com 2016.07.01 start
    //fix file entel Buglist of A610_feedback_0629 No.10
    public static final boolean WIND_DEF_OPTR_E183L_PE= SystemProperties.get("ro.wind_def_optr_e183l_pe").equals("1");
    //xiangjuncheng@wind-mobi.com 2016.07.01 end
    //liuxiaoshuan@wind-mobi.com 2016.07.04 start
    //fix Feature #111850
    public static final boolean WIND_DEF_OPTR_E188F_SP= SystemProperties.get("ro.wind_def_optr_e188f_sp").equals("1");
    public static final boolean WIND_DEF_OPTR_E188F_MX= SystemProperties.get("ro.wind_def_optr_e188f_mx").equals("1");
    public static final boolean WIND_DEF_OPTR_E188F_IN= SystemProperties.get("ro.wind_def_optr_e188f_in").equals("1");
    public static final boolean WIND_KEYSWITCH_NOTITLE = SystemProperties.get("ro.wind_keyswitch_notitle").equals("1");
    public static final boolean WIND_DEF_OPTR_E188F_MAL= SystemProperties.get("ro.wind_def_optr_e188f_mal").equals("1");
    //liuxiaoshuan@wind-mobi.com 2016.07.04 end
    //zhuzhibin@wind-mobi.com add 2016/10/12 start
    public static final boolean WIND_DEF_OPTR_E183L_COL = SystemProperties.get("ro.wind_def_optr_e183l_col").equals("1");
    public static final boolean WIND_DEF_OPTR_E183L_IN = SystemProperties.get("ro.wind_def_optr_e183l_in").equals("1");
    //zhuzhibin@wind-mobi.com add 2016/10/12 end

    //bailu@wind-mobi.com 20160711 begin
    //add WIND_HIDE_BASEBAND_DATE
    public static final boolean WIND_HIDE_BASEBAND_DATE = SystemProperties.get("ro.wind_hide_baseband_date").equals("1");
    //bailu@wind-mobi.com 20160711 end
    //lijingwei@wind-mobi.com 2016.07.26 start
    //fix Feature #122197 for spain operator name display
    public static final boolean WIND_DEF_OPTR_E183L_EUR = SystemProperties.get("ro.wind_def_optr_e183l_eur").equals("1");
    //lijingwei@wind-mobi.com 2016.07.26 end
    //lijingwei@wind-mobi.com 2016.09.05 start
    //fix Feature #129880 for EAP-SIM
    public static final boolean WIND_DEF_OPTR_E183L_SG = SystemProperties.get("ro.wind_def_optr_e183l_sg").equals("1");
    //lijingwei@wind-mobi.com 2016.09.05 end
}

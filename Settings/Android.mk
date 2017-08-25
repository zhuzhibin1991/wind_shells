LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

#fengsong@wind-mobi.com add for feature 111893 start
ifeq ($(strip $(WIND_DEF_OPTR_E183L_MAL)), yes)
    LOCAL_MANIFEST_FILE := E183L_MAL/AndroidManifest.xml
endif
#fengsong@wind-mobi.com add for feature 111893 end
#liuxiaoshuan@wind-mobi.com add 20160830 for feature 129546 start
ifeq ($(strip $(WIND_DEF_OPTR_E188F_MAL)), yes)
    LOCAL_MANIFEST_FILE := E183L_MAL/AndroidManifest.xml
endif
#liuxiaoshuan@wind-mobi.com add for feature 129546 end
ifeq ($(strip $(MTK_CLEARMOTION_SUPPORT)),no)
# if not support clearmotion, load a small video for clearmotion
LOCAL_ASSET_DIR := $(LOCAL_PATH)/assets_no_clearmotion
else
LOCAL_ASSET_DIR := $(LOCAL_PATH)/assets_clearmotion
endif

LOCAL_JAVA_LIBRARIES := bouncycastle conscrypt telephony-common ims-common \
                        mediatek-framework

LOCAL_STATIC_JAVA_LIBRARIES := android-support-v4 android-support-v13 jsr305 \
                               com.mediatek.lbs.em2.utils \
                               com.mediatek.settings.ext
                               
LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := \
        $(call all-java-files-under, src) \
        src/com/android/settings/EventLogTags.logtags

LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/res
LOCAL_RESOURCE_DIR += $(LOCAL_PATH)/res_ext

LOCAL_PACKAGE_NAME := Settings
LOCAL_CERTIFICATE := platform
LOCAL_PRIVILEGED_MODULE := true

LOCAL_PROGUARD_FLAG_FILES := proguard.flags

ifneq ($(INCREMENTAL_BUILDS),)
    LOCAL_PROGUARD_ENABLED := disabled
    LOCAL_JACK_ENABLED := incremental
endif

include frameworks/opt/setupwizard/navigationbar/common.mk
include frameworks/opt/setupwizard/library/common.mk
include frameworks/base/packages/SettingsLib/common.mk

include $(BUILD_PACKAGE)

# Use the following include to make our test apk.
ifeq (,$(ONE_SHOT_MAKEFILE))
include $(call all-makefiles-under,$(LOCAL_PATH))
endif

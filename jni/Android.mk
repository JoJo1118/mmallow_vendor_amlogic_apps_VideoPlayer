LOCAL_PATH := $(call my-dir)
AMPLAYER_PATH := $(LOCAL_PATH)/../../LibPlayer

include $(CLEAR_VARS)

LOCAL_MODULE    := libamplayerjni
LOCAL_SRC_FILES := amlogic_playerservice_AmPlayer.c sys_conf.c
LOCAL_ARM_MODE := arm
LOCAL_C_INCLUDES := $(AMPLAYER_PATH)/amplayer/player/include \
    $(AMPLAYER_PATH)/amplayer/control/include \
    $(AMPLAYER_PATH)/amcodec/include \
    $(AMPLAYER_PATH)/amffmpeg \
    $(JNI_H_INCLUDE) 

LOCAL_MODULE_PATH := ${LOCAL_PATH}/../lib
LOCAL_STATIC_LIBRARIES := libamplayer libamcontroler libamcodec libavformat libavcodec libavutil libamadec 
LOCAL_SHARED_LIBRARIES += libutils libmedia libz

LOCAL_PRELINK_MODULE := false

include $(BUILD_SHARED_LIBRARY)

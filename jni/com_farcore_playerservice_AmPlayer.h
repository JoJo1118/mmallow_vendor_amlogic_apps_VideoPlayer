/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_farcore_playerservice_AmPlayer */

#ifndef _Included_com_farcore_playerservice_AmPlayer
#define _Included_com_farcore_playerservice_AmPlayer
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_farcore_playerservice_AmPlayer
 * Method:    setMedia
 * Signature: (Ljava/lang/String;III)I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_setMedia
  (JNIEnv *, jobject, jstring, jint, jint, jint);

/*
 * Class:     com_farcore_playerservice_AmPlayer
 * Method:    playMedia
 * Signature: (Ljava/lang/String;III)I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_playMedia
  (JNIEnv *, jobject, jstring, jint, jint, jint);

/*
 * Class:     com_farcore_playerservice_AmPlayer
 * Method:    start
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_start
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_farcore_playerservice_AmPlayer
 * Method:    pause
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_pause
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_farcore_playerservice_AmPlayer
 * Method:    resume
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_resume
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_farcore_playerservice_AmPlayer
 * Method:    seek
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_seek
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     com_farcore_playerservice_AmPlayer
 * Method:    stop
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_stop
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_farcore_playerservice_AmPlayer
 * Method:    close
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_close
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_farcore_playerservice_AmPlayer
 * Method:    fastforward
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_fastforward
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     com_farcore_playerservice_AmPlayer
 * Method:    fastrewind
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_fastrewind
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     com_farcore_playerservice_AmPlayer
 * Method:    setSubtitleOut
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_setSubtitleOut
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     com_farcore_playerservice_AmPlayer
 * Method:    setAudioTrack
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_setAudioTrack
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     com_farcore_playerservice_AmPlayer
 * Method:    setRepeat
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_setRepeat
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     com_farcore_playerservice_AmPlayer
 * Method:    getMetaInfo
 * Signature: (I)Ljava/lang/Object;
 */
JNIEXPORT jobject JNICALL Java_com_farcore_playerservice_AmPlayer_getMetaInfo
  (JNIEnv *, jobject, jint);

/*
 * Class:     com_farcore_playerservice_AmPlayer
 * Method:    setTone
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_setTone
  (JNIEnv *, jclass, jint, jint);

/*
 * Class:     com_farcore_playerservice_AmPlayer
 * Method:    setIVolume
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_setIVolume
  (JNIEnv *, jclass, jint);

/*
 * Class:     com_farcore_playerservice_AmPlayer
 * Method:    mute
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_mute
  (JNIEnv *, jclass);

/*
 * Class:     com_farcore_playerservice_AmPlayer
 * Method:    unmute
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_unmute
  (JNIEnv *, jclass);

/*
 * Class:     com_farcore_playerservice_AmPlayer
 * Method:    setVideoBlackOut
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_setVideoBlackOut
  (JNIEnv *, jclass, jint);

/*
 * Class:     com_farcore_playerservice_AmPlayer
 * Method:    native_init
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_native_1init
  (JNIEnv *, jclass);

/*
 * Class:     com_farcore_playerservice_AmPlayer
 * Method:    native_uninit
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_native_1uninit
  (JNIEnv *, jclass);

/*
 * Class:     com_farcore_playerservice_AmPlayer
 * Method:    native_enablecolorkey
 * Signature: (S)I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_native_1enablecolorkey
  (JNIEnv *, jclass, jshort);

/*
 * Class:     com_farcore_playerservice_AmPlayer
 * Method:    native_disablecolorkey
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_native_1disablecolorkey
  (JNIEnv *, jclass);

/*
 * Class:     com_farcore_playerservice_AmPlayer
 * Method:    native_setglobalalpha
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_native_1setglobalalpha
  (JNIEnv *, jclass, jint);

/*
 * Class:     com_farcore_playerservice_AmPlayer
 * Method:    native_getosdbpp
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_native_1getosdbpp
  (JNIEnv *, jclass);

/*
 * Class:     com_farcore_playerservice_AmPlayer
 * Method:    enable_freescale
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_enable_1freescale
  (JNIEnv *, jclass, jint);

/*
 * Class:     com_farcore_playerservice_AmPlayer
 * Method:    disable_freescale
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_farcore_playerservice_AmPlayer_disable_1freescale
  (JNIEnv *, jclass, jint);

#ifdef __cplusplus
}
#endif
#endif

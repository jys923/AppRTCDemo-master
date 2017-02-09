/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_appspot_apprtc_my_DetectionBasedTracker */

#ifndef _Included_org_appspot_apprtc_my_DetectionBasedTracker
#define _Included_org_appspot_apprtc_my_DetectionBasedTracker
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     org_appspot_apprtc_my_DetectionBasedTracker
 * Method:    nativeCreateObject
 * Signature: (Ljava/lang/String;I)J
 */
JNIEXPORT jlong JNICALL Java_org_appspot_apprtc_my_DetectionBasedTracker_nativeCreateObject
  (JNIEnv *, jclass, jstring, jint);

/*
 * Class:     org_appspot_apprtc_my_DetectionBasedTracker
 * Method:    nativeDestroyObject
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_appspot_apprtc_my_DetectionBasedTracker_nativeDestroyObject
  (JNIEnv *, jclass, jlong);

/*
 * Class:     org_appspot_apprtc_my_DetectionBasedTracker
 * Method:    nativeStart
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_appspot_apprtc_my_DetectionBasedTracker_nativeStart
  (JNIEnv *, jclass, jlong);

/*
 * Class:     org_appspot_apprtc_my_DetectionBasedTracker
 * Method:    nativeStop
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_org_appspot_apprtc_my_DetectionBasedTracker_nativeStop
  (JNIEnv *, jclass, jlong);

/*
 * Class:     org_appspot_apprtc_my_DetectionBasedTracker
 * Method:    nativeSetFaceSize
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_org_appspot_apprtc_my_DetectionBasedTracker_nativeSetFaceSize
  (JNIEnv *, jclass, jlong, jint);

/*
 * Class:     org_appspot_apprtc_my_DetectionBasedTracker
 * Method:    nativeDetect
 * Signature: (JJJ)V
 */
JNIEXPORT void JNICALL Java_org_appspot_apprtc_my_DetectionBasedTracker_nativeDetect
  (JNIEnv *, jclass, jlong, jlong, jlong);

#ifdef __cplusplus
}
#endif
#endif
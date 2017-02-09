////
//// Created by Administrator on 2017-01-14.
////
////#include <opencv/cv.h>
////#include <opencv2/imgproc/imgproc_c.h>
////#include <opencv2/core.hpp>
////#include <opencv2/highgui.hpp>
////#include <opencv2/imgproc.hpp>
//
////#include <jni.h>
////#include <opencv2/core/core.hpp>
////#include <opencv2/highgui/highgui.hpp>
////#include <opencv2/imgproc/imgproc.hpp>
////#include <stdio.h>
////#include <iostream>
////#include <cmath>
////using namespace std;
////using namespace cv;
//
////#include "org_appspot_apprtc_NDK_Hello.h"
////
////JNIEXPORT jint JNICALL Java_org_appspot_apprtc_NDK_Hello_GetInt
////        (JNIEnv *, jobject)
////{
////    return 777;
////}
//
////#include "org_appspot_apprtc_my_FaceDetectActivity.h"
////
////JNIEXPORT jint JNICALL Java_org_appspot_apprtc_my_FaceDetectActivity_convertNativeGray
////        (JNIEnv *, jobject, jlong, jlong)
////{
////    return 777;
////}
//
//#include <jni.h>
//#include "org_appspot_apprtc_my_FaceDetectActivity.h"
//#include <opencv2/opencv.hpp>
//#include <stdlib.h>
//#include <stdio.h>
//
//using namespace std;
//using namespace cv;
//
//int toGray(Mat img, Mat& gray);
//
//extern "C" {
//
//JNIEXPORT jint JNICALL Java_org_appspot_apprtc_my_FaceDetectActivity_convertNativeGray(JNIEnv*, jobject, jlong addrRgba, jlong addrGray);
//
//JNIEXPORT jint JNICALL Java_org_appspot_apprtc_my_FaceDetectActivity_convertNativeGray(JNIEnv*, jobject, jlong addrRgba, jlong addrGray) {
//
//    Mat& mRgb = *(Mat*)addrRgba;
//    Mat& mGray = *(Mat*)addrGray;
//
//    int conv;
//    jint retVal;
//
//    conv = toGray(mRgb, mGray);
//
//    retVal = (jint)conv;
//
//    return retVal;
//
//}
//
//}
//
//int toGray(Mat img, Mat& gray)
//{
//    Mat grayframe;
//    // face detection configuration
//    CascadeClassifier face_classifier;
//    //face_classifier.load( "{YOUR_OPENCV_PATH}/data/haarcascades/haarcascade_frontalface_default.xml" );
//    face_classifier.load( "/storage/emulated/0/ChatRTC/Downloads/haarcascade_frontalface_default.xml" );
//    gray=img.clone();
//    cvtColor(img, grayframe, CV_RGBA2GRAY);
//    equalizeHist( grayframe, grayframe );
//    // -------------------------------------------------------------
//    // face detection routine
//
//    // a vector array to store the face found
//    std::vector<cv::Rect> faces;
//
//    face_classifier.detectMultiScale( grayframe, faces,
//                                      1.1, // increase search scale by 10% each pass
//                                      3,   // merge groups of three detections
//                                      /*CV_HAAR_FIND_BIGGEST_OBJECT|CV_HAAR_SCALE_IMAGE,*/
//                                      4|2,
//                                      cv::Size( 30, 30 )
//    );
//    // -------------------------------------------------------------
//    // draw the results
//    for( int i = 0 ; i < faces.size() ; i++ ) {
//        Point lb( faces[i].x + faces[i].width, faces[i].y + faces[i].height );
//        Point tr( faces[i].x, faces[i].y );
//        rectangle( gray, lb, tr, cv::Scalar( 0, 255, 0 ), 3, 4, 0 );
//    }
//    return(0);
//}
//
////int toGray(Mat img, Mat& gray)
////{
////    cvtColor(img, gray, CV_RGBA2GRAY);
////
////    if (gray.rows == img.rows && gray.cols == img.cols)
////    {
////        return (1);
////    }
////    return(0);
////}

//#include "org_appspot_apprtc_my_FaceDetectActivity.h"
////#include <jni.h>
//#include <opencv2/opencv.hpp>
//#include <android/asset_manager_jni.h>
//#include <android/log.h>
//#include <string>
//
//using namespace cv;
//using namespace std;
//
//extern "C" {
//float resize(Mat img_src, Mat &img_resize, int resize_width) {
//    float scale = resize_width / (float) img_src.cols;
//    if (img_src.cols > resize_width) {
//        int new_height = cvRound(img_src.rows * scale);
//        resize(img_src, img_resize, Size(resize_width, new_height));
//    }
//    else {
//        img_resize = img_src;
//    }
//    return scale;
//}
//JNIEXPORT void JNICALL
//Java_com_tistory_webnautes_assetmanager_1jni_1example_MainActivity_detect
//(JNIEnv * , jobject ,jlong cascadeClassifier_face,jlong cascadeClassifier_eye,jlong addrInput,jlong addrResult ) {
//Mat &img_input = *(Mat *) addrInput;
//Mat &img_result = *(Mat *) addrResult;
//img_result = img_input.clone();
//std::vector <Rect> faces;
//Mat img_gray;
//cvtColor(img_input, img_gray, COLOR_BGR2GRAY ) ;
//equalizeHist(img_gray, img_gray ) ;
//Mat img_resize;
//float resizeRatio = resize(img_gray, img_resize, 640);
//
////-- Detect faces
//(( CascadeClassifier * ) cascadeClassifier_face ) -> detectMultiScale( img_resize, faces, 1.1 , 2 , 0 | CASCADE_SCALE_IMAGE , Size( 30 , 30 )) ;
//__android_log_print(ANDROID_LOG_DEBUG, (
//char * ) "native-lib :: " ,
//( char * ) "face %d found " , faces . size()) ;
//for ( int i = 0;
//i<faces . size();
//i ++ ) {
//double real_facesize_x = faces[i].x / resizeRatio;
//double real_facesize_y = faces[i].y / resizeRatio;
//double real_facesize_width = faces[i].width / resizeRatio;
//double real_facesize_height = faces[i].height / resizeRatio;
//Point center(real_facesize_x + real_facesize_width / 2, real_facesize_y + real_facesize_height / 2);
//ellipse(img_result, center, Size(real_facesize_width / 2, real_facesize_height / 2), 0 , 0 , 360 ,
//Scalar( 255 , 0 , 255 ) , 30 , 8 , 0 ) ;
//Rect face_area(real_facesize_x, real_facesize_y, real_facesize_width, real_facesize_height);
//Mat faceROI = img_gray(face_area);
//std::vector <Rect> eyes;
//
////-- In each face, detect eyes
//(( CascadeClassifier * ) cascadeClassifier_eye ) -> detectMultiScale( faceROI, eyes, 1.1 , 2 , 0 | CASCADE_SCALE_IMAGE , Size( 30 , 30 )) ;
//for ( size_t j = 0;
//j<eyes . size();
//j ++ )
//{
//Point eye_center(real_facesize_x + eyes[j].x + eyes[j].width / 2,
//                 real_facesize_y + eyes[j].y + eyes[j].height / 2);
//int radius = cvRound((eyes[j].width + eyes[j].height) * 0.25);
//circle( img_result, eye_center, radius, Scalar(255, 0, 0), 30 , 8 , 0 ) ;
//}
//}
//}
//
//JNIEXPORT jlong JNICALL Java_org_appspot_apprtc_my_FaceDetectActivity_loadCascade(JNIEnv * env,jobject,jstring cascadeFileName) {
//const char *nativeFileNameString = env->GetStringUTFChars(cascadeFileName, JNI_FALSE);
//string baseDir("/storage/emulated/0/");
//baseDir.
//append(nativeFileNameString);
//const char *pathDir = baseDir.c_str();
//jlong ret = 0;
//ret = (jlong)
//new
//CascadeClassifier(pathDir);
//if (((CascadeClassifier *) ret)->
//
//empty()
//
//) {
//__android_log_print(ANDROID_LOG_DEBUG,
//"native-lib :: ",
//"CascadeClassifier로 로딩 실패  %s", nativeFileNameString);
//}
//else
//__android_log_print(ANDROID_LOG_DEBUG,
//"native-lib :: ",
//"CascadeClassifier로 로딩 성공 %s", nativeFileNameString);
//
//return
//ret;
//}
//}

#include <org_appspot_apprtc_my_DetectionBasedTracker.h>
#include <opencv2/core/core.hpp>
#include <opencv2/objdetect.hpp>

#include <string>
#include <vector>

#include <android/log.h>

#define LOG_TAG "FaceDetection/DetectionBasedTracker"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))

using namespace std;
using namespace cv;

inline void vector_Rect_to_Mat(vector<Rect>& v_rect, Mat& mat)
{
    mat = Mat(v_rect, true);
}

class CascadeDetectorAdapter: public DetectionBasedTracker::IDetector
{
public:
    CascadeDetectorAdapter(cv::Ptr<cv::CascadeClassifier> detector):
            IDetector(),
            Detector(detector)
    {
        LOGD("CascadeDetectorAdapter::Detect::Detect");
        CV_Assert(detector);
    }

    void detect(const cv::Mat &Image, std::vector<cv::Rect> &objects)
    {
        LOGD("CascadeDetectorAdapter::Detect: begin");
        LOGD("CascadeDetectorAdapter::Detect: scaleFactor=%.2f, minNeighbours=%d, minObjSize=(%dx%d), maxObjSize=(%dx%d)", scaleFactor, minNeighbours, minObjSize.width, minObjSize.height, maxObjSize.width, maxObjSize.height);
        Detector->detectMultiScale(Image, objects, scaleFactor, minNeighbours, 0, minObjSize, maxObjSize);
        LOGD("CascadeDetectorAdapter::Detect: end");
    }

    virtual ~CascadeDetectorAdapter()
    {
        LOGD("CascadeDetectorAdapter::Detect::~Detect");
    }

private:
    CascadeDetectorAdapter();
    cv::Ptr<cv::CascadeClassifier> Detector;
};

struct DetectorAgregator
{
    cv::Ptr<CascadeDetectorAdapter> mainDetector;
    cv::Ptr<CascadeDetectorAdapter> trackingDetector;

    cv::Ptr<DetectionBasedTracker> tracker;
    DetectorAgregator(cv::Ptr<CascadeDetectorAdapter>& _mainDetector, cv::Ptr<CascadeDetectorAdapter>& _trackingDetector):
            mainDetector(_mainDetector),
            trackingDetector(_trackingDetector)
    {
        CV_Assert(_mainDetector);
        CV_Assert(_trackingDetector);

        DetectionBasedTracker::Parameters DetectorParams;
        tracker = makePtr<DetectionBasedTracker>(mainDetector, trackingDetector, DetectorParams);
    }
};

JNIEXPORT jlong JNICALL Java_org_appspot_apprtc_my_DetectionBasedTracker_nativeCreateObject
        (JNIEnv * jenv, jclass, jstring jFileName, jint faceSize)
{
    LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeCreateObject enter");
    const char* jnamestr = jenv->GetStringUTFChars(jFileName, NULL);
    string stdFileName(jnamestr);
    jlong result = 0;

    LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeCreateObject");

    try
    {
        cv::Ptr<CascadeDetectorAdapter> mainDetector = makePtr<CascadeDetectorAdapter>(
                makePtr<CascadeClassifier>(stdFileName));
        cv::Ptr<CascadeDetectorAdapter> trackingDetector = makePtr<CascadeDetectorAdapter>(
                makePtr<CascadeClassifier>(stdFileName));
        result = (jlong)new DetectorAgregator(mainDetector, trackingDetector);
        if (faceSize > 0)
        {
            mainDetector->setMinObjectSize(Size(faceSize, faceSize));
            //trackingDetector->setMinObjectSize(Size(faceSize, faceSize));
        }
    }
    catch(cv::Exception& e)
    {
        LOGD("nativeCreateObject caught cv::Exception: %s", e.what());
        jclass je = jenv->FindClass("org/opencv/core/CvException");
        if(!je)
            je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, e.what());
    }
    catch (...)
    {
        LOGD("nativeCreateObject caught unknown exception");
        jclass je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, "Unknown exception in JNI code of DetectionBasedTracker.nativeCreateObject()");
        return 0;
    }

    LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeCreateObject exit");
    return result;
}

JNIEXPORT void JNICALL Java_org_appspot_apprtc_my_DetectionBasedTracker_nativeDestroyObject
(JNIEnv * jenv, jclass, jlong thiz)
{
LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeDestroyObject");

try
{
if(thiz != 0)
{
((DetectorAgregator*)thiz)->tracker->stop();
delete (DetectorAgregator*)thiz;
}
}
catch(cv::Exception& e)
{
LOGD("nativeestroyObject caught cv::Exception: %s", e.what());
jclass je = jenv->FindClass("org/opencv/core/CvException");
if(!je)
je = jenv->FindClass("java/lang/Exception");
jenv->ThrowNew(je, e.what());
}
catch (...)
{
LOGD("nativeDestroyObject caught unknown exception");
jclass je = jenv->FindClass("java/lang/Exception");
jenv->ThrowNew(je, "Unknown exception in JNI code of DetectionBasedTracker.nativeDestroyObject()");
}
LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeDestroyObject exit");
}

JNIEXPORT void JNICALL Java_org_appspot_apprtc_my_DetectionBasedTracker_nativeStart
(JNIEnv * jenv, jclass, jlong thiz)
{
LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeStart");

try
{
((DetectorAgregator*)thiz)->tracker->run();
}
catch(cv::Exception& e)
{
LOGD("nativeStart caught cv::Exception: %s", e.what());
jclass je = jenv->FindClass("org/opencv/core/CvException");
if(!je)
je = jenv->FindClass("java/lang/Exception");
jenv->ThrowNew(je, e.what());
}
catch (...)
{
LOGD("nativeStart caught unknown exception");
jclass je = jenv->FindClass("java/lang/Exception");
jenv->ThrowNew(je, "Unknown exception in JNI code of DetectionBasedTracker.nativeStart()");
}
LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeStart exit");
}

JNIEXPORT void JNICALL Java_org_appspot_apprtc_my_DetectionBasedTracker_nativeStop
(JNIEnv * jenv, jclass, jlong thiz)
{
LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeStop");

try
{
((DetectorAgregator*)thiz)->tracker->stop();
}
catch(cv::Exception& e)
{
LOGD("nativeStop caught cv::Exception: %s", e.what());
jclass je = jenv->FindClass("org/opencv/core/CvException");
if(!je)
je = jenv->FindClass("java/lang/Exception");
jenv->ThrowNew(je, e.what());
}
catch (...)
{
LOGD("nativeStop caught unknown exception");
jclass je = jenv->FindClass("java/lang/Exception");
jenv->ThrowNew(je, "Unknown exception in JNI code of DetectionBasedTracker.nativeStop()");
}
LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeStop exit");
}

JNIEXPORT void JNICALL Java_org_appspot_apprtc_my_DetectionBasedTracker_nativeSetFaceSize
(JNIEnv * jenv, jclass, jlong thiz, jint faceSize)
{
LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeSetFaceSize -- BEGIN");

try
{
if (faceSize > 0)
{
((DetectorAgregator*)thiz)->mainDetector->setMinObjectSize(Size(faceSize, faceSize));
//((DetectorAgregator*)thiz)->trackingDetector->setMinObjectSize(Size(faceSize, faceSize));
}
}
catch(cv::Exception& e)
{
LOGD("nativeStop caught cv::Exception: %s", e.what());
jclass je = jenv->FindClass("org/opencv/core/CvException");
if(!je)
je = jenv->FindClass("java/lang/Exception");
jenv->ThrowNew(je, e.what());
}
catch (...)
{
LOGD("nativeSetFaceSize caught unknown exception");
jclass je = jenv->FindClass("java/lang/Exception");
jenv->ThrowNew(je, "Unknown exception in JNI code of DetectionBasedTracker.nativeSetFaceSize()");
}
LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeSetFaceSize -- END");
}


JNIEXPORT void JNICALL Java_org_appspot_apprtc_my_DetectionBasedTracker_nativeDetect
(JNIEnv * jenv, jclass, jlong thiz, jlong imageGray, jlong faces)
{
LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeDetect");

try
{
vector<Rect> RectFaces;
((DetectorAgregator*)thiz)->tracker->process(*((Mat*)imageGray));
((DetectorAgregator*)thiz)->tracker->getObjects(RectFaces);
*((Mat*)faces) = Mat(RectFaces, true);
}
catch(cv::Exception& e)
{
LOGD("nativeCreateObject caught cv::Exception: %s", e.what());
jclass je = jenv->FindClass("org/opencv/core/CvException");
if(!je)
je = jenv->FindClass("java/lang/Exception");
jenv->ThrowNew(je, e.what());
}
catch (...)
{
LOGD("nativeDetect caught unknown exception");
jclass je = jenv->FindClass("java/lang/Exception");
jenv->ThrowNew(je, "Unknown exception in JNI code DetectionBasedTracker.nativeDetect()");
}
LOGD("Java_org_opencv_samples_facedetect_DetectionBasedTracker_nativeDetect END");
}

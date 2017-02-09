package org.appspot.apprtc.NDK;

/**
 * Created by Administrator on 2017-01-14.
 */
public class Util {
    public native String face(String path);

    static {
        System.loadLibrary("jniHello");
    }
}

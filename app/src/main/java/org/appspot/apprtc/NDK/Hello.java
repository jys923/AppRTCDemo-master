package org.appspot.apprtc.NDK;

/**
 * Created by Administrator on 2017-01-14.
 */
public class Hello{
    public native int GetInt();

    static {
        System.loadLibrary("jniHello");
    }
}

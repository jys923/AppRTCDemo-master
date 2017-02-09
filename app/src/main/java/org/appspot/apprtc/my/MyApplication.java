package org.appspot.apprtc.my;

import android.app.Application;

import java.io.DataOutputStream;
import java.net.Socket;

/**
 * Created by Administrator on 2016-12-21.
 */
public class MyApplication extends Application
{
    private String RTC_msg;
    private String mGlobalString;
    private Socket socket;
    private DataOutputStream out;
    private String ip="192.168.0.33";
    private String iphttp="192.168.0.38";
    private int port=9876;
    private int porthttp=47271;
    private String id="sang";
    private Boolean ChatRoomBR=false;
    private Boolean RoomListBR=false;

    public String getRTC_msg() {
        return RTC_msg;
    }

    public void setRTC_msg(String RTC_msg) {
        this.RTC_msg = RTC_msg;
    }

    public DataOutputStream getOut() {
        return out;
    }

    public void setOut(DataOutputStream out) {
        this.out = out;
    }

    public String getIphttp() {
        return iphttp;
    }

    public void setIphttp(String iphttp) {
        this.iphttp = iphttp;
    }

    public int getPorthttp() {
        return porthttp;
    }

    public void setPorthttp(int porthttp) {
        this.porthttp = porthttp;
    }

    public Boolean getChatRoomBR() {
        return ChatRoomBR;
    }

    public void setChatRoomBR(Boolean chatRoomBR) {
        ChatRoomBR = chatRoomBR;
    }

    public Boolean getRoomListBR() {
        return RoomListBR;
    }

    public void setRoomListBR(Boolean roomListBR) {
        RoomListBR = roomListBR;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getmGlobalString() {
        return mGlobalString;
    }

    public void setmGlobalString(String mGlobalString) {
        this.mGlobalString = mGlobalString;
    }
}
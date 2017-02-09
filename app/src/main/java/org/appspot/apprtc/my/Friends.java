package org.appspot.apprtc.my;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2016-12-16.
 */
public class Friends {
    private Bitmap iconDrawable;
    private String titleStr;
    private String descStr;
    private String lstStr;

    public String getLstStr() {
        return lstStr;
    }

    public void setLstStr(String lstStr) {
        this.lstStr = lstStr;
    }

    public void setIcon(Bitmap icon) {
        iconDrawable = icon;
    }

    public void setTitle(String title) {
        titleStr = title;
    }

    public void setDesc(String desc) {
        descStr = desc;
    }

    public Bitmap getIcon() {
        return this.iconDrawable;
    }

    public String getTitle() {
        return this.titleStr;
    }

    public String getDesc() {
        return this.descStr;
    }
}


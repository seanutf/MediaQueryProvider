package com.seanutf.media.queryprovider.data;

import java.io.Serializable;

/**
 * 定义本地媒体文件(图片、视频)的基本类型
 */
public class Media implements Serializable {

    public static final int TYPE_MEDIA_IMAGE = 1;
    public static final int TYPE_MEDIA_VIDEO = 2;

    public int mediaType;    //媒体类型 1图片 2视频
    public int mediaHeight;  //媒体文件的高度
    public int mediaWidth; //媒体文件的宽度
    public String mediaPath;//媒体文件的路径
    public long dateModified;  //媒体文件的修改时间
    public String artist;  //媒体文件的作者艺术家等信息
    public long size;//媒体文件的文件大小
    public long duration;//媒体文件的时长(视频)
    public String name;//媒体文件的名称

    public boolean isVideo() {
        return mediaType == TYPE_MEDIA_VIDEO;
    }
}

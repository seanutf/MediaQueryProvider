package com.seanutf.media.queryprovider.config

import com.seanutf.media.queryprovider.QueryMode
import com.seanutf.media.queryprovider.data.ImgFormat
import com.seanutf.media.queryprovider.data.VideoFormat


class QueryConfig {
    companion object {
        const val MIN_DEFAULT_INT_VALUE = -1
        const val MAX_DEFAULT_INT_VALUE = Int.MAX_VALUE
        const val MIN_DEFAULT_LONG_VALUE = -1L
        const val MAX_DEFAULT_LONG_VALUE = Long.MAX_VALUE
    }

    /**
     * 设置媒体的查找范围
     * 默认查找全部(包括图片和视频，暂不支持音频)
     * */
    var mode: QueryMode = QueryMode.ALL

    /**
     * 对查找图片的最小宽度要求
     * 只查找大于设置值的图片
     * 默认为-1，即对最小宽度无要求
     * */
    var imgMinWidth = MIN_DEFAULT_INT_VALUE


    /**
     * 对查找图片的最大宽度要求
     * 只查找小于设置值的图片
     * 默认为-1，即对最大宽度无要求
     * */
    var imgMaxWidth = MAX_DEFAULT_INT_VALUE

    /**
     * 对查找图片的最小高度要求
     * 只查找大于设置值的图片
     * 默认为-1，即对最小高度无要求
     * */
    var imgMinHeight = MIN_DEFAULT_INT_VALUE

    /**
     * 对查找图片的最大高度要求
     * 只查找小于设置值的图片
     * 默认为-1，即对最大高度无要求
     * */
    var imgMaxHeight = MAX_DEFAULT_INT_VALUE

    /**
     * 对查找图片的最小文件体积要求
     * 只查找大于设置值的图片
     * 默认为-1，即对最小文件体积无要求
     * */
    var imgMinSize = MIN_DEFAULT_LONG_VALUE

    /**
     * 对查找图片的最大文件体积要求
     * 只查找小于设置值的图片
     * 默认为-1，即对最大文件体积无要求
     * */
    var imgMaxSize = MAX_DEFAULT_LONG_VALUE

    /**
     * 对查找视频的最小宽度要求
     * 只查找大于设置值的视频
     * 默认为-1，即对最小宽度无要求
     * */
    var videoMinWidth = MIN_DEFAULT_INT_VALUE

    /**
     * 对查找视频的最大宽度要求
     * 只查找小于设置值的视频
     * 默认为-1，即对最大宽度无要求
     * */
    var videoMaxWidth = MAX_DEFAULT_INT_VALUE

    /**
     * 对查找视频的最小高度要求
     * 只查找大于设置值的视频
     * 默认为-1，即对最小高度无要求
     * */
    var videoMinHeight = MIN_DEFAULT_INT_VALUE

    /**
     * 对查找视频的最大高度要求
     * 只查找小于设置值的视频
     * 默认为-1，即对最大高度无要求
     * */
    var videoMaxHeight = MAX_DEFAULT_INT_VALUE

    /**
     * 对查找视频的最小文件体积要求
     * 只查找大于设置值的视频
     * 默认为-1，即对最小文件体积无要求
     * */
    var videoMinSize = MIN_DEFAULT_LONG_VALUE

    /**
     * 对查找视频的最大文件体积要求
     * 只查找小于设置值的视频
     * 默认为-1，即对最大文件体积无要求
     * */
    var videoMaxSize = MAX_DEFAULT_LONG_VALUE

    /**
     * 对查找媒体的最小宽度要求
     * 只查找小于设置值的媒体
     * 默认为-1，即对最小宽度无要求
     * 设置改值后，会覆盖
     * [imgMinWidth]
     * [videoMinWidth] 设置的值
     * */
    var mediaMinWidth = MIN_DEFAULT_INT_VALUE

    /**
     * 对查找媒体的最大宽度要求
     * 只查找小于设置值的媒体
     * 默认为Int.MAX_VALUE，即对最大宽度无要求
     * 设置改值后，会覆盖
     * [imgMaxWidth]
     * [videoMaxWidth] 设置的值
     * */
    var mediaMaxWidth = MAX_DEFAULT_INT_VALUE

    /**
     * 对查找媒体的最小高度要求
     * 只查找小于设置值的媒体
     * 默认为-1，即对最小高度无要求
     * 设置改值后，会覆盖
     * [imgMinHeight]
     * [videoMinHeight] 设置的值
     * */
    var mediaMinHeight = MIN_DEFAULT_INT_VALUE

    /**
     * 对查找媒体的最大高度要求
     * 只查找小于设置值的媒体
     * 默认为Int.MAX_VALUE，即对最大高度无要求
     * 设置改值后，会覆盖
     * [imgMaxHeight]
     * [videoMaxHeight] 设置的值
     * */
    var mediaMaxHeight = MAX_DEFAULT_INT_VALUE

    /**
     * 对查找媒体的最小文件体积要求
     * 只查找大于设置值的媒体
     * 默认为-1，即对最小文件体积无要求
     * 设置改值后，会覆盖
     * [imgMinSize]
     * [videoMinSize] 设置的值
     * */
    var mediaMinSize = MIN_DEFAULT_LONG_VALUE

    /**
     * 对查找媒体的最大文件体积要求
     * 只查找小于设置值的媒体
     * 默认为Long.MAX_VALUE，即对最大文件体积无要求
     * 设置该值后，会覆盖
     * [imgMaxSize]
     * [videoMaxSize] 设置的值
     * */
    var mediaMaxSize = MAX_DEFAULT_LONG_VALUE

    /**
     * 对查找图片的格式设置
     * 默认为null，即查找所有图片格式文件
     * 如果需指定特定文件格式，请创建
     * [ImgFormat]数组
     * */
    var imgQueryFormatArray: Array<String>? = null

    /**
     * 对查找视频的格式设置
     * 默认为null，即查找所有视频格式文件
     * 如果需指定特定文件格式，请创建
     * [VideoFormat]数组
     * */
    var videoQueryFormatArray: Array<String>? = null

    /**
     * 设置查找媒体的名称
     * */
    var queryName: String? = null

    /**
     * 设置查找时间范围的开始时间
     * 单位为毫秒
     * 默认为-1，即不限制开始时间
     * */
    var startTime = MIN_DEFAULT_LONG_VALUE

    /**
     * 设置查找时间范围的结束时间
     * 单位为毫秒
     * 默认为-1，即不限制结束时间
     * */
    var endTime = MAX_DEFAULT_LONG_VALUE

    /**
     * 设置媒体列表排序规则是否为创建时间
     * */
    var sortByCreate = false


    /**
     * 预留,支持分页加载
     * */
    var nextPageId = -1L


    fun transSetting() {
        if (mediaMinWidth != MIN_DEFAULT_INT_VALUE) {
            imgMinWidth = mediaMinWidth
            videoMinWidth = mediaMinWidth
        }

        if (mediaMaxWidth != MAX_DEFAULT_INT_VALUE) {
            imgMaxWidth = mediaMaxWidth
            videoMaxWidth = mediaMaxWidth
        }

        if (mediaMinHeight != MIN_DEFAULT_INT_VALUE) {
            imgMinHeight = mediaMinHeight
            videoMinHeight = mediaMinHeight
        }

        if (mediaMaxHeight != MAX_DEFAULT_INT_VALUE) {
            imgMaxHeight = mediaMaxHeight
            videoMaxHeight = mediaMaxHeight
        }

        if (mediaMinSize != MIN_DEFAULT_LONG_VALUE) {
            imgMinSize = mediaMinSize
            videoMinSize = mediaMinSize
        }

        if (mediaMaxSize != MAX_DEFAULT_LONG_VALUE) {
            imgMaxSize = mediaMaxSize
            videoMaxSize = mediaMaxSize
        }
    }
}
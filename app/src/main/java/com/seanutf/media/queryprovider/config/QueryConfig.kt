package com.seanutf.media.queryprovider.config

import com.seanutf.media.queryprovider.QueryMode
import com.seanutf.media.queryprovider.QueryRule
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
     * 单位像素值
     * */
    var imgMinWidth: Int = MIN_DEFAULT_INT_VALUE


    /**
     * 对查找图片的最大宽度要求
     * 只查找小于设置值的图片
     * 默认为最大值，即对最大宽度无要求
     * 单位像素值
     * */
    var imgMaxWidth: Int = MAX_DEFAULT_INT_VALUE

    /**
     * 对查找图片的最小高度要求
     * 只查找大于设置值的图片
     * 默认为-1，即对最小高度无要求
     * 单位像素值
     * */
    var imgMinHeight: Int = MIN_DEFAULT_INT_VALUE

    /**
     * 对查找图片的最大高度要求
     * 只查找小于设置值的图片
     * 默认为最大值，即对最大高度无要求
     * 单位像素值
     * */
    var imgMaxHeight: Int = MAX_DEFAULT_INT_VALUE

    /**
     * 对查找图片的最小文件体积要求
     * 只查找大于设置值的图片
     * 默认为10k，即对最小需大于10k
     * 单位为Byte单位
     * */
    var imgMinSize: Long = 10240

    /**
     * 对查找图片的最大文件体积要求
     * 只查找小于设置值的图片
     * 默认为最大值，即对最大文件体积无要求
     * 单位为Byte单位
     * */
    var imgMaxSize: Long = MAX_DEFAULT_LONG_VALUE

    /**
     * 对查找视频的最小宽度要求
     * 只查找大于设置值的视频
     * 默认为-1，即对最小宽度无要求
     * 单位像素值
     * */
    var videoMinWidth: Int = MIN_DEFAULT_INT_VALUE

    /**
     * 对查找视频的最大宽度要求
     * 只查找小于设置值的视频
     * 默认为最大值，即对最大宽度无要求
     * 单位像素值
     * */
    var videoMaxWidth: Int = MAX_DEFAULT_INT_VALUE

    /**
     * 对查找视频的最小高度要求
     * 只查找大于设置值的视频
     * 默认为-1，即对最小高度无要求
     * 单位像素值
     * */
    var videoMinHeight: Int = MIN_DEFAULT_INT_VALUE

    /**
     * 对查找视频的最大高度要求
     * 只查找小于设置值的视频
     * 默认为最大值，即对最大高度无要求
     * 单位像素值
     * */
    var videoMaxHeight: Int = MAX_DEFAULT_INT_VALUE

    /**
     * 对查找视频的最小文件体积要求
     * 只查找大于设置值的视频
     * 默认为-1，即对最小文件体积无要求
     * 单位为Byte单位
     * */
    var videoMinSize: Long = MIN_DEFAULT_LONG_VALUE

    /**
     * 对查找视频的最大文件体积要求
     * 只查找小于设置值的视频
     * 默认为最大值，即对最大文件体积无要求
     * 单位为Byte单位
     * */
    var videoMaxSize: Long = MAX_DEFAULT_LONG_VALUE


    /**
     * 对查找视频的最小时长要求
     * 只查找大于设置值的视频
     * 默认为0，即对最小视频时长无要求
     * 单位毫秒
     * */
    var videoMinDuration: Long = 0L

    /**
     * 对查找视频的最大时长要求
     * 只查找小于设置值的视频
     * 默认为最大值，即对最大视频时长无要求
     * 单位毫秒
     * */
    var videoMaxDuration: Long = MAX_DEFAULT_LONG_VALUE

    /**
     * 对查找媒体的最小宽度要求
     * 只查找小于设置值的媒体
     * 默认为-1，即对最小宽度无要求
     * 设置改值后，会覆盖
     * [imgMinWidth]
     * [videoMinWidth] 设置的值
     * 单位像素值
     * */
    var mediaMinWidth: Int = MIN_DEFAULT_INT_VALUE

    /**
     * 对查找媒体的最大宽度要求
     * 只查找小于设置值的媒体
     * 默认为Int.MAX_VALUE，即对最大宽度无要求
     * 设置改值后，会覆盖
     * [imgMaxWidth]
     * [videoMaxWidth] 设置的值
     * 单位像素值
     * */
    var mediaMaxWidth: Int = MAX_DEFAULT_INT_VALUE

    /**
     * 对查找媒体的最小高度要求
     * 只查找小于设置值的媒体
     * 默认为-1，即对最小高度无要求
     * 设置改值后，会覆盖
     * [imgMinHeight]
     * [videoMinHeight] 设置的值
     * 单位像素值
     * */
    var mediaMinHeight: Int = MIN_DEFAULT_INT_VALUE

    /**
     * 对查找媒体的最大高度要求
     * 只查找小于设置值的媒体
     * 默认为Int.MAX_VALUE，即对最大高度无要求
     * 设置改值后，会覆盖
     * [imgMaxHeight]
     * [videoMaxHeight] 设置的值
     * 单位像素值
     * */
    var mediaMaxHeight: Int = MAX_DEFAULT_INT_VALUE

    /**
     * 对查找媒体的最小文件体积要求
     * 只查找大于设置值的媒体
     * 默认为-1，即对最小文件体积无要求
     * 设置改值后，会覆盖
     * [imgMinSize]
     * [videoMinSize] 设置的值
     * 单位为Byte值
     * */
    var mediaMinSize: Long = MIN_DEFAULT_LONG_VALUE

    /**
     * 对查找媒体的最大文件体积要求
     * 只查找小于设置值的媒体
     * 默认为Long.MAX_VALUE，即对最大文件体积无要求
     * 设置该值后，会覆盖
     * [imgMaxSize]
     * [videoMaxSize] 设置的值
     * 单位为Byte值
     * */
    var mediaMaxSize: Long = MAX_DEFAULT_LONG_VALUE

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
     * 单位毫秒值
     * */
    var startTime: Long = MIN_DEFAULT_LONG_VALUE

    /**
     * 设置查找时间范围的结束时间
     * 单位为毫秒
     * 默认为-1，即不限制结束时间
     * 单位毫秒值
     * */
    var endTime: Long = MAX_DEFAULT_LONG_VALUE

    /**
     * 设置媒体列表排序规则
     * 默认为修改时间
     * 具体规则描述请参考
     * [QueryRule]
     * */
    var queryRule: QueryRule = QueryRule.MODIFY


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
package com.seanutf.media.queryprovider

/**
 * @Author seanutf
 * @Date 2022/2/11 9:38 下午
 * @Desc 查找媒体列表的排序规则
 */
enum class QueryRule {
    /**
     * 所有媒体目录或特定目录
     * 所有媒体文件按新增的时间排序
     * */
    ADD,

    /**
     * 所有媒体目录或特定目录排序
     * 所有媒体文件按修改的时间排序
     * 修改的媒体文件会新增为单独的一份副本保存
     * 源文件维持不动保存
     * */
    MODIFY,

    /**
     * 所有媒体目录或特定目录
     * 只搜索本机拍摄的媒体文件
     * 排序按新增时间处理
     * 为SDK API 29新增
     * API 29以下机型，
     * 本SDK默认为按[MODIFY]类型排序
     * */
    TAKEN,

    /**
     * 所有媒体目录或特定目录
     * 只搜索过期的媒体文件
     * 即被用户删除到垃圾箱的媒体文件
     * 排序按新增时间处理
     * 为SDK API 29新增
     * API 29以下机型，
     * 本SDK默认为按[MODIFY]类型排序
     * */
    EXPIRE
}
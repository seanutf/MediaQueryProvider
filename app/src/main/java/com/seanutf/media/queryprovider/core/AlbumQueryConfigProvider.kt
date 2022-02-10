package com.seanutf.media.queryprovider.core

import android.net.Uri
import android.provider.MediaStore
import com.seanutf.media.queryprovider.QueryMode
import com.seanutf.media.queryprovider.config.QueryConfig

/**
 * 媒体选择器中
 * 获取相册列表的参数提供者
 *
 * 与获取某个指定相册中的媒体列表
 * 代码相似
 * 但因为两者目的明显不同，
 * 所以为避免代码混乱和逻辑混淆
 * 将两者代码分开，方便理解
 * 如果一方代码配置有所改动
 * 记得检查另一方代码是否需要配合修改
 * */
class AlbumQueryConfigProvider {
    private var queryConfig: QueryConfig? = null

    private val orderBy: String = MediaStore.Files.FileColumns.DATE_ADDED + " DESC"

    fun setConfig(queryConfig: QueryConfig?) {
        this.queryConfig = queryConfig
    }

    fun getOrderBy(): String {
        return orderBy
    }

    fun getAlbumUri(): Uri {
        val uri: Uri
        when (queryConfig?.mode ?: QueryMode.IMG) {

            QueryMode.IMG -> {
                uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

            QueryMode.VIDEO -> {
                uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }

            QueryMode.ALL -> {
                uri = MediaStore.Files.getContentUri("external")
            }

            else -> {
                uri = MediaStore.Files.getContentUri("external")
            }
        }

        return uri
    }

    fun getAlbumProjection(): Array<String>? {
        val albumProjection: Array<String>?
        when (queryConfig?.mode ?: QueryMode.IMG) {

            QueryMode.IMG -> {
                albumProjection = getAlbumProjectionForImages()
            }

            QueryMode.VIDEO -> {
                albumProjection = getAlbumProjectionForVideos()
            }

            QueryMode.ALL -> {
                albumProjection = getAlbumProjectionForAllMedias()
            }

            else -> {
                albumProjection = getAlbumProjectionForAllMedias()
            }
        }

        return albumProjection
    }

    private fun getAlbumProjectionForImages(): Array<String>? {
        return null
        //使用下面注释的代码不能正确的返回结果值，待查
//        return if(RunTimeVersionUtil.isLargeApi29()){
//            arrayOf(
//                    MediaStore.Images.ImageColumns._ID,
//                    "bucket_id",
//                    "bucket_display_name",
//                    MediaStore.MediaColumns.MIME_TYPE)
//        } else {
//            arrayOf(
//                    MediaStore.Images.ImageColumns._ID,
//                    MediaStore.MediaColumns.DATA,
//                    "bucket_id",
//                    "bucket_display_name",
//                    MediaStore.MediaColumns.MIME_TYPE,
//                    "COUNT(*) AS " + "count")
//        }
    }

    private fun getAlbumProjectionForVideos(): Array<String>? {
        return null
        //使用下面注释的代码不能正确的返回结果值，待查
//        return if(RunTimeVersionUtil.isLargeApi29()){
//            arrayOf(
//                    MediaStore.Video.VideoColumns._ID,
//                    "bucket_id",
//                    "bucket_display_name",
//                    MediaStore.MediaColumns.MIME_TYPE)
//        } else {
//            arrayOf(
//                    MediaStore.Video.VideoColumns._ID,
//                    MediaStore.MediaColumns.DATA,
//                    "bucket_id",
//                    "bucket_display_name",
//                    MediaStore.MediaColumns.MIME_TYPE,
//                    "COUNT(*) AS " + "count")
//        }
    }

    private fun getAlbumProjectionForAllMedias(): Array<String>? {
        return null
//        return if(RunTimeVersionUtil.isLargeApi29()){
//            arrayOf(
//                    MediaStore.Files.FileColumns._ID,
//                    "bucket_id",
//                    "bucket_display_name",
//                    MediaStore.MediaColumns.MIME_TYPE)
//        } else {
//            arrayOf(
//                    MediaStore.Files.FileColumns._ID,
//                    MediaStore.MediaColumns.DATA,
//                    "bucket_id",
//                    "bucket_display_name",
//                    MediaStore.MediaColumns.MIME_TYPE,
//                    "COUNT(*) AS " + "count")
//        }
    }

    fun getSelection(): String {
        val selection: String
        when (queryConfig?.mode ?: QueryMode.IMG) {

            QueryMode.IMG -> {
                selection = getSelectionForImages()
            }

            QueryMode.VIDEO -> {
                selection = getSelectionForVideos()
            }

            QueryMode.ALL -> {
                selection = getSelectionForAllMedias()
            }

            else -> {
                selection = getSelectionForAllMedias()
            }
        }

        return selection
    }

    private fun getSelectionForAllMedias(): String {
        return "(media_type=? AND (${buildImgMimeTypes()})) OR (media_type=? AND (${buildVideoMimeTypes()}))"
    }

    private fun buildImgMimeTypes() = buildString {
        if (!queryConfig?.imgQueryFormatArray.isNullOrEmpty()) {
            for ((index, type) in (queryConfig?.imgQueryFormatArray ?: return@buildString).withIndex()) {
                append("mime_type='${type}'")
                if (index != (queryConfig?.imgQueryFormatArray ?: return@buildString).size - 1){
                    append(" OR ")
                }
            }
        } else {
            append("mime_type='image/png' OR mime_type='image/jpeg'")
        }
    }

    private fun buildVideoMimeTypes() = buildString {
        if (!queryConfig?.videoQueryFormatArray.isNullOrEmpty()) {
            for ((index, type) in (queryConfig?.videoQueryFormatArray ?: return@buildString).withIndex()) {
                append("mime_type='${type}'")
                if (index != (queryConfig?.videoQueryFormatArray ?: return@buildString).size - 1){
                    append(" OR ")
                }
            }
        } else {
            append("mime_type='video/mp4'")
        }
    }

    private fun getSelectionForImages(): String {
        return (MediaStore.Images.Media.MIME_TYPE + "=? or "
                + MediaStore.Images.Media.MIME_TYPE + "=?")
    }

    private fun getSelectionForVideos(): String {
        return (MediaStore.Video.Media.MIME_TYPE + "=?")
    }

    fun getSelectionArgs(): Array<String> {
        val selectionArgs: Array<String>
        when (queryConfig?.mode ?: QueryMode.IMG) {

            QueryMode.IMG -> {
                selectionArgs = getSelectionArgsForImages()
            }

            QueryMode.VIDEO -> {
                selectionArgs = getSelectionArgsForVideos()
            }

            QueryMode.ALL -> {
                selectionArgs = getSelectionArgsForAllMedias()
            }

            else -> {
                selectionArgs = getSelectionArgsForAllMedias()
            }
        }

        return selectionArgs
    }

    /**
     * 获取查找所有媒体的参数值
     * @return 外部配置指定类型，默认媒体库默认
     */
    private fun getSelectionArgsForAllMedias(): Array<String> {
        return arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
        )
    }

    /**
     * 获取查找图片的参数值
     * @return 外部配置指定类型，默认png和jpg
     */
    private fun getSelectionArgsForImages(): Array<String> {
        return queryConfig?.imgQueryFormatArray ?: arrayOf("image/png", "image/jpeg")
    }

    /**
     * 获取查找视频的参数值
     * @return 外部配置指定类型，默认mp4
     */
    private fun getSelectionArgsForVideos(): Array<String> {
        return queryConfig?.videoQueryFormatArray ?: arrayOf("video/mp4")
    }

}
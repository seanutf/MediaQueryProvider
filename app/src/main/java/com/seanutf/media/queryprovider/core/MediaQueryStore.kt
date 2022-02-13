package com.seanutf.media.queryprovider.core

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentResolver
import android.database.Cursor
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.database.getStringOrNull
import com.seanutf.media.queryprovider.QueryMode
import com.seanutf.media.queryprovider.config.QueryConfig
import com.seanutf.media.queryprovider.data.Album
import com.seanutf.media.queryprovider.data.Media
import java.util.*

/**
 * 真正与系统媒体库Api 交互
 * 的内部查询器
 * */
class MediaQueryStore {

    private var queryConfig: QueryConfig? = null
    private var allAlbumList: List<Album>? = null
    private var context: Application? = null
    private var mediaPlayer: MediaPlayer? = null

    /**
     * 设置查找的数据参数
     *
     * [queryConfig] 查找数据的参数配置
     * */
    fun setConfig(context: Application?, queryConfig: QueryConfig?) {
        this.context = context
        this.queryConfig = queryConfig
    }

    /**
     * 获取相册封面的真实地址
     * @param cursor 游标
     * @return 文件的真实地址的字符串
     */
    @SuppressLint("Range")
    private fun getFirstUrl(cursor: Cursor): String {
        return cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))
    }

    /**
     * 获取相册封面的文件扩展类型
     *
     * @param cursor 游标
     * @return 文件扩展类型的字符串
     */
    @SuppressLint("Range")
    private fun getFirstCoverMimeType(cursor: Cursor): String {
        return cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE))
    }

    /**
     * [mediaUri] 所查找媒体的类型
     * [projection] 所查找数据库的列
     * [selection] 所查找媒体的参数
     * [selectionArgs] 所查找媒体的参数的值
     * [sortOrder] 所查找媒体的排列规则
     * @return 媒体目录的列表，获取异常或失败时返回 null
     * */
    fun queryMedias(
        loadAlbum: Boolean,
        mediaUri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String
    ): MutableList<Media>? {
        var cursor: Cursor? = null
        try {
            cursor = if (Build.VERSION.SDK_INT >= 30) {
                val queryArgs: Bundle = createQueryArgsBundle(selection, selectionArgs)
                context?.contentResolver?.query(mediaUri, projection, queryArgs, null)
            } else {
                context?.contentResolver?.query(
                    mediaUri,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder
                )
            }
            return queryMediaList(cursor ?: return null, loadAlbum)
        } catch (e: Exception) {
            Log.e("MediaQuery", "loadMediaList Data Error: " + e.message)
            e.printStackTrace()
            return null
        } finally {
            if (mediaPlayer != null) {
                mediaPlayer = null
            }

            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
        }
    }

    /**
     * R  createQueryArgsBundle
     *
     * @param selection
     * @param selectionArgs
     * @param limitCount
     * @param offset
     * @return
     */
    private fun createQueryArgsBundle(selection: String?, selectionArgs: Array<String>?): Bundle {
        val queryArgs = Bundle()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            queryArgs.putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection)
            queryArgs.putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selectionArgs)
            queryArgs.putString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER, MediaStore.Files.FileColumns._ID + " DESC")
        }
        return queryArgs
    }

    /**
     * 查询用户媒体库中
     * 所有的媒体文件目录
     *
     * @param cursor 游标
     * @return 媒体目录的列表
     * */
    @SuppressLint("Range")
    private fun queryMediaList(cursor: Cursor, loadAlbum: Boolean): MutableList<Media> {

        val mediaItems: MutableList<Media> = mutableListOf()
        val bucketIdMap: MutableMap<Long, Int> = mutableMapOf()
        var needGetVideoCover = true
        var videoTotalCount = 0

        val mediaAlbums: MutableList<Album> = ArrayList<Album>()
        val allVideoAlbum = Album()
        val allMediaAlbum = Album()
        val allImageAlbum = Album()


        while (cursor.moveToNext()) {

            val basicRequires: Array<String> = checkBasicRequires(cursor) ?: continue

            val absolutePath = basicRequires[0]
            val mimeType = basicRequires[1]


            var width = 0
            var height = 0
            var videoDuration = 0L

            Log.i("MediaQuery", "load media absolutePath is: $absolutePath")
            if (mimeType.contains("video")) {
                Log.i("MediaQuery", "load media  is: video")
                width = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.WIDTH))
                height = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.HEIGHT))
                videoDuration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))
                Log.i("MediaQuery", "load video pre info is: width: $width, and height is: $height, and videoDuration is: $videoDuration")
                //这里代码是对例如小红书这类不规范App的兼容
                //猜测小红书保存视频时是直写数据库，而非Uri通知形式，
                //而且写入时，视频的宽高时长都为0，
                // 导致后续其他App读取媒体库时，能读到视频文件但读不到视频时长，影响用户体验
                //所以采用MediaPlayer再次获取视频信息
                if (width == 0 || height == 0 || videoDuration == 0L) {
                    if (width == 0) {
                        Log.i("MediaQuery", "load video pre info is: width: 0")
                    }

                    if (height == 0) {
                        Log.i("MediaQuery", "load video pre info is: height: 0")
                    }

                    if (videoDuration == 0L) {
                        Log.i("MediaQuery", "load video pre info is: videoDuration: 0")
                    }
                    if (mediaPlayer == null) {
                        Log.i("MediaQuery", "load video pre info is: current mediaPlayer is null")
                        mediaPlayer = MediaPlayer()
                    } else {
                        Log.i("MediaQuery", "load video pre info is: current mediaPlayer not null")
                    }

                    if (mediaPlayer == null) {
                        Log.i("MediaQuery", "load video pre info is: inited mediaPlayer is null")
                    } else {
                        Log.i("MediaQuery", "load video pre info is: inited mediaPlayer not null")
                    }
                    mediaPlayer?.run {
                        Log.i("MediaQuery", "load video pre info is: will call setDataSource($absolutePath)")
                        setDataSource(absolutePath)
                        Log.i("MediaQuery", "load video pre info is: will call prepare()")
                        prepare()
                        Log.i("MediaQuery", "load video pre info is: is called prepare()")
                        width = videoWidth
                        Log.i("MediaQuery", "load video pre info is: get new info width: $width")
                        height = videoHeight
                        Log.i("MediaQuery", "load video pre info is: get new info height: $height")
                        videoDuration = duration * 1000L
                        Log.i("MediaQuery", "load video pre info is: get new info videoDuration: $videoDuration")
                        Log.i("MediaQuery", "load video pre info is: will call reset()")
                        reset()
                        Log.i("MediaQuery", "load video pre info is: will call release()")
                        release()
                    }

                    Log.i("MediaQuery", "load video pre info is: reget info end")
                    if (!checkVideoDurationConfigs(videoDuration)) {
                        Log.i("MediaQuery", "load video pre info is: checkVideoDurationConfigs false")
                        continue
                    } else {
                        Log.i("MediaQuery", "load video pre info is: checkVideoDurationConfigs true")
                    }
                } else {
                    Log.i("MediaQuery", "load video pre info is: width != 0,and height != 0, and videoDuration != 0")
                }
            } else {
                Log.i("MediaQuery", "load media  is: img")
                width = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.WIDTH))
                height = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.HEIGHT))
                Log.i("MediaQuery", "load img pre info is: width: $width, and height is: $height ")
            }

            Log.i("MediaQuery", "load media pre info is: end")
            val customIntConfigs: Array<Int> = checkCustomIntConfigs(width, height, mimeType) ?: continue

            val mediaWidth = customIntConfigs[0]
            val mediaHeight = customIntConfigs[1]

            val customLongConfigs: Array<Long> = checkCustomLongConfigs(cursor, mimeType) ?: continue

            val size = customLongConfigs[0]
            val dateModified = customLongConfigs[1]

            val mediaName: String = checkCustomStringConfigs(cursor) ?: continue
            Log.i("MediaQuery", "load media pre info is: all end")
            //以上代码是根据固定基础要求和灵活配置获取的单个媒体的信息
            //以便能够快速排除一些不符合当前配置和要求的媒体文件
            //以下代码是对文件夹和媒体文件的处理

            val bucketId: Long = cursor.getLong(cursor.getColumnIndexOrThrow("bucket_id"))
            val bucketDisplayName: String? = cursor.getString(cursor.getColumnIndexOrThrow("bucket_display_name"))

            if (loadAlbum) {
                when (queryConfig?.mode ?: QueryMode.IMG) {

                    QueryMode.IMG -> {
                        allImageAlbum.bucketId = -1
                        if (cursor.isFirst) {
                            allImageAlbum.firstImagePath = getFirstUrl(cursor)
                            allImageAlbum.firstMimeType = getFirstCoverMimeType(cursor)
                            allImageAlbum.name = "所有图片"
                            mediaAlbums.add(0, allImageAlbum)
                            bucketIdMap[-1] = 1
                        }
                    }

                    QueryMode.VIDEO -> {
                        allVideoAlbum.bucketId = -1
                        if (cursor.isFirst) {
                            allVideoAlbum.firstImagePath = getFirstUrl(cursor)
                            allVideoAlbum.firstMimeType = getFirstCoverMimeType(cursor)
                            allVideoAlbum.name = "所有视频"
                            mediaAlbums.add(0, allVideoAlbum)
                            bucketIdMap[-1] = 1
                        }
                    }

                    QueryMode.ALL -> {
                        allMediaAlbum.bucketId = -1
                        allVideoAlbum.bucketId = -2

                        if (cursor.isFirst) {
                            allMediaAlbum.firstImagePath = getFirstUrl(cursor)
                            allMediaAlbum.firstMimeType = getFirstCoverMimeType(cursor)
                            allMediaAlbum.name = "图片和视频"
                            allVideoAlbum.name = "所有视频"
                            mediaAlbums.add(0, allMediaAlbum)
                            mediaAlbums.add(1, allVideoAlbum)
                            bucketIdMap[-1] = 1
                        }
                    }
                }

                if (!bucketIdMap.contains(bucketId)) {
                    bucketIdMap[bucketId] = 1

                    val album = Album()
                    album.bucketId = bucketId
                    //val id: Long = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID))
                    album.name = bucketDisplayName
                    album.firstImagePath = absolutePath
                    album.firstMimeType = mimeType
                    mediaAlbums.add(album)
                } else {
                    bucketIdMap[bucketId] = bucketIdMap[bucketId]?.plus(1) ?: 1
                }

                bucketIdMap[-1] = bucketIdMap[-1]?.plus(1) ?: 1

                if (queryConfig?.mode == QueryMode.ALL && mimeType.startsWith("video")) {
                    videoTotalCount += 1

                    if (mediaAlbums.size >= 2 && needGetVideoCover) {
                        needGetVideoCover = false
                        mediaAlbums[1].firstImagePath = getFirstUrl(cursor)
                        mediaAlbums[1].firstMimeType = getFirstCoverMimeType(cursor)
                    }
                }
            }

            val mediaItem = buildMedia(cursor, absolutePath, mimeType, mediaWidth, mediaHeight, size, dateModified, mediaName, videoDuration)
            mediaItems.add(mediaItem)
        }


        if (loadAlbum) {
            for ((id, count) in bucketIdMap) {
                mediaAlbums.forEach {
                    if (it.bucketId == id) {
                        it.count = count
                    }
                    it.bucketId
                }
            }

            allAlbumList = mediaAlbums

            if (queryConfig?.mode == QueryMode.ALL && mediaAlbums.size >= 2) {
                mediaAlbums[1].count = videoTotalCount
            }
        }

        return mediaItems
    }

    private fun checkCustomStringConfigs(cursor: Cursor): String? {
        val name = cursor.getStringOrNull(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))

        if (!queryConfig?.queryName.isNullOrBlank()) {
            if (!name.isNullOrBlank() && name.contains((queryConfig ?: return null).queryName ?: return null)) {
                return name
            } else {
                return null
            }
        } else {
            return name
        }
    }

    private fun checkVideoDurationConfigs(duration: Long): Boolean {
        queryConfig?.run {
            if (duration in videoMinDuration..videoMaxDuration) {
                return true
            }
        }
        return false
    }

    private fun checkCustomLongConfigs(cursor: Cursor, mimeType: String): Array<Long>? {
        val size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE))
        val dateModified = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED))

        if (mimeType.contains("video")) {
            queryConfig?.run {
                if (size < videoMinSize ||
                    size > videoMaxSize ||
                    dateModified < startTime ||
                    dateModified > endTime
                ) {
                    return null
                }
            }
        } else {
            queryConfig?.run {
                if (size < imgMinSize ||
                    size > imgMaxSize ||
                    dateModified < startTime ||
                    dateModified > endTime
                ) {
                    return null
                }
            }
        }

        return arrayOf(size, dateModified)
    }

    private fun checkCustomIntConfigs(width: Int, height: Int, mimeType: String): Array<Int>? {
        if (mimeType.contains("video")) {
            queryConfig?.run {
                if (width < videoMinWidth ||
                    width > videoMaxWidth ||
                    height < videoMinHeight ||
                    height > videoMaxHeight
                ) {
                    return null
                }
            }
        } else {
            queryConfig?.run {
                if (width < imgMinWidth ||
                    width > imgMaxWidth ||
                    height < imgMinHeight ||
                    height > imgMaxHeight
                ) {
                    return null
                }
            }
        }

        return arrayOf(width, height)
    }

    private fun buildMedia(
        cursor: Cursor,
        absolutePath: String,
        mimeType: String,
        mediaWidth: Int,
        mediaHeight: Int,
        size: Long,
        dateModified: Long,
        mediaName: String,
        videoDuration: Long,
    ): Media {

        val mediaItem = Media()
        mediaItem.mediaPath = absolutePath
        mediaItem.name = mediaName
        mediaItem.size = size
        mediaItem.mediaWidth = mediaWidth
        mediaItem.mediaHeight = mediaHeight
        mediaItem.dateModified = dateModified

        if (mimeType.contains("video")) {
            mediaItem.duration = videoDuration
            mediaItem.artist = cursor.getStringOrNull(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST))
        }


        if (mimeType.contains("video")) {
            mediaItem.mediaType = Media.TYPE_MEDIA_VIDEO
        } else if (mimeType.contains("image")) {
            mediaItem.mediaType = Media.TYPE_MEDIA_IMAGE
        }

        return mediaItem
    }

    /**
     * 这个方法是要求
     * 某个媒体文件无论在什么筛选条件下都要符合下面的要求
     * 即如果某个文件系统认为是媒体文件，如果不符合下面的要求
     * 我这里也不认为是媒体文件，因为这种媒体文件本身场景
     * 可能太过极端化：
     * 1.没有完整文件路径
     * 2.文件路径包含 thumb，可能为临时缓存文件
     * 3.获取不到 mimeType，后续的媒体文件筛选过程依赖mimeType
     * 如果获取不到，无法进行处理，相比其他媒体筛选器可能会"丢失"媒体文件
     *
     * */
    private fun checkBasicRequires(cursor: Cursor): Array<String>? {
        val absolutePath: String? = cursor.getStringOrNull(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))

        if (absolutePath == null || absolutePath.contains("_.thumbnails") || absolutePath.contains("/thumb")) {
            return null
        }

        var mimeType: String? = cursor.getStringOrNull(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE))

        if (mimeType == null) {
            //在一加7 plus pro 双开微信时，双开微信下载的图片获取不到mimeType
            //这样描述仅说明复现场景，不能说明仅仅是这一个机型的问题
            //从数据库获取mimeType为空时，利用工具再次获取一次，如果还为null，则过滤当前文件
            val extension: String = MimeTypeMap.getFileExtensionFromUrl(absolutePath) ?: return null
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: return null
        }

        return arrayOf(absolutePath, mimeType)
    }

    fun getAlbumList(): List<Album>? {
        return allAlbumList
    }
}
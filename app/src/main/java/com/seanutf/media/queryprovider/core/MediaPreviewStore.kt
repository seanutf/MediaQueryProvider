package com.seanutf.media.queryprovider.core

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentResolver
import android.database.Cursor
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


class MediaPreviewStore {

    private var queryConfig: QueryConfig? = null
    private var allAlbumList: List<Album>? = null
    private var context: Application? = null

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
     * [mediaUri] 所查找媒体的类型
     * [projection] 所查找数据库的列
     * [selection] 所查找媒体的参数
     * [selectionArgs] 所查找媒体的参数的值
     * [sortOrder] 所查找媒体的排列规则
     * @return 媒体目录的列表，获取异常或失败时返回 null
     * */
    fun queryAlbum(
        mediaUri: Uri, projection: Array<String>?, selection: String, selectionArgs: Array<String>,
        sortOrder: String
    ): List<Album>? {

        var cursor: Cursor? = null
        try {
            cursor = context?.contentResolver?.query(
                mediaUri,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )
            return queryAlbum(cursor!!)
        } catch (e: Exception) {
            Log.e("MediaPreview", "loadMediaAlbums Data Error: " + e.message)
            return null
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
        }
    }

    /**
     * 查询用户媒体库中
     * 所有的媒体文件目录
     *
     * @param cursor 游标
     * @return 媒体目录的列表
     * */
    @SuppressLint("Range")
    private fun queryAlbum(cursor: Cursor): List<Album> {
        val count = cursor.count
        var totalCount = 0
        var videoTotalCount = 0
        val mediaAlbums: MutableList<Album> = ArrayList<Album>()
        if (count > 0) {
            val countMap: MutableMap<Long, Long> = HashMap()
            while (cursor.moveToNext()) {
                val bucketId: Long = cursor.getLong(cursor.getColumnIndex("bucket_id"))
                val mimeType: String = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE))
                if (queryConfig?.mode == QueryMode.ALL && !mimeType.startsWith("image")) {
                    videoTotalCount += 1
                }
                var newCount = countMap[bucketId]
                if (newCount == null) {
                    newCount = 1L
                } else {
                    newCount++
                }
                countMap[bucketId] = newCount
            }

            if (cursor.moveToFirst()) {
                val hashSet: MutableSet<Long> = HashSet()
                do {
                    //这里没有用MediaStore.MediaColumns.BUCKET_ID,是因为
                    //MediaStore.MediaColumns.BUCKET_ID这个父级常量值在 API 29才有
                    //而"bucket_id"之前就有，只不过处于子级
                    //在MediaStore.Images.Media.BUCKET_ID或MediaStore.Video.Media.BUCKET_ID中
                    //"bucket_display_name"同理

                    val bucketId: Long = cursor.getLong(cursor.getColumnIndex("bucket_id"))
                    if (hashSet.contains(bucketId)) {
                        continue
                    }
                    val album = Album()
                    album.bucketId = bucketId
                    val bucketDisplayName: String? = cursor.getString(
                        cursor.getColumnIndex("bucket_display_name")
                    )
                    val mimeType: String = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE))
                    val size = countMap[bucketId]!!
                    //val id: Long = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID))
                    album.name = bucketDisplayName
                    album.count = size.toInt()
                    val url = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))
                    album.firstImagePath = url
                    album.firstMimeType = mimeType
                    mediaAlbums.add(album)
                    hashSet.add(bucketId)
                    totalCount += size.toInt()
                } while (cursor.moveToNext())
            }

//            else {
//                cursor.moveToFirst()
//                do {
//                    val album = AlbumData()
//                    val bucketId = cursor.getLong(cursor.getColumnIndex("bucket_id"))
//                    val bucketDisplayName = cursor.getString(cursor.getColumnIndex("bucket_display_name"))
//                    val mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE))
//                    val size = cursor.getInt(cursor.getColumnIndex("count"))
//                    album.bucketId = bucketId
//                    val url = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))
//                    album.firstImagePath = url
//                    album.name = bucketDisplayName
//                    album.firstMimeType = mimeType
//                    album.count = size
//                    mediaAlbums.add(album)
//                    totalCount += size
//                    if(dataConfig?.mode == SelectMode.ALL && mimeType.contains("video")){
//                        videoTotalCount += size
//                    }
//                } while (cursor.moveToNext())
//            }


            when (queryConfig?.mode) {
                QueryMode.IMG -> {
                    // 所有图片文件夹
                    val allImageAlbum = generateAllImageAlbum(cursor, totalCount)
                    allImageAlbum.isChecked = true
                    allImageAlbum.isSelected = true
                    mediaAlbums.add(0, allImageAlbum)
                }

                QueryMode.VIDEO -> {
                    // 所有视频文件夹
                    val allVideoAlbum = generateAllVideoAlbum(cursor, totalCount)
                    allVideoAlbum.isChecked = true
                    allVideoAlbum.isSelected = true
                    mediaAlbums.add(0, allVideoAlbum)
                }

                QueryMode.ALL -> {
                    // 图片和视频文件夹
                    val allMediaAlbum = generateAllMediaAlbum(cursor, totalCount)
                    allMediaAlbum.isChecked = true
                    allMediaAlbum.isSelected = true
                    mediaAlbums.add(0, allMediaAlbum)

                    // 所有视频文件夹
                    val allVideoAlbum = generateAllVideoAlbum(cursor, videoTotalCount)
                    mediaAlbums.add(1, allVideoAlbum)
                }
            }

            return mediaAlbums
        }

        return ArrayList<Album>()
    }

    /**
     * 生成全部图片相册(包含全部的符合查询规则的图片媒体)
     * 即: "所有图片"
     * 其中的 bucketId = -1
     * 为本选择器默认的所有自定义文件夹的规则
     * 其值会在之后的获取某个指定文件夹的媒体列表中使用
     * 如果需要更改该规则，需要更改全部自定义文件夹
     *
     * @param cursor 游标
     * @param totalCount 全部图片的数量
     * @return 媒体目录数据结构
     * */
    private fun generateAllImageAlbum(cursor: Cursor, totalCount: Int): Album {
        val allImageAlbum = Album()
        allImageAlbum.count = totalCount
        allImageAlbum.bucketId = -1
        if (cursor.moveToFirst()) {
            allImageAlbum.firstImagePath = getFirstUrl(cursor)
            allImageAlbum.firstMimeType = getFirstCoverMimeType(cursor)
        }

        allImageAlbum.name = "所有图片"

        return allImageAlbum
    }

    /**
     * 生成全部视频相册(包含全部的符合查询规则的视频媒体)
     * 即: "所有视频"
     * 其中的 bucketId = -1
     * 为本选择器默认的所有自定义文件夹的规则
     * 其值会在之后的获取某个指定文件夹的媒体列表中使用
     * 如果需要更改该规则，需要更改全部自定义文件夹
     *
     * 特别注意：在查询为全部媒体时也会生成此文件夹
     * 为什么呢？猜测是：
     * 最初的的开发者开发媒体选择功能时
     * 仅知道 MediaStore.Images.Media.EXTERNAL_CONTENT_URI
     * 和 MediaStore.Video.Media.EXTERNAL_CONTENT_URI
     * 不知道 MediaStore.Files.getContentUri("external")
     * 所以在通过相册查询相册内的媒体列表时，他只能
     * 二选一去查询，因为明显相册会更多一些
     * 所以通过相册查询相册内的媒体列表时，他选择了使用
     * MediaStore.Images.Media.EXTERNAL_CONTENT_URI
     * 这样就会把 同一个相册下的视频漏掉，没有展示
     * 所以就又单加一个全部视频的相册目录
     * 至于旧有选择媒体库中，全部列表中同时可以罗列
     * 图片和视频
     * 是因为开发者先获取全部视频再获取全部图片，
     * 再用自定义算法将两个列表按时间穿插排列组合
     * 具体可以查看旧有媒体选择库
     * 新的媒体选择库为了兼容用户体验
     * 保留了这一特性
     *
     * @param cursor 游标
     * @param videoTotalCount 全部视频的数量
     * @return 媒体目录数据结构
     * */
    private fun generateAllVideoAlbum(cursor: Cursor, videoTotalCount: Int): Album {
        val allVideoAlbum = Album()
        allVideoAlbum.count = videoTotalCount
        allVideoAlbum.bucketId = -1
        if (cursor.moveToFirst()) {
            allVideoAlbum.firstImagePath = getFirstUrl(cursor)
            allVideoAlbum.firstMimeType = getFirstCoverMimeType(cursor)
        }

        allVideoAlbum.name = "所有视频"

        return allVideoAlbum
    }

    /**
     * 生成全部媒体相册(包含全部的符合查询规则的图片、视频等媒体)
     * 即: "图片和视频"或 "相机胶卷"
     * 其中的 bucketId = -1
     * 为本选择器默认的所有自定义文件夹的规则
     * 其值会在之后的获取某个指定文件夹的媒体列表中使用
     * 如果需要更改该规则，需要更改全部自定义文件夹
     *
     * @param cursor 游标
     * @param totalCount 全部媒体的数量
     * @return 媒体目录数据结构
     * */
    private fun generateAllMediaAlbum(cursor: Cursor, totalCount: Int): Album {
        val allMediaAlbum = Album()
        allMediaAlbum.count = totalCount
        allMediaAlbum.bucketId = -1
        if (cursor.moveToFirst()) {
            allMediaAlbum.firstImagePath = getFirstUrl(cursor)
            allMediaAlbum.firstMimeType = getFirstCoverMimeType(cursor)
        }

        allMediaAlbum.name = "图片和视频"

        return allMediaAlbum
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
     * 针对 Android Q 以上的系统
     * 利用 MediaStore.Files.FileColumns._ID 获取的id
     * 生成真实的文件路径
     * 但测试发现并没有生成真实文件路径
     * 而是：Uri地址
     * 所以放弃使用
     * 暂留
     * id MediaStore.Files.FileColumns._ID 获取的id
     */
//    private fun getRealPath(id: Long): String {
//        return MediaStore.Files.getContentUri("external").buildUpon().appendPath(id.toString()).build().toString()
//    }


    /**
     * [mediaUri] 所查找媒体的类型
     * [projection] 所查找数据库的列
     * [selection] 所查找媒体的参数
     * [selectionArgs] 所查找媒体的参数的值
     * [sortOrder] 所查找媒体的排列规则
     * @return 媒体目录的列表，获取异常或失败时返回 null
     * */
    fun queryMedias(
        mediaUri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String
    ): MutableList<Media>? {
        var cursor: Cursor? = null
        try {
            cursor = context?.contentResolver?.query(
                mediaUri,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )
            return queryMediaList(cursor!!)
        } catch (e: Exception) {
            Log.e("MediaPreview", "loadMediaList Data Error: " + e.message)
            return null
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
        }
    }

    @SuppressLint("Range")
    private fun queryMediaList(cursor: Cursor): MutableList<Media> {
        val mediaItems: MutableList<Media> = mutableListOf()

        while (cursor.moveToNext()) {
            val absolutePath = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA)) ?: continue

            val mediaItem = Media()
            mediaItem.mediaPath = absolutePath

            val mimeType: String = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE))

            mediaItem.name = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME))
            mediaItem.size = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE))
            mediaItem.mediaWidth = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns.WIDTH))
            mediaItem.mediaHeight = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns.HEIGHT))
            mediaItem.dateModified = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED))

            if (mimeType.contains("video")) {
                mediaItem.duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))
                mediaItem.artist = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.ARTIST))
            }


            if (mimeType.contains("video")) {
                mediaItem.mediaType = Media.TYPE_MEDIA_VIDEO
            } else if (mimeType.contains("image")) {
                mediaItem.mediaType = Media.TYPE_MEDIA_IMAGE
            }

            mediaItems.add(mediaItem)
        }

        return mediaItems
    }

    /**
     * [mediaUri] 所查找媒体的类型
     * [projection] 所查找数据库的列
     * [selection] 所查找媒体的参数
     * [selectionArgs] 所查找媒体的参数的值
     * [sortOrder] 所查找媒体的排列规则
     * @return 媒体目录的列表，获取异常或失败时返回 null
     * */
    fun queryMedias2(
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
            return queryMediaList2(cursor!!, loadAlbum)
        } catch (e: Exception) {
            Log.e("MediaPreview", "loadMediaList Data Error: " + e.message)
            return null
        } finally {
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
    private fun queryMediaList2(cursor: Cursor, loadAlbum: Boolean): MutableList<Media> {

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

            val customIntConfigs: Array<Int> = checkCustomIntConfigs(cursor, mimeType) ?: continue

            val mediaWidth = customIntConfigs[0]
            val mediaHeight = customIntConfigs[1]

            val customLongConfigs: Array<Long> = checkCustomLongConfigs(cursor, mimeType) ?: continue

            val size = customLongConfigs[0]
            val dateModified = customLongConfigs[1]

            val mediaName: String = checkCustomStringConfigs(cursor) ?: continue

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

            val mediaItem = buildMedia(cursor, absolutePath, mimeType, mediaWidth, mediaHeight, size, dateModified, mediaName)
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

    private fun checkCustomIntConfigs(cursor: Cursor, mimeType: String): Array<Int>? {
        val width = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.WIDTH))
        val height = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.HEIGHT))

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
        mediaName: String
    ): Media {

        val mediaItem = Media()
        mediaItem.mediaPath = absolutePath
        mediaItem.name = mediaName
        mediaItem.size = size
        mediaItem.mediaWidth = mediaWidth
        mediaItem.mediaHeight = mediaHeight
        mediaItem.dateModified = dateModified

        if (mimeType.contains("video")) {
            mediaItem.duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))
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
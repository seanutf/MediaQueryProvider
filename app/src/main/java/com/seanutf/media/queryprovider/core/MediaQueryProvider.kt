package com.seanutf.media.queryprovider.core

import android.app.Application
import com.seanutf.media.queryprovider.config.QueryConfig
import com.seanutf.media.queryprovider.data.Album
import com.seanutf.media.queryprovider.data.Media

/**
 * 对外提供的查询器，
 * 用于外部设置查询配置
 * */
class MediaQueryProvider {

    private val store = MediaQueryStore()
    private val mediasQuery = MediasQueryConfigProvider()
    private var queryConfig: QueryConfig? = null

    fun setConfig(context: Application?, queryConfig: QueryConfig?) {
        queryConfig?.run {
            transSetting()
        }
        this.queryConfig = queryConfig
        mediasQuery.setConfig(queryConfig)
        store.setConfig(context, queryConfig)
    }

    fun loadAlbumMedias(bucketId: Long, loadAlbum: Boolean): List<Media>? {
        if (queryConfig == null) {
            return null
        }
        return store.queryMedias(
            loadAlbum,
            mediasQuery.getMediasUri(),
            mediasQuery.getMediasProjection(),
            mediasQuery.getSelection(bucketId),
            mediasQuery.getSelectionArgs(bucketId),
            mediasQuery.getOrderBy()
        )
    }

    fun getAlbumList(): List<Album>? {
        return store.getAlbumList()
    }
}
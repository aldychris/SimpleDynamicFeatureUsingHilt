package com.github.aldychris.dynamicfeature1.core.network

import io.reactivex.Single
import retrofit2.http.GET

interface SomeApiInFeature1 {
    @GET("v1/list2")
    fun getDataList(): Single<ListResponse>
}

data class ListResponse(
    var data: String?
)
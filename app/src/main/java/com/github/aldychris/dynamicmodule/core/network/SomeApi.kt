package com.github.aldychris.dynamicmodule.core.network

import io.reactivex.Single
import retrofit2.http.GET

interface SomeApi {
    @GET("v1/list")
    fun getDataList(): Single<ListResponse>
}

data class ListResponse(
    var data: String?
)
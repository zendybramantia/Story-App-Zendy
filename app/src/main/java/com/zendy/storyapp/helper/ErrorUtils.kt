package com.zendy.storyapp.helper

import com.zendy.storyapp.data.network.APIError
import com.zendy.storyapp.data.network.ApiConfig
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import java.io.IOException


object ErrorUtils {
    fun parseError(response: Response<*>): APIError? {
        val converter: Converter<ResponseBody?, APIError> = ApiConfig.getRetrofit()
            .responseBodyConverter(APIError::class.java, arrayOfNulls<Annotation>(0))
        val error: APIError? = try {
            converter.convert(response.errorBody())
        } catch (e: IOException) {
            return APIError()
        }
        return error
    }
}
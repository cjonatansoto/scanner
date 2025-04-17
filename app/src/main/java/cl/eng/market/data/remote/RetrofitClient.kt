package cl.eng.market.data.remote

import android.content.Context
import cl.eng.market.R
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    fun getInstance(context: Context): ProductoApi {
        val baseUrl = context.getString(R.string.base_url)

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProductoApi::class.java)
    }
}
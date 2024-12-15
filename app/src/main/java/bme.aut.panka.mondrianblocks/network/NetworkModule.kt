package bme.aut.panka.mondrianblocks.network

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private var gson: Gson = GsonBuilder().setLenient().create()

    @SuppressLint("ServiceCast")
    @Provides
    @Singleton
    fun provideNetworkChecker(@ApplicationContext context: Context): NetworkChecker {
        return NetworkChecker(context) { ctx ->
            val connectivityManager = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return@NetworkChecker null
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return@NetworkChecker null
            NetworkCapabilitiesWrapper(networkCapabilities)
        }
    }

    @Provides
    @Singleton
    fun provideApiService(client: OkHttpClient): ApiService {
        return Retrofit.Builder()
            .baseUrl("https://coglica.aut.bme.hu")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val sharedPreferences =
                    context.getSharedPreferences("coglica_prefs", Context.MODE_PRIVATE)
                val authToken = sharedPreferences.getString("access_token", "") ?: ""
                Log.d("AuthToken", "Using token: $authToken")

                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $authToken")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor { chain ->
                val response = chain.proceed(chain.request())
                if (!response.isSuccessful) {
                    val errorBody = response.body()
                    val errorString = errorBody?.string() ?: "Unknown error"
                    Log.e("NetworkError", "Error: $errorString")
                    response.newBuilder()
                        .body(ResponseBody.create(errorBody?.contentType(), errorString)).build()
                } else {
                    response
                }
            }
            .build()

    }
}
package bme.aut.panka.mondrianblocks.network

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
    fun provideApiService(): ApiService {
        return Retrofit.Builder().baseUrl("https://icanhazdadjoke.com")
            .addConverterFactory(GsonConverterFactory.create(gson)).build()
            .create(ApiService::class.java)
    }

}
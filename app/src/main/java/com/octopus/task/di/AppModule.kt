package com.rubu.Play.di

import android.content.Context
import androidx.room.Room
import com.octopus.task.helpers.PreferencesHelper
import com.octopus.task.remote.APIInterface
import com.octopus.task.storage.dao.PlaylistDAO
import com.octopus.task.storage.database.PlaylistDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.CipherSuite
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun injectRetrofitAPI(@ApplicationContext context: Context): APIInterface {

        val spec = listOf<ConnectionSpec>(
            ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
                .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_1, TlsVersion.TLS_1_0)
                .cipherSuites(
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256,
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA
                )
                .build(), ConnectionSpec.CLEARTEXT, ConnectionSpec.MODERN_TLS
        )
        val okhttp = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
        //.connectionSpecs(spec)

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return Retrofit.Builder()
            .baseUrl("sadasda")
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okhttp)
            .build()
            .create(APIInterface::class.java)
    }

/*    @Singleton
    @Provides
    fun BoardRepositoryProvider(
        @ApplicationContext context: Context,
        retrofitAPI: APIInterface,
        playlistDao: PlaylistDAO,
        preferencesHelper: PreferencesHelper,
    ): BoardRepository = BoardRepository_Impl(
        context,
        retrofitAPI,
        playlistDao,
        preferencesHelper,
    )*/

    @Singleton
    @Provides
    fun injectPlaylistDAO(database: PlaylistDB) = database.playlistDao()

    @Singleton
    @Provides
    fun injectPlaylistDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context, PlaylistDB::class.java, "playlistdb"
        ).fallbackToDestructiveMigration().build()

}
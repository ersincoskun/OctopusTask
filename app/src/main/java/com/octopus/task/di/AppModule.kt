package com.rubu.Play.di

import android.content.Context
import androidx.room.Room
import com.octopus.task.helpers.PreferencesHelper
import com.octopus.task.remote.ApiInterface
import com.octopus.task.repo.CommonRepository
import com.octopus.task.repo.CommonRepository_Impl
import com.octopus.task.storage.dao.PlaylistDAO
import com.octopus.task.storage.database.PlaylistDB
import com.octopus.task.utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
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
    fun injectRetrofitAPI(@ApplicationContext context: Context): ApiInterface {
        val okhttp = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okhttp)
            .build()
            .create(ApiInterface::class.java)
    }

    @Singleton
    @Provides
    fun CommonRepositoryProvider(
        @ApplicationContext context: Context,
        retrofitAPI: ApiInterface,
        playlistDao: PlaylistDAO,
        preferencesHelper: PreferencesHelper,
    ): CommonRepository = CommonRepository_Impl(
        context,
        retrofitAPI,
        playlistDao,
        preferencesHelper,
    )

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
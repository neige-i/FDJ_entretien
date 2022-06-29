package fr.neige_i.fdj_entretien.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import fr.neige_i.fdj_entretien.BuildConfig
import fr.neige_i.fdj_entretien.data.sport_api.SportDataSource
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object DataProvidingModule {

    @Provides
    fun provideSportDatasource(): SportDataSource = Retrofit.Builder()
        .baseUrl("https://www.thesportsdb.com/api/v1/json/${BuildConfig.THE_SPORTS_DB_API_KEY}/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(SportDataSource::class.java)
}
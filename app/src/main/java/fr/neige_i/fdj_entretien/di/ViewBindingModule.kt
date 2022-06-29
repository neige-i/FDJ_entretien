package fr.neige_i.fdj_entretien.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import fr.neige_i.fdj_entretien.ui.search.SearchContract
import fr.neige_i.fdj_entretien.ui.search.SearchPresenter

@Module
@InstallIn(ActivityComponent::class)
abstract class ViewBindingModule {

    @Binds
    abstract fun bindSearchPresenter(searchPresenter: SearchPresenter): SearchContract.Presenter
}
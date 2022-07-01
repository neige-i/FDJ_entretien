package fr.neige_i.fdj_entretien.domain.search

import fr.neige_i.fdj_entretien.data.search.SearchRepository
import javax.inject.Inject

class UpdateSearchUseCase @Inject constructor(
    private val searchRepository: SearchRepository,
) {

    fun setCurrentQuery(currentQuery: String) {
        searchRepository.setCurrentQuery(currentQuery)
    }

    fun setSubmittedQuery(submittedQuery: String) {
        searchRepository.setSearchedLeagueName(submittedQuery)
    }
}
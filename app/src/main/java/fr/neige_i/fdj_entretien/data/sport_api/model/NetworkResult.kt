package fr.neige_i.fdj_entretien.data.sport_api.model

sealed class NetworkResult<out R : Any> {

    data class Success<R : Any>(
        val content: R,
    ) : NetworkResult<R>()

    sealed class Failure : NetworkResult<Nothing>() {
        object IoFailure : Failure()
        object ApiFailure : Failure()
    }
}

package fr.neige_i.fdj_entretien.domain

import androidx.annotation.StringRes

sealed class DataResult<out D : Any> {

    data class Content<out D : Any>(
        val data: D,
    ) : DataResult<D>()

    data class Error(
        @StringRes val errorMessage: Int,
    ) : DataResult<Nothing>()
}

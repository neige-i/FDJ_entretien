package fr.neige_i.fdj_entretien.util

import android.content.Context
import androidx.annotation.StringRes

sealed class LocalText {
    data class Simple(val content: String) : LocalText()
    data class Res(@StringRes val stringId: Int) : LocalText()
    data class ResWithArgs(@StringRes val stringId: Int, val args: List<Any>) : LocalText()
}

fun LocalText.toCharSequence(context: Context): CharSequence = when (this) {
    is LocalText.Simple -> content
    is LocalText.Res -> context.getString(stringId)
    is LocalText.ResWithArgs -> context.getString(stringId, *args.toTypedArray())
}

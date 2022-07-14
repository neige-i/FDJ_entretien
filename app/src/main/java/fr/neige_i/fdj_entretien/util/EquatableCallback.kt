package fr.neige_i.fdj_entretien.util

class EquatableCallback(private val callback: () -> Unit) {

    operator fun invoke() {
        callback.invoke()
    }

    override fun equals(other: Any?): Boolean = if (other is EquatableCallback) {
        true
    } else {
        super.equals(other)
    }

    override fun hashCode(): Int = 2051656923
}

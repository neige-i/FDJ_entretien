package fr.neige_i.fdj_entretien.data.sport_api.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LeagueResponse(
    @SerializedName("idLeague")
    @Expose
    val idLeague: Int?,
    @SerializedName("strLeague")
    @Expose
    val strLeague: String?,
    @SerializedName("strSport")
    @Expose
    val strSport: String?,
    @SerializedName("strLeagueAlternate")
    @Expose
    val strLeagueAlternate: String?,
)

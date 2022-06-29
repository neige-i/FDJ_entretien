package fr.neige_i.fdj_entretien.data.sport_api.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LeagueListResponse(
    @SerializedName("leagues")
    @Expose
    val leagues: List<LeagueResponse>?,
)

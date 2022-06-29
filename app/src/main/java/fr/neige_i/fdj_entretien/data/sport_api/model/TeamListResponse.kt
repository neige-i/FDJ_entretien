package fr.neige_i.fdj_entretien.data.sport_api.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TeamListResponse(
    @SerializedName("teams")
    @Expose
    val teams: List<TeamResponse>?,
)

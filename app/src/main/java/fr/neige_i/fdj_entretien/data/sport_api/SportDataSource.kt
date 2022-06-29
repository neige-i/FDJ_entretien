package fr.neige_i.fdj_entretien.data.sport_api

import fr.neige_i.fdj_entretien.data.sport_api.model.TeamListResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SportDataSource {

    @GET("search_all_teams.php")
    suspend fun getTeamsByLeague(@Query("l") league: String): TeamListResponse

    @GET("searchteams.php")
    suspend fun getTeamByName(@Query("t") team: String): TeamListResponse
}
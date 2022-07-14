package fr.neige_i.fdj_entretien.ui.search

import fr.neige_i.fdj_entretien.R
import fr.neige_i.fdj_entretien.TestCoroutineRule
import fr.neige_i.fdj_entretien.data.sport_api.model.TeamResponse
import fr.neige_i.fdj_entretien.domain.DataResult
import fr.neige_i.fdj_entretien.domain.search.GetAutocompleteResultUseCase
import fr.neige_i.fdj_entretien.domain.search.GetSearchResultUseCase
import fr.neige_i.fdj_entretien.domain.search.UpdateSearchUseCase
import fr.neige_i.fdj_entretien.util.LocalText
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SearchPresenterTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val getSearchResultUseCase: GetSearchResultUseCase = mockk()
    private val getAutocompleteResultUseCase: GetAutocompleteResultUseCase = mockk()
    private val updateSearchUseCase: UpdateSearchUseCase = mockk()
    private val coroutineDispatcherProvider = testCoroutineRule.getCoroutineDispatcherProvider()

    private val subject = SearchPresenter(
        getSearchResultUseCase,
        getAutocompleteResultUseCase,
        updateSearchUseCase,
        coroutineDispatcherProvider
    )

    private val view: SearchContract.View = mockk(relaxed = true)

    @Before
    fun setUp() {
        subject.onCreated(view)

        every { getSearchResultUseCase.invoke() } returns flowOf(DataResult.Content(data = listOf(getTeam())))
    }

    @After
    fun tearDown() {
        verify(exactly = 1) {
            coroutineDispatcherProvider.io
        }
        confirmVerified(
            getSearchResultUseCase,
            getAutocompleteResultUseCase,
            updateSearchUseCase,
            coroutineDispatcherProvider,
            view,
        )
    }

    @Test
    fun `initial case - show default search result`() = testCoroutineRule.runTest {
        // WHEN
        runCurrent()

        // THEN
        verify(exactly = 1) {
            getSearchResultUseCase.invoke()
            coroutineDispatcherProvider.main
            view.showSearchResults(getDefaultSearchUiModel())
        }
    }

//    @Test
//    fun `open 1st team detail when click on item`() = testCoroutineRule.runTest {
//        // WHEN
//        runCurrent()
//        getDefaultSearchUiModel().teamUiModels[0].onClicked()
//
//        // THEN
//        verify(exactly = 1) {
//            view.showSearchResults(getDefaultSearchUiModel())
//            view.openTeamDetails("strTeam")
//        }
//    }
//
//    @Test
//    fun `alt case - do NOT open team detail when click on item with null view`() = testCoroutineRule.runTest {
//        // GIVEN
//        subject.onCreated(null)
//
//        // WHEN
//        runCurrent()
//        getDefaultSearchUiModel().teamUiModels[0].onClicked()
//
//        // THEN
//        verify(exactly = 1) {
//            view.showSearchResults(getDefaultSearchUiModel())
//        }
//        verify(exactly = 0) {
//            view.openTeamDetails("strTeam")
//        }
//    }

    @Test
    fun `alt case - show empty search result when team ID is null`() = testCoroutineRule.runTest {
        // GIVEN
        every { getSearchResultUseCase.invoke() } returns flowOf(
            DataResult.Content(data = listOf(getTeam(id = null)))
        )

        // WHEN
        runCurrent()

        // THEN
        verify(exactly = 1) {
            getSearchResultUseCase.invoke()
            coroutineDispatcherProvider.main
            view.showSearchResults(
                getDefaultSearchUiModel(size = 0, teams = emptyList())
            )
        }
    }

    @Test
    fun `alt case - show empty search result when team badge is null`() = testCoroutineRule.runTest {
        // GIVEN
        every { getSearchResultUseCase.invoke() } returns flowOf(
            DataResult.Content(data = listOf(getTeam(badge = null)))
        )

        // WHEN
        runCurrent()

        // THEN
        verify(exactly = 1) {
            getSearchResultUseCase.invoke()
            coroutineDispatcherProvider.main
            view.showSearchResults(
                getDefaultSearchUiModel(size = 0, teams = emptyList())
            )
        }
    }

    @Test
    fun `alt case - show empty search result when team name is null`() = testCoroutineRule.runTest {
        // GIVEN
        every { getSearchResultUseCase.invoke() } returns flowOf(
            DataResult.Content(data = listOf(getTeam(name = null)))
        )

        // WHEN
        runCurrent()

        // THEN
        verify(exactly = 1) {
            getSearchResultUseCase.invoke()
            coroutineDispatcherProvider.main
            view.showSearchResults(
                getDefaultSearchUiModel(size = 0, teams = emptyList())
            )
        }
    }

    @Test
    fun `show autocomplete when expand SearchView`() {
        // WHEN
        subject.onSearchViewExpanded(true)

        // THEN
        verify(exactly = 1) {
            view.setAutocompleteVisibility(true)
        }
    }

    @Test
    fun `hide autocomplete when collapse SearchView`() {
        // WHEN
        subject.onSearchViewExpanded(false)

        // THEN
        verify(exactly = 1) {
            view.setAutocompleteVisibility(false)
        }
    }

    @Test
    fun `do nothing when change SearchView expansion with null view`() {
        // GIVEN
        subject.onCreated(null)

        // WHEN
        subject.onSearchViewExpanded(true)

        // THEN
        verify(exactly = 0) {
            view.setAutocompleteVisibility(true)
        }
    }

    private fun getTeam(
        id: Int? = 1,
        name: String? = "strTeam",
        badge: String? = "strTeamBadge",
    ): TeamResponse = mockk {
        every { idTeam } returns id
        every { strTeam } returns name
        every { strTeamBadge } returns badge
    }

    private fun getDefaultTeamUiModel() = TeamUiModel(
        id = 1,
        badgeImageUrl = "strTeamBadge",
        onClicked = mockk(),
    )

    private fun getDefaultSearchUiModel(
        size: Int = 1,
        teams: List<TeamUiModel> = listOf(getDefaultTeamUiModel())
    ) = SearchUiModel(
        resultCountText = LocalText.ResWithArgs(stringId = R.string.team_count_in_league, args = listOf(size)),
        teamUiModels = teams,
    )
}
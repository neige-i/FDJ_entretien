package fr.neige_i.fdj_entretien.data.search

import fr.neige_i.fdj_entretien.TestCoroutineRule
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class SearchRepositoryTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val subject = SearchRepository()

    @Test
    fun `initial case - get empty search query when initialize repository`() = testCoroutineRule.runTest {
        val searchQuery = subject.getSearchedLeagueNameFlow().first()

        // THEN
        assertEquals("", searchQuery)
    }

    @Test
    fun `update search query when set a new value`() = testCoroutineRule.runTest {
        // WHEN
        subject.setSearchedLeagueName("Premier League")
        val searchQuery = subject.getSearchedLeagueNameFlow().first()

        // THEN
        assertEquals("Premier League", searchQuery)
    }

    @Test
    fun `updated current query when set a new value`() = testCoroutineRule.runTest {
        // WHEN
        subject.setCurrentQuery("UEFA")
        val currentQuery = subject.getCurrentQueryFlow().first()

        // THEN
        assertEquals("UEFA", currentQuery)
    }
}
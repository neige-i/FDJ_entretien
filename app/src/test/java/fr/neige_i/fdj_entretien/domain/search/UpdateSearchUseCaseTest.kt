package fr.neige_i.fdj_entretien.domain.search

import fr.neige_i.fdj_entretien.TestCoroutineRule
import fr.neige_i.fdj_entretien.data.search.SearchRepository
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UpdateSearchUseCaseTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val searchRepository: SearchRepository = mockk()

    private val subject = UpdateSearchUseCase(searchRepository)

    @Before
    fun setUp() {
        coJustRun { searchRepository.setCurrentQuery(any()) }
        coJustRun { searchRepository.setSearchedLeagueName(any()) }
    }

    @Test
    fun `call repository method when modify query`() {
        // WHEN
        subject.setCurrentQuery("Some query")

        // THEN
        coVerify(exactly = 1) { searchRepository.setCurrentQuery("Some query") }
        confirmVerified(searchRepository)
    }

    @Test
    fun `call repository method when submit query`() {
        // WHEN
        subject.setSubmittedQuery("Query to submit")

        // THEN
        coVerify(exactly = 1) { searchRepository.setSearchedLeagueName("Query to submit") }
        confirmVerified(searchRepository)
    }
}
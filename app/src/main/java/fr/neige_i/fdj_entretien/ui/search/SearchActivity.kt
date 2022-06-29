package fr.neige_i.fdj_entretien.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import fr.neige_i.fdj_entretien.R
import fr.neige_i.fdj_entretien.databinding.ActivitySearchBinding
import fr.neige_i.fdj_entretien.ui.detail.DetailActivity
import fr.neige_i.fdj_entretien.util.toCharSequence
import fr.neige_i.fdj_entretien.util.viewBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchActivity : AppCompatActivity(), SearchContract.View {

    private val binding by viewBinding { layoutInflater -> ActivitySearchBinding.inflate(layoutInflater) }

    @Inject
    lateinit var presenter: SearchContract.Presenter

    private val teamAdapter = TeamAdapter()
    private val autocompleteAdapter = AutocompleteAdapter()
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        setSupportActionBar(binding.searchToolbar)

        binding.searchSuggestions.adapter = autocompleteAdapter
        binding.searchResults.adapter = teamAdapter

        presenter.onCreated(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)

        val searchMenuItem = menu?.findItem(R.id.action_search)

        searchView = (searchMenuItem?.actionView as SearchView)
            .apply {
                queryHint = getString(R.string.search_hint)

                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextChange(newText: String?): Boolean {
                        presenter.onSearchModified(leagueName = newText.orEmpty())
                        return false
                    }

                    override fun onQueryTextSubmit(query: String?): Boolean {
                        // STEP 1: Search a league
                        presenter.onSearchSubmitted(leagueName = query.orEmpty())
                        return false
                    }
                })
            }

        searchMenuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                setAutocompleteVisibility(true)
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                setAutocompleteVisibility(false)
                return true
            }
        })

        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    override fun setSearchQuery(searchQuery: String) {
        searchView.setQuery(searchQuery, true)
    }

    override fun setAutocompleteVisibility(isAutocompleteVisible: Boolean) {
        binding.searchSuggestions.isVisible = isAutocompleteVisible
    }

    override fun showAutocompleteSuggestions(autocompleteStateFlow: Flow<List<AutocompleteState>>) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                autocompleteStateFlow.collect { autocompleteStates ->
                    autocompleteAdapter.submitList(autocompleteStates)
                }
            }
        }
    }

    override fun showSearchResults(searchStateFlow: Flow<SearchState>) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // STEP 4: Display the team list
                searchStateFlow.collect { searchState ->
                    binding.searchResultCountTxt.text = searchState.resultCountText.toCharSequence(this@SearchActivity)
                    teamAdapter.submitList(searchState.teamStates)
                }
            }
        }
    }

    override fun openTeamDetails(teamName: String) {
        startActivity(
            Intent(this, DetailActivity::class.java)
                .putExtra(DetailActivity.EXTRA_TEAM_NAME, teamName)
        )
    }
}
package fr.neige_i.fdj_entretien.ui.search

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import fr.neige_i.fdj_entretien.R
import fr.neige_i.fdj_entretien.databinding.ActivitySearchBinding
import fr.neige_i.fdj_entretien.ui.detail.DetailActivity
import fr.neige_i.fdj_entretien.util.toCharSequence
import fr.neige_i.fdj_entretien.util.viewBinding
import javax.inject.Inject

@AndroidEntryPoint
class SearchActivity : AppCompatActivity(), SearchContract.View {

    private val binding by viewBinding { layoutInflater -> ActivitySearchBinding.inflate(layoutInflater) }

    @Inject
    lateinit var presenter: SearchContract.Presenter

    private val teamAdapter = TeamAdapter()
    private val autocompleteAdapter = AutocompleteAdapter()

    private var searchMenuItem: MenuItem? = null
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

        searchMenuItem = menu?.findItem(R.id.action_search)

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

        presenter.onMenuCreated()

        searchMenuItem?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
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

    override fun expandSearchView(searchQuery: String) {
        searchMenuItem?.expandActionView()
        searchView.setQuery(searchQuery, false)
    }

    override fun setAutocompleteVisibility(isAutocompleteVisible: Boolean) {
        binding.searchSuggestions.isVisible = isAutocompleteVisible
    }

    override fun showAutocompleteSuggestions(autocompleteStates: List<AutocompleteState>) {
        autocompleteAdapter.submitList(autocompleteStates)
    }

    override fun showSearchResults(searchState: SearchState) {
        binding.searchResultCountTxt.text = searchState.resultCountText.toCharSequence(this@SearchActivity)
        teamAdapter.submitList(searchState.teamStates)
    }

    override fun openTeamDetails(teamName: String) {
        startActivity(DetailActivity.navigate(this, teamName))
    }

    override fun showErrorToast() {
        Toast.makeText(this, R.string.no_team_found_error, Toast.LENGTH_SHORT).show()
    }
}
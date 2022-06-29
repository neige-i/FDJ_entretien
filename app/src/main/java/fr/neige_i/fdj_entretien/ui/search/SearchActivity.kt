package fr.neige_i.fdj_entretien.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        setSupportActionBar(binding.searchToolbar)

        presenter.onCreated(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)

        val searchView = menu?.findItem(R.id.action_search)?.actionView as SearchView

        searchView.queryHint = getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean = false

            override fun onQueryTextSubmit(query: String?): Boolean {
                // STEP 1: Search a league
                presenter.onSearchSubmitted(leagueName = query.orEmpty())
                return false
            }
        })

        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    override fun showSearchResults(searchStateFlow: Flow<SearchState>) {
        val teamAdapter = TeamAdapter()
        binding.searchResults.adapter = teamAdapter

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
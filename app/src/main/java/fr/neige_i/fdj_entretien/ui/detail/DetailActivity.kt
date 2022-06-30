package fr.neige_i.fdj_entretien.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import fr.neige_i.fdj_entretien.R
import fr.neige_i.fdj_entretien.databinding.ActivityDetailBinding
import fr.neige_i.fdj_entretien.util.viewBinding
import javax.inject.Inject

@AndroidEntryPoint
class DetailActivity : AppCompatActivity(), DetailContract.View {

    companion object {
        private const val EXTRA_TEAM_NAME = "EXTRA_TEAM_NAME"

        fun navigate(context: Context, teamName: String) = Intent(context, DetailActivity::class.java).apply {
            putExtra(EXTRA_TEAM_NAME, teamName)
        }
    }

    private val binding by viewBinding { layoutInflater -> ActivityDetailBinding.inflate(layoutInflater) }

    @Inject
    lateinit var presenter: DetailContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        setSupportActionBar(binding.detailToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.detailToolbar.setNavigationOnClickListener { onBackPressed() }

        binding.teamDescriptionText.movementMethod = ScrollingMovementMethod()

        presenter.onCreated(this)
        presenter.onTeamNameRetrieved(intent.getStringExtra(EXTRA_TEAM_NAME).orEmpty())
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    // STEP 8: Show team detail info
    override fun showDetailInfo(detailState: DetailState) {
        title = detailState.toolbarTitle

        Glide
            .with(this@DetailActivity)
            .load(detailState.bannerImageUrl)
            .error(R.drawable.ic_no_image)
            .into(binding.teamBannerImg)

        binding.teamCountryTxt.text = detailState.country
        binding.teamLeagueTxt.text = detailState.league

        binding.teamDescriptionText.text = detailState.description
    }
}
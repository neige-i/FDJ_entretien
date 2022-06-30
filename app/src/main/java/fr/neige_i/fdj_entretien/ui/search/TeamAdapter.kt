package fr.neige_i.fdj_entretien.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.neige_i.fdj_entretien.R
import fr.neige_i.fdj_entretien.databinding.ItemTeamBinding

class TeamAdapter : ListAdapter<TeamState, TeamAdapter.TeamViewHolder>(TeamDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TeamViewHolder(
        ItemTeamBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TeamViewHolder(private val binding: ItemTeamBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(team: TeamState) {
            Glide
                .with(binding.teamBadgeImg)
                .load(team.badgeImageUrl)
                .error(R.drawable.ic_no_image)
                .into(binding.teamBadgeImg)

            // STEP 5: Select a team
            binding.teamBadgeImg.setOnClickListener { team.onClicked() }
        }
    }

    object TeamDiffUtil : DiffUtil.ItemCallback<TeamState>() {

        override fun areItemsTheSame(oldItem: TeamState, newItem: TeamState): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: TeamState, newItem: TeamState): Boolean = oldItem == newItem
    }
}
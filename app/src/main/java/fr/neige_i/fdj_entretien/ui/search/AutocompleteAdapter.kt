package fr.neige_i.fdj_entretien.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import fr.neige_i.fdj_entretien.databinding.ItemAutocompleteBinding

class AutocompleteAdapter : ListAdapter<AutocompleteUiModel, AutocompleteAdapter.AutocompleteViewHolder>(AutocompleteDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = AutocompleteViewHolder(
        ItemAutocompleteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: AutocompleteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AutocompleteViewHolder(private val binding: ItemAutocompleteBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(autocomplete: AutocompleteUiModel) {
            binding.autoCompleteTxt.text = autocomplete.suggestion
            binding.autoCompleteTxt.setOnClickListener { autocomplete.onClicked() }
        }
    }

    class AutocompleteDiffUtil : DiffUtil.ItemCallback<AutocompleteUiModel>() {

        override fun areItemsTheSame(oldItem: AutocompleteUiModel, newItem: AutocompleteUiModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AutocompleteUiModel, newItem: AutocompleteUiModel): Boolean {
            return oldItem == newItem
        }
    }
}
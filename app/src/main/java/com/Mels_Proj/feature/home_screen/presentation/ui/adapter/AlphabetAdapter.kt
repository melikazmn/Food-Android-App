package com.Mels_Proj.feature.home_screen.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.Mels_Proj.R

class AlphabetAdapter(
    private val onLetterClick: (String) -> Unit
) : RecyclerView.Adapter<AlphabetAdapter.ViewHolder>() {

    private val alphabet = listOf("All") + ('A'..'Z').map { it.toString() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alphabet, parent, false) as TextView
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(alphabet[position])
    }

    override fun getItemCount(): Int = alphabet.size

    inner class ViewHolder(private val textView: TextView) : RecyclerView.ViewHolder(textView) {
        init {
            textView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onLetterClick(alphabet[adapterPosition])
                }
            }
        }

        fun bind(letter: String) {
            textView.text = letter
        }
    }
}
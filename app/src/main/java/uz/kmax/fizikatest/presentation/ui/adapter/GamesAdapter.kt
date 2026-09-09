package uz.kmax.fizikatest.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.kmax.fizikatest.databinding.ItemGameBinding

sealed class GameListElement {
    object Physics2048 : GameListElement()
    data class AdItem(val adView: View) : GameListElement()
}

class GamesAdapter : ListAdapter<GameListElement, RecyclerView.ViewHolder>(DiffCallback()) {

    private var onGameClickListener: (() -> Unit)? = null

    fun setOnGameClickListener(listener: () -> Unit) {
        onGameClickListener = listener
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is GameListElement.Physics2048 -> 0
            is GameListElement.AdItem -> 1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val binding = ItemGameBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            GameViewHolder(binding)
        } else {
            val frame = FrameLayout(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
            AdViewHolder(frame)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (holder is GameViewHolder && item is GameListElement.Physics2048) {
            holder.bind()
        } else if (holder is AdViewHolder && item is GameListElement.AdItem) {
            holder.bind(item.adView)
        }
    }

    inner class GameViewHolder(private val binding: ItemGameBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            // Hozircha faqat bitta o'yin bor
            binding.gameTitle.text = "Fizika 2048"

            binding.playBtn.setOnClickListener {
                onGameClickListener?.invoke()
            }
            binding.root.setOnClickListener {
                onGameClickListener?.invoke()
            }
        }
    }

    class AdViewHolder(private val container: ViewGroup) : RecyclerView.ViewHolder(container) {
        fun bind(adView: View) {
            container.removeAllViews()
            (adView.parent as? ViewGroup)?.removeView(adView)
            container.addView(adView)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<GameListElement>() {
        override fun areItemsTheSame(oldItem: GameListElement, newItem: GameListElement): Boolean {
            return if (oldItem is GameListElement.Physics2048 && newItem is GameListElement.Physics2048) {
                true
            } else if (oldItem is GameListElement.AdItem && newItem is GameListElement.AdItem) {
                oldItem.adView == newItem.adView
            } else {
                false
            }
        }

        override fun areContentsTheSame(oldItem: GameListElement, newItem: GameListElement): Boolean {
            return oldItem == newItem
        }
    }
}

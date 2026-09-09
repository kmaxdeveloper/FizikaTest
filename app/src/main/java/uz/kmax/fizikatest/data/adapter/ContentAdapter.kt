package uz.kmax.fizikatest.data.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import uz.kmax.fizikatest.databinding.ItemContentMenuBinding
import uz.kmax.fizikatest.domain.models.MenuContentData

sealed class ContentListElement {
    data class ContentItem(val data: MenuContentData) : ContentListElement()
    data class AdItem(val adView: View) : ContentListElement()
}

class ContentAdapter : ListAdapter<ContentListElement, RecyclerView.ViewHolder>(DiffCallback()) {

    private var onTaskListener: ((Int, String) -> Unit)? = null

    fun setOnTaskListener(listener: (Int, String) -> Unit) {
        onTaskListener = listener
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ContentListElement.ContentItem -> 0
            is ContentListElement.AdItem -> 1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            val binding = ItemContentMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ContentViewHolder(binding)
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
        if (holder is ContentViewHolder && item is ContentListElement.ContentItem) {
            holder.bind(item.data)
        } else if (holder is AdViewHolder && item is ContentListElement.AdItem) {
            holder.bind(item.adView)
        }
    }

    inner class ContentViewHolder(private val binding: ItemContentMenuBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MenuContentData) {
            binding.contentName.text = item.contentName
            binding.contentTitle.text = "Content"
            
            if (item.contentType == 1) {
                binding.contentNewOld.visibility = if (item.contentNewOld == 1) View.INVISIBLE else View.GONE
            } else {
                binding.contentNewOld.visibility = if (item.contentNewOld == 1) View.VISIBLE else View.INVISIBLE
            }

            binding.test.setOnClickListener {
                onTaskListener?.invoke(item.contentType, item.contentLocation)
            }

            setDataToView(binding, item.contentLocation)
        }

        private fun setDataToView(binding: ItemContentMenuBinding, path: String) {
            val storage = Firebase.storage.getReference("FizikaTest/Content")
            val imageRef: StorageReference = storage.child(path).child("image.png")

            imageRef.getBytes(1024 * 1024)
                .addOnSuccessListener { image ->
                    binding.itemImage.setImageBitmap(
                        BitmapFactory.decodeByteArray(
                            image,
                            0,
                            image.size
                        )
                    )
                }
        }
    }

    inner class AdViewHolder(private val container: ViewGroup) : RecyclerView.ViewHolder(container) {
        fun bind(adView: View) {
            container.removeAllViews()
            (adView.parent as? ViewGroup)?.removeView(adView)
            container.addView(adView)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ContentListElement>() {
        override fun areItemsTheSame(oldItem: ContentListElement, newItem: ContentListElement): Boolean {
            return if (oldItem is ContentListElement.ContentItem && newItem is ContentListElement.ContentItem) {
                oldItem.data.contentName == newItem.data.contentName
            } else if (oldItem is ContentListElement.AdItem && newItem is ContentListElement.AdItem) {
                oldItem.adView == newItem.adView
            } else {
                false
            }
        }

        override fun areContentsTheSame(oldItem: ContentListElement, newItem: ContentListElement): Boolean {
            return if (oldItem is ContentListElement.ContentItem && newItem is ContentListElement.ContentItem) {
                oldItem.data == newItem.data
            } else {
                false
            }
        }
    }
}

package uz.kmax.fizikatest.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import uz.kmax.fizikatest.R

class WelcomeAdapter(val ctx: Context) : RecyclerView.Adapter<WelcomeAdapter.ViewHolder>() {
    private val images = intArrayOf(
        R.raw.lottie_animation_say_hello_robot,
        R.raw.lottie_animation_atomic,
        R.raw.lottie_animation_magnit,
        R.raw.lottie_test_collection,
        R.raw.lottie_checking_iq,
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(ctx).inflate(R.layout.item_welcome, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.animation.setAnimation(images[position])
    }

    override fun getItemCount(): Int {
        return images.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var animation: LottieAnimationView

        init {
            animation = itemView.findViewById(R.id.lottieAnimation)
        }
    }
}
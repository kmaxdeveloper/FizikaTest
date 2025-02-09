package uz.kmax.fizikatest.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.View
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import uz.kmax.base.recycleview.BaseRecycleViewDU
import uz.kmax.fizikatest.data.main.MenuTestData
import uz.kmax.fizikatest.databinding.ItemTestMenuBinding
import uz.kmax.fizikatest.tools.firebase.FirebaseManager
import uz.kmax.fizikatest.tools.tools.SharedPref

class TestListAdapter(private var context : Context) : BaseRecycleViewDU<ItemTestMenuBinding, MenuTestData>(ItemTestMenuBinding::inflate) {

    lateinit var firebaseManager : FirebaseManager
    lateinit var sharedPref: SharedPref

    override fun bind(binding: ItemTestMenuBinding, item: MenuTestData) {
        firebaseManager = FirebaseManager()
        sharedPref  = SharedPref(context)
        var language : String = sharedPref.getLanguage().toString()
        binding.testName.text = item.testName
        binding.testCount.text = "Random"
        if (item.testNewOld == 1) {
            binding.testNewOld.visibility = View.VISIBLE
        } else {
            binding.testNewOld.visibility = View.INVISIBLE
        }

        binding.test.setOnClickListener {
            firebaseManager.getChildCount("Test/$language/${item.testLocation}"){
                sendMessage(it.toInt(),item.testLocation)
            }
        }

        val storage = Firebase.storage.getReference("FizikaTest")
        val imageRef: StorageReference =
            storage.child("Menu/${item.testLocation}").child("image.png")

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

    override fun areContentsTheSame(oldItem: MenuTestData, newItem: MenuTestData) = oldItem == newItem

    override fun areItemsTheSame(oldItem: MenuTestData, newItem: MenuTestData) = oldItem.testName == newItem.testName
}
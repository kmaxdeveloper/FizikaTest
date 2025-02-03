package uz.kmax.fizikatest.fragment.other

import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.fizikatest.data.tool.MessageData
import uz.kmax.fizikatest.databinding.FragmentMessageBinding
import uz.kmax.fizikatest.tools.firebase.FirebaseManager

class MessageFragment(messageLocation : String) : BaseFragmentWC<FragmentMessageBinding>(FragmentMessageBinding::inflate) {
    private var messageLoc = messageLocation
    lateinit var firebaseManager: FirebaseManager
    var language = "uz"

    override fun onViewCreated() {
        firebaseManager = FirebaseManager()
        getMessage(messageLoc)
    }

    private fun getMessage(messageLocation : String){
        firebaseManager.observeList("Message/$language/$messageLocation", MessageData::class.java){
            if (it != null) {
                setData(it)
            }
        }
    }

    fun setData(data : ArrayList<MessageData>){
        val getData = ArrayList<MessageData>()
        getData.addAll(data)
        binding.title.text = getData[getData.size-1].title
        binding.message.text = getData[getData.size-1].message
    }
}
package uz.kmax.fizikatest.fragment.main.content

import androidx.recyclerview.widget.LinearLayoutManager
import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.fizikatest.adapter.UnitsAdapter
import uz.kmax.fizikatest.data.main.UnitsData
import uz.kmax.fizikatest.databinding.FragmentUnitsBinding
import uz.kmax.fizikatest.fragment.main.MenuFragment
import uz.kmax.fizikatest.tools.firebase.FirebaseManager
import uz.kmax.fizikatest.tools.tools.SharedPref

class UnitsFragment(private var location : String) : BaseFragmentWC<FragmentUnitsBinding>(FragmentUnitsBinding::inflate){
    private var adapter = UnitsAdapter()
    private var dayHistorySize: Int = 0
    private var language = "uz"
    lateinit var sharedPref: SharedPref
    lateinit var firebaseManager: FirebaseManager

    override fun onViewCreated() {
        firebaseManager = FirebaseManager()
        sharedPref = SharedPref(requireContext())
        language = sharedPref.getLanguage().toString()

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        getData()

        binding.back.setOnClickListener {
            startMainFragment(MenuFragment())
        }
    }

    private fun getData(){
        firebaseManager.observeList("Content/$language/$location", UnitsData::class.java){
            if (it != null) {
                dayHistorySize = it.size
                adapter.setItems(it)
            }
        }
    }
}
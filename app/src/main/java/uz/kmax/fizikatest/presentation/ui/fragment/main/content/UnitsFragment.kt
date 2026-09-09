package uz.kmax.fizikatest.presentation.ui.fragment.main.content

import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.fizikatest.data.adapter.UnitsAdapter
import uz.kmax.fizikatest.domain.models.UnitsData
import uz.kmax.fizikatest.databinding.FragmentUnitsBinding
import uz.kmax.fizikatest.presentation.ui.fragment.main.MenuFragment
import uz.kmax.fizikatest.data.firebase.FirebaseManager
import uz.kmax.fizikatest.data.tools.SharedPref
import javax.inject.Inject

@AndroidEntryPoint
class UnitsFragment(private var location : String) : BaseFragmentWC<FragmentUnitsBinding>(FragmentUnitsBinding::inflate){
    
    @Inject
    lateinit var sharedPref: SharedPref
    
    private var adapter = UnitsAdapter()
    private var dayHistorySize: Int = 0
    private var language = "uz"
    lateinit var firebaseManager: FirebaseManager

    override fun onViewCreated() {
        firebaseManager = FirebaseManager()
        language = sharedPref.getLanguage().toString()

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        getData()

        binding.toolbar.setNavigationOnClickListener {
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

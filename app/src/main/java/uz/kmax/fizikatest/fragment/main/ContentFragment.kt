package uz.kmax.fizikatest.fragment.main

import android.icu.text.SimpleDateFormat
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.fizikatest.R
import uz.kmax.fizikatest.adapter.ContentAdapter
import uz.kmax.fizikatest.data.main.MenuContentData
import uz.kmax.fizikatest.databinding.FragmentContentBinding
import uz.kmax.fizikatest.fragment.main.content.UnitsFragment
import uz.kmax.fizikatest.tools.filter.TypeFilter
import uz.kmax.fizikatest.tools.firebase.FirebaseManager
import uz.kmax.fizikatest.tools.manager.AdsManager
import uz.kmax.fizikatest.tools.tools.SharedPref
import java.util.Date

class ContentFragment : BaseFragmentWC<FragmentContentBinding>(FragmentContentBinding::inflate) {
    val adapter by lazy { ContentAdapter() }
    lateinit var firebaseManager: FirebaseManager
    private var date: String = ""
    var filter = TypeFilter()
    lateinit var shared : SharedPref
    private var language = "uz"
    private var adsManager = AdsManager()
    private var adsStatus = false

    override fun onViewCreated() {
        adsManager.initialize(requireContext())
        firebaseManager = FirebaseManager()

        //////////////////////////////////////////////////
        val currentDayDate: String = SimpleDateFormat("dd").format(Date())
        val currentMonthDate: String = SimpleDateFormat("MM").format(Date())
        val currentYearDate: String = SimpleDateFormat("yyyy").format(Date())
        date = "${currentDayDate}.${currentMonthDate}.${currentYearDate}"
        /////////////////////////////////////////////////

        shared = SharedPref(requireContext())
        language = shared.getLanguage().toString()
        adsManager.loadInterstitialAd(requireContext(), getString(R.string.interstitialAdsUnitId))
        adsManager.setOnAdLoadStatusListener {
            adsStatus = it
        }

        getContentData()
        binding.contentRecycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.contentRecycleView.adapter = adapter

        adapter.setOnTaskListener { contentType, contentLocation ->
            ads(contentType,contentLocation)
        }
    }

    private fun getContentData() {
        firebaseManager.observeList("AllContent/$language", MenuContentData::class.java){
            if (it != null){
                adapter.setItems(filter.filter(it,1,0))
            }
        }
    }

    private fun ads(type: Int, contentLocation: String) {
        if (adsStatus) {
            adsManager.showInterstitialAd(requireActivity())
            adsManager.setOnAdNotReadyListener {
                replace(type, contentLocation)
            }
            adsManager.setOnAdDismissListener {
                replace(type, contentLocation)
            }
            adsManager.setOnAdClickListener {
                Toast.makeText(requireContext(), "Thanks ! for clicking ads :D", Toast.LENGTH_SHORT).show()
            }
        } else {
            replace(type, contentLocation)
        }
    }

    private fun replace(type: Int, location: String) {
        when (type) {
            1 -> {
                replaceFragment(UnitsFragment(location))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        adsManager.loadInterstitialAd(requireContext(), getString(R.string.interstitialAdsUnitId))
        adsManager.setOnAdLoadStatusListener {
            adsStatus = it
        }
    }
}
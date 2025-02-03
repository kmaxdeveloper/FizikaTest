package uz.kmax.fizikatest.fragment.main

import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.fizikatest.R
import uz.kmax.fizikatest.adapter.TestListAdapter
import uz.kmax.fizikatest.data.main.MenuTestData
import uz.kmax.fizikatest.databinding.FragmentTestListBinding
import uz.kmax.fizikatest.dialog.DialogConnection
import uz.kmax.fizikatest.tools.filter.TypeFilter
import uz.kmax.fizikatest.tools.firebase.FirebaseManager
import uz.kmax.fizikatest.tools.manager.AdsManager
import uz.kmax.fizikatest.tools.manager.ConnectionManager
import uz.kmax.fizikatest.tools.tools.SharedPref

class TestListFragment : BaseFragmentWC<FragmentTestListBinding>(FragmentTestListBinding::inflate) {
    val adapter by lazy { TestListAdapter() }
    lateinit var firebaseManager: FirebaseManager
    private var connectionDialog = DialogConnection()
    private var adsManager = AdsManager()
    private lateinit var shared: SharedPref
    private var dataFilter = TypeFilter()
    private var adsStatus = false
    private var language = "uz"

    override fun onViewCreated() {
        firebaseManager = FirebaseManager()
        shared = SharedPref(requireContext())
        language = shared.getLanguage().toString()
        adsManager.initialize(requireContext())

        adsManager.loadInterstitialAd(requireContext(), getString(R.string.interstitialAdsUnitId))
        adsManager.setOnAdLoadStatusListener {
            adsStatus = it
        }

        getTestData()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        adapter.setOnTaskListener { testCount, testLocation ->
            if (ConnectionManager().check(requireContext())) {
                ads(testLocation, testCount)
            } else {
                connectionDialog.show(requireContext())
                connectionDialog.setOnCloseListener {
                    activity?.finish()
                }
                connectionDialog.setOnTryAgainListener {
                    if (ConnectionManager().check(requireContext())) {
                        ads(testLocation, testCount)
                    } else {
                        connectionDialog.show(requireContext())
                    }
                }
            }
        }
    }

    private fun getTestData() {
        firebaseManager.observeList("AllTest/$language", MenuTestData::class.java){
            if (it != null) {
                adapter.setItems(dataFilter.filter(it, 0))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        adsManager.loadInterstitialAd(
            requireContext(),
            getString(R.string.interstitialAdsUnitId)
        )
        adsManager.setOnAdLoadStatusListener {
            adsStatus = it
        }
    }

    private fun ads(testLocation: String, testCount: Int) {
        if (adsStatus) {
            adsManager.showInterstitialAd(requireActivity())
            adsManager.setOnAdNotReadyListener {
                replaceFragment(TestFragment(testLocation, testCount))
            }
            adsManager.setOnAdDismissListener {
                replaceFragment(
                    TestFragment(
                        testLocation,
                        testCount
                    )
                )
            }
            adsManager.setOnAdClickListener {
                Toast.makeText(requireContext(), "Thanks ! for clicking ads :D", Toast.LENGTH_SHORT).show()
            }
        } else {
            replaceFragment(TestFragment(testLocation, testCount))
        }
    }

    override fun onStart() {
        super.onStart()
        adsManager.loadInterstitialAd(
            requireContext(),
            getString(R.string.interstitialAdsUnitId)
        )
        adsManager.setOnAdLoadStatusListener {
            adsStatus = it
        }
    }
}
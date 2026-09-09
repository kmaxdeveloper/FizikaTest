package uz.kmax.fizikatest.presentation.ui.fragment.main

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.fizikatest.data.adapter.ContentAdapter
import uz.kmax.fizikatest.data.adapter.ContentListElement
import uz.kmax.fizikatest.data.manager.AdsManager
import uz.kmax.fizikatest.domain.models.MenuContentData
import uz.kmax.fizikatest.databinding.FragmentContentBinding
import uz.kmax.fizikatest.presentation.ui.fragment.main.content.UnitsFragment
import uz.kmax.fizikatest.presentation.ui.fragment.main.content.list.BookListFragment
import uz.kmax.fizikatest.data.tools.TypeFilter
import uz.kmax.fizikatest.data.firebase.FirebaseManager
import uz.kmax.fizikatest.data.tools.SharedPref
import javax.inject.Inject

@AndroidEntryPoint
class ContentFragment : BaseFragmentWC<FragmentContentBinding>(FragmentContentBinding::inflate) {
    
    @Inject
    lateinit var shared: SharedPref

    @Inject
    lateinit var adsManager: AdsManager
    
    val adapter by lazy { ContentAdapter() }
    lateinit var firebaseManager: FirebaseManager
    var filter = TypeFilter()
    private var language = "uz"
    private var nativeAdView: View? = null
    private var originalContentData: List<MenuContentData>? = null

    override fun onViewCreated() {
        firebaseManager = FirebaseManager()

        language = shared.getLanguage().toString()

        adsManager.init()
        loadNativeAd()

        getContentData()
        binding.contentRecycleView.layoutManager = LinearLayoutManager(requireContext())
        binding.contentRecycleView.adapter = adapter

        adapter.setOnTaskListener { contentType, contentLocation ->
            ads(contentType,contentLocation)
        }
    }

    private fun getContentData() {
        binding.shimmerView.startShimmer()
        binding.shimmerView.visibility = View.VISIBLE
        binding.contentRecycleView.visibility = View.GONE
        val startTime = System.currentTimeMillis()

        firebaseManager.observeList("AllContent/$language", MenuContentData::class.java){
            val timePassed = System.currentTimeMillis() - startTime
            val delay = if (timePassed < 2000) 2000 - timePassed else 0L

            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                if (isAdded) {
                    binding.shimmerView.stopShimmer()
                    binding.shimmerView.visibility = View.GONE
                    binding.contentRecycleView.visibility = View.VISIBLE

                    if (it != null){
                        originalContentData = filter.filter(it, 1, 0)
                        updateListWithAds()
                    }
                }
            }, delay)
        }
    }

    private fun loadNativeAd() {
        adsManager.loadNativeAd(binding.contentRecycleView) { adView ->
            if (isAdded && !isStateSaved && adView != null) {
                nativeAdView = adView
                updateListWithAds()
            }
        }
    }

    private fun updateListWithAds() {
        val contents = originalContentData ?: return
        val items = mutableListOf<ContentListElement>()

        contents.forEach { content ->
            items.add(ContentListElement.ContentItem(content))
        }

        if (nativeAdView != null) {
            items.add(ContentListElement.AdItem(nativeAdView!!))
        }

        adapter.submitList(items)
    }

    private fun ads(type: Int, contentLocation: String) {
        adsManager.setOnAdDismissListener {
            replace(type, contentLocation)
        }
        adsManager.showAds(requireActivity()) { success ->
            if (!success) {
                replace(type, contentLocation)
            }
        }
    }

    private fun replace(type: Int, location: String) {
        when (type) {
            1 -> {
                replaceFragment(UnitsFragment(location))
            }
            2 -> {
                replaceFragment(BookListFragment())
            }
            else -> {
                if (location.lowercase().contains("book") || location.lowercase().contains("kitob")) {
                    replaceFragment(BookListFragment())
                } else {
                    replaceFragment(UnitsFragment(location))
                }
            }
        }
    }

    override fun onDestroyView() {
        adsManager.setOnAdDismissListener {}
        adsManager.setOnAdClickListener {}
        super.onDestroyView()
    }
}

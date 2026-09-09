package uz.kmax.fizikatest.presentation.ui.fragment.main.arcade

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.fizikatest.data.manager.AdsManager
import uz.kmax.fizikatest.databinding.FragmentGamesListBinding
import uz.kmax.fizikatest.presentation.ui.adapter.GameListElement
import uz.kmax.fizikatest.presentation.ui.adapter.GamesAdapter
import javax.inject.Inject

@AndroidEntryPoint
class GamesListFragment : BaseFragmentWC<FragmentGamesListBinding>(FragmentGamesListBinding::inflate) {

    @Inject
    lateinit var adsManager: AdsManager

    private val adapter by lazy { GamesAdapter() }

    override fun onViewCreated() {
        binding.gamesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.gamesRecyclerView.adapter = adapter

        updateList()

        adapter.setOnGameClickListener {
            val currentActivity = activity ?: return@setOnGameClickListener
            adsManager.setOnAdDismissListener {
                if (isAdded && !isStateSaved) {
                    startMainFragment(Physics2048Fragment())
                }
            }
            adsManager.showAds(currentActivity, false) { showed ->
                if (!showed) {
                    if (isAdded && !isStateSaved) {
                        startMainFragment(Physics2048Fragment())
                    }
                }
            }
        }
    }

    private fun updateList() {
        val items = mutableListOf<GameListElement>()
        items.add(GameListElement.Physics2048)
        adapter.submitList(items)
    }

    override fun onDestroyView() {
        adsManager.setOnAdDismissListener {}
        adsManager.setOnAdClickListener {}
        super.onDestroyView()
    }
}

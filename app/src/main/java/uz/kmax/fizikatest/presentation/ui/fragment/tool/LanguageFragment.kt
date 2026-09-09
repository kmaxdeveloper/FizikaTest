package uz.kmax.fizikatest.presentation.ui.fragment.tool

import dagger.hilt.android.AndroidEntryPoint
import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.fizikatest.R
import uz.kmax.fizikatest.data.tools.SharedPref
import uz.kmax.fizikatest.databinding.FragmentLanguageBinding
import uz.kmax.fizikatest.presentation.ui.fragment.presentation.SplashFragment
import uz.kmax.fizikatest.presentation.ui.fragment.presentation.WelcomeFragment
import javax.inject.Inject

@AndroidEntryPoint
class LanguageFragment : BaseFragmentWC<FragmentLanguageBinding>(FragmentLanguageBinding::inflate) {
    
    @Inject
    lateinit var shared: SharedPref
    
    override fun onViewCreated() {
        binding.selectLangUz.setOnClickListener {
            shared.setLanguage(getString(R.string.lang_uz),requireContext())
            shared.setLangStatus(false)
            navigateToNext()
        }

        binding.selectLangEn.setOnClickListener {
            shared.setLanguage(getString(R.string.lang_en),requireContext())
            shared.setLangStatus(false)
            navigateToNext()
        }
    }

    private fun navigateToNext() {
        if (shared.getWelcomeStatus()) {
            startMainFragment(WelcomeFragment())
        } else {
            startMainFragment(SplashFragment())
        }
    }
}

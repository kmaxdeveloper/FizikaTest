package uz.kmax.fizikatest.fragment.tool

import android.content.Intent
import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.fizikatest.MainActivity
import uz.kmax.fizikatest.R
import uz.kmax.fizikatest.databinding.FragmentLanguageBinding
import uz.kmax.fizikatest.tools.tools.SharedPref

class LanguageFragment : BaseFragmentWC<FragmentLanguageBinding>(FragmentLanguageBinding::inflate) {
    lateinit var sharedPref: SharedPref
    override fun onViewCreated() {
        sharedPref = SharedPref(requireContext())
        val window = requireActivity().window
        window.statusBarColor = this.resources.getColor(R.color.white)

        binding.selectLangEn.setOnClickListener {
            sharedPref.setLanguage(getString(R.string.lang_uz),requireContext())
            sharedPref.setLangStatus(false)
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }

        binding.selectLangUz.setOnClickListener {
            sharedPref.setLanguage(getString(R.string.lang_en),requireContext())
            sharedPref.setLangStatus(false)
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }
    }
}
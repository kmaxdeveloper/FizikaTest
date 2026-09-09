package uz.kmax.fizikatest

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import uz.kmax.base.fragmentcontroller.FragmentController
import uz.kmax.fizikatest.data.tools.SharedPref
import uz.kmax.fizikatest.databinding.ActivityMainBinding
import uz.kmax.fizikatest.presentation.ui.fragment.presentation.SplashFragment
import uz.kmax.fizikatest.presentation.ui.fragment.presentation.WelcomeFragment
import uz.kmax.fizikatest.presentation.ui.fragment.tool.LanguageFragment
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var sharedPref: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(sharedPref.getThemeMode())
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Faqat system bar padding'larini root view'ga tatbiq etamiz —
        // kontent esa status/nav bar ostida emas, yonida ko'rinadi.
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        FragmentController.init(R.id.container, supportFragmentManager)
        
        when {
            sharedPref.getLangStatus() -> {
                startFragment(LanguageFragment())
            }
            sharedPref.getWelcomeStatus() -> {
                startFragment(WelcomeFragment())
            }
            else -> {
                startFragment(SplashFragment())
            }
        }
    }

    private fun startFragment(fragment: Fragment) {
        FragmentController.controller?.startMainFragment(fragment)
    }
}

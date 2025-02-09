package uz.kmax.fizikatest

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.intFloatMapOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import uz.kmax.base.fragmentcontroller.FragmentController
import uz.kmax.fizikatest.databinding.ActivityMainBinding
import uz.kmax.fizikatest.fragment.presentation.SplashFragment
import uz.kmax.fizikatest.fragment.presentation.WelcomeFragment
import uz.kmax.fizikatest.fragment.tool.LanguageFragment
import uz.kmax.fizikatest.tools.tools.SharedPref

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var sharedPref: SharedPref
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = SharedPref(this)

        FragmentController.init(R.id.container,supportFragmentManager)
        if (!sharedPref.getLangStatus()){
            if (sharedPref.getWelcomeStatus()){
                startFragment(WelcomeFragment())
            }else{
                startFragment(SplashFragment())
            }
        }else {
            startFragment(LanguageFragment())
        }
    }

    private fun startFragment(fragment : Fragment){
        FragmentController.controller?.startMainFragment(fragment)
    }
}
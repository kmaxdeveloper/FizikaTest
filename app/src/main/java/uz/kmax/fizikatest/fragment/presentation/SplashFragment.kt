package uz.kmax.fizikatest.fragment.presentation

import android.content.Intent
import android.net.Uri
import android.os.CountDownTimer
import android.widget.Toast
import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.fizikatest.R
import uz.kmax.fizikatest.data.tool.CheckUpdateData
import uz.kmax.fizikatest.databinding.FragmentSplashBinding
import uz.kmax.fizikatest.dialog.DialogConnection
import uz.kmax.fizikatest.dialog.DialogUpdate
import uz.kmax.fizikatest.fragment.main.MenuFragment
import uz.kmax.fizikatest.tools.firebase.FirebaseManager
import uz.kmax.fizikatest.tools.manager.ConnectionManager
import uz.kmax.fizikatest.tools.manager.NetworkMonitor
import uz.kmax.fizikatest.tools.tools.SharedPref
import uz.kmax.fizikatest.tools.tools.getAppVersion

class SplashFragment : BaseFragmentWC<FragmentSplashBinding>(FragmentSplashBinding::inflate) {
    var connectionDialog = DialogConnection()
    lateinit var shared: SharedPref
    var updateDialog = DialogUpdate()
    lateinit var networkMonitor: NetworkMonitor
    lateinit var firebaseManager: FirebaseManager
    var updateInfo : Boolean = true

    override fun onViewCreated() {
        firebaseManager = FirebaseManager()
        shared = SharedPref(requireContext())
        val window = requireActivity().window
        window.statusBarColor = this.resources.getColor(R.color.color_app)

        updateDialog.setOnUpdateNowListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(
                    "https://play.google.com/store/apps/details?id=uz.kmax.fizikatest")
                setPackage("com.android.vending")
            }
            startActivity(intent)
            activity?.finish()
        }

        updateDialog.setOnExitListener { activity?.finish() }
        progress()
    }

    fun progress(){
        if (ConnectionManager().check(requireContext())) {
            checkUp()
        } else {
            Toast.makeText(requireContext(), "No connection !", Toast.LENGTH_SHORT).show()
            connectionDialog.show(requireContext())
            connectionDialog.setOnCloseListener {
                activity?.finish()
            }
            connectionDialog.setOnTryAgainListener {
                if (ConnectionManager().check(requireContext())) {
                    checkUp()
                } else {
                    connectionDialog.show(requireContext())
                }
            }
        }
    }

    fun checkUp(){
        firebaseManager.observeList("Update", CheckUpdateData::class.java) { list ->
            if (list != null) {
                val currentAppVersion: Long = getAppVersion(requireContext())!!.versionNumber
                for (i in 0 until list.size){
                    if (currentAppVersion < list[i].versionCode) {
                        if (list[i].updateLevel >= 4) {
                            updateInfo = false
                            updateDialog.show(requireContext())
                        } else {
                            startApp()
                            shared.setUpdateStatus(true)
                        }
                    }
                }
                if (updateInfo){
                    startApp()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        progress()
    }

    fun startApp() {
        object : CountDownTimer(3000, 100) {
            override fun onFinish() {
                if (ConnectionManager().check(requireContext())) {
                    startMainFragment(MenuFragment())
                } else {
                    connectionDialog.show(requireContext())
                    connectionDialog.setOnCloseListener {
                        activity?.finish()
                    }
                    connectionDialog.setOnTryAgainListener {
                        if (ConnectionManager().check(requireContext())) {
                            startMainFragment(MenuFragment())
                        } else {
                            connectionDialog.show(requireContext())
                        }
                    }
                }
            }
            override fun onTick(value: Long) {}
        }.start()
    }

    override fun onStart() {
        super.onStart()
        networkMonitor = NetworkMonitor(requireActivity().application)
        networkMonitor.observe(requireActivity()){
            if (!it){
                connectionDialog.show(requireContext())
                connectionDialog.setOnCloseListener {
                    activity?.finish()
                }
                connectionDialog.setOnTryAgainListener {
                    if (ConnectionManager().check(requireContext())) {
                        startMainFragment(MenuFragment())
                    } else {
                        connectionDialog.show(requireContext())
                    }
                }
            }
        }
    }
}
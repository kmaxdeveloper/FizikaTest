package uz.kmax.fizikatest.presentation.ui.fragment.presentation

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.AndroidEntryPoint
import uz.kmax.base.fragment.BaseFragmentWC
import uz.kmax.fizikatest.data.firebase.FirebaseManager
import uz.kmax.fizikatest.data.manager.ConnectionManager
import uz.kmax.fizikatest.data.manager.NetworkMonitor
import uz.kmax.fizikatest.data.manager.UpdateManager
import uz.kmax.fizikatest.data.tools.SharedPref
import uz.kmax.fizikatest.data.tools.getAppVersion
import uz.kmax.fizikatest.databinding.FragmentSplashBinding
import uz.kmax.fizikatest.domain.models.CheckUpdateData
import uz.kmax.fizikatest.presentation.ui.dialog.DialogConnection
import uz.kmax.fizikatest.presentation.ui.dialog.DialogUpdate
import uz.kmax.fizikatest.presentation.ui.fragment.main.MenuFragment
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : BaseFragmentWC<FragmentSplashBinding>(FragmentSplashBinding::inflate) {
    
    @Inject
    lateinit var shared: SharedPref
    
    private var connectionDialog = DialogConnection()
    private var updateDialog = DialogUpdate()
    private lateinit var networkMonitor: NetworkMonitor
    private lateinit var firebaseManager: FirebaseManager
    private lateinit var googleUpdateManager: UpdateManager
    private var updateInfo : Boolean = true
    private var splashTimer: CountDownTimer? = null
    private var isProgressing = false
    private var isTimerFinished = false

    private val updateLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode != FragmentActivity.RESULT_OK) {
            googleUpdateManager.transAction()
        } else {
            if (isAdded && !isStateSaved) {
                checkFirebaseUpdate()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseManager = FirebaseManager()

        googleUpdateManager = UpdateManager(requireContext())
        googleUpdateManager.init(requireContext(), updateLauncher)
        
        googleUpdateManager.setUpdateDismissListener {
            activity?.finish()
        }
    }

    override fun onViewCreated() {
        val window = activity?.window
        window?.statusBarColor = Color.TRANSPARENT
        
        binding.splashScreen.alpha = 0f
        binding.splashScreen.animate().alpha(1f).setDuration(800).start()

        updateDialog.setOnUpdateNowListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=uz.kmax.fizikatest")
                setPackage("com.android.vending")
            }
            startActivity(intent)
            activity?.finish()
        }

        updateDialog.setOnExitListener { activity?.finish() }
        
        networkMonitor = NetworkMonitor(requireActivity().application)
        networkMonitor.observe(viewLifecycleOwner) {
            if (!isAdded) return@observe
            if (!it) {
                context?.let { ctx ->
                    connectionDialog.show(ctx)
                    connectionDialog.setOnCloseListener { activity?.finish() }
                    connectionDialog.setOnTryAgainListener {
                        val currentCtx = context ?: return@setOnTryAgainListener
                        if (ConnectionManager().check(currentCtx)) {
                            googleUpdateManager.update(1)
                        } else {
                            connectionDialog.show(currentCtx)
                        }
                    }
                }
            }
        }
    }

    private fun progress(){
        if (isProgressing) return
        isProgressing = true
        
        val ctx = context ?: return
        if (ConnectionManager().check(ctx)) {
            checkFirebaseUpdate()
        } else {
            Toast.makeText(ctx, "No connection !", Toast.LENGTH_SHORT).show()
            connectionDialog.show(ctx)
            connectionDialog.setOnCloseListener {
                activity?.finish()
            }
            connectionDialog.setOnTryAgainListener {
                val currentCtx = context ?: return@setOnTryAgainListener
                if (ConnectionManager().check(currentCtx)) {
                    isProgressing = false
                    progress()
                } else {
                    connectionDialog.show(currentCtx)
                }
            }
        }
    }

    private fun checkFirebaseUpdate(){
        firebaseManager.readList("Update/AppUpdate", CheckUpdateData::class.java) { list ->
            if (!isAdded || isStateSaved) return@readList
            if (list != null) {
                val appVersion = getAppVersion(requireContext())
                val currentAppVersion: Long = appVersion?.versionNumber ?: 0
                
                var hasCriticalUpdate = false
                for (item in list) {
                    if (currentAppVersion < item.versionCode) {
                        if (item.updateLevel >= 4) {
                            hasCriticalUpdate = true
                            
                            // 1. Google Play In-App Update orqali urinib ko'ramiz
                            googleUpdateManager.setNotUpdateListener {
                                // Agar Google Play yangilanishni ko'rmasa, o'zimizning dialogni chiqaramiz (Fallback)
                                if (isAdded && !isStateSaved) {
                                    updateInfo = false
                                    updateDialog.show(requireContext())
                                }
                            }
                            
                            googleUpdateManager.setOnUpdateAvailableListener {
                                // Google Play orqali yangilanish boshlandi
                            }
                            
                            googleUpdateManager.update(1)
                            break
                        } else {
                            shared.setUpdateStatus(true)
                        }
                    }
                }
                if (!hasCriticalUpdate) {
                    startApp()
                }
            } else {
                startApp()
            }
        }
    }

    private fun startApp() {
        splashTimer?.cancel()
        splashTimer = object : CountDownTimer(2000, 100) {
            override fun onFinish() {
                isTimerFinished = true
                tryMoveToMain()
            }
            override fun onTick(value: Long) {}
        }
        splashTimer?.start()
    }

    private fun tryMoveToMain() {
        if (!isAdded || isStateSaved || !isTimerFinished) return
        
        val ctx = context ?: return
        if (ConnectionManager().check(ctx)) {
            startMainFragment(MenuFragment())
        } else {
            connectionDialog.show(ctx)
            connectionDialog.setOnCloseListener { activity?.finish() }
            connectionDialog.setOnTryAgainListener {
                if (!isAdded || isStateSaved) return@setOnTryAgainListener
                val currentCtx = context ?: return@setOnTryAgainListener
                if (ConnectionManager().check(currentCtx)) {
                    startMainFragment(MenuFragment())
                } else {
                    connectionDialog.show(currentCtx)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        googleUpdateManager.onStarted()
        progress()
        if (isTimerFinished) {
            tryMoveToMain()
        }
    }

    override fun onDestroyView() {
        splashTimer?.cancel()
        super.onDestroyView()
    }
}

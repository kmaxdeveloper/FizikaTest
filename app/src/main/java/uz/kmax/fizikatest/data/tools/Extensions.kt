package uz.kmax.fizikatest.data.tools

import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

fun AppCompatActivity.onBackPressed(
    doubleBackToExit: Boolean = false,  
    confirmExitDialog: Boolean = false, 
    onBackPressedAction: (() -> Unit)? = null  
) {
    var backPressedTime: Long = 0

    onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            when {
                onBackPressedAction != null -> onBackPressedAction.invoke()
                doubleBackToExit -> handleDoubleBackPress()
                confirmExitDialog -> showExitConfirmationDialog()
                else -> finish()
            }
        }

        private fun handleDoubleBackPress() {
            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                finish()
            } else {
                Toast.makeText(this@onBackPressed, "Chiqish uchun yana bir marta bosing", Toast.LENGTH_SHORT).show()
                backPressedTime = System.currentTimeMillis()
            }
        }

        private fun showExitConfirmationDialog() {
            AlertDialog.Builder(this@onBackPressed)
                .setTitle("Chiqishni tasdiqlang")
                .setMessage("Siz haqiqatdan ham dasturni yopmoqchimisiz?")
                .setPositiveButton("Ha") { _, _ -> finish() }
                .setNegativeButton("Yo'q", null)
                .show()
        }
    })
}

fun Fragment.onFragmentBackPressed(
    onBackPressedAction: (() -> Unit)? = null
) {
    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBackPressedAction?.invoke()
        }
    })
}

fun Fragment.sendFragmentResult(requestKey: String, data: Bundle) {
    parentFragmentManager.setFragmentResult(requestKey, data)
}

fun Fragment.getFragmentResult(requestKey: String, onResult: (Bundle) -> Unit) {
    parentFragmentManager.setFragmentResultListener(requestKey, viewLifecycleOwner) { _, bundle ->
        onResult(bundle)
    }
}
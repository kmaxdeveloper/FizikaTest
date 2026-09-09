package uz.kmax.fizikatest.data.tools

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream

object SaveFiles {

    fun saveFileInternally(context: Context, fileName: String, sourceFile: File): String {
        val folder = File(context.getExternalFilesDir(null), "Kitoblar")
        if (!folder.exists()) folder.mkdirs()

        val destinationFile = File(folder, fileName)
        return try {
            sourceFile.inputStream().use { input ->
                FileOutputStream(destinationFile).use { output ->
                    input.copyTo(output)
                }
            }
            destinationFile.absolutePath
        } catch (e: Exception) {
            Log.e("InternalSave", "Xatolik: ${e.message}")
            ""
        }
    }
}

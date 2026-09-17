package com.etaratasy.app

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

object FileUtils {

    fun telechargerPdfDepuisAssets(context: Context, assetPath: String, fileName: String): Boolean {
        return try {
            // Vérification de l'existence de l'asset
            val assetManager = context.assets
            val inputStream: InputStream = try {
                assetManager.open(assetPath)
            } catch (e: Exception) {
                // Fallback sur le template de résidence si l'asset spécifique n'existe pas
                assetManager.open("pdfs/fkt_residence_template.pdf")
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                
                val resolver = context.contentResolver
                val uri: Uri? = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                
                uri?.let {
                    val outputStream: OutputStream? = resolver.openOutputStream(it)
                    outputStream?.use { out ->
                        inputStream.copyTo(out)
                    }
                    true
                } ?: false
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, fileName)
                val outputStream = FileOutputStream(file)
                outputStream.use { out ->
                    inputStream.copyTo(out)
                }
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

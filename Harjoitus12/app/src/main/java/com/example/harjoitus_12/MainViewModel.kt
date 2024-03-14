package com.example.harjoitus_12


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.ref.WeakReference

class MainViewModel(context: Context) : ViewModel() {

    private val contextRef: WeakReference<Context> = WeakReference(context)

    private val dirName = "photos"

    private val _bitmaps = MutableStateFlow(getBitmapsFromDir())
    val bitmaps = _bitmaps.asStateFlow()

    fun onTakePhoto(bitmap: Bitmap) {
        _bitmaps.value += bitmap
        saveBitmapToDir(bitmap)
    }

    private fun getBitmapsFromDir(): List<Bitmap> {
        val bitmaps = mutableListOf<Bitmap>()
        val context = getContext() ?: return bitmaps

        val dir = context.getDir(dirName, Context.MODE_PRIVATE)
        val files = dir.listFiles()

        files?.forEach { file ->
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            bitmap?.let {
                bitmaps.add(it)
            }
        }

        return bitmaps
    }

    private fun saveBitmapToDir(bitmap: Bitmap) {
        val context = getContext() ?: return

        val dir = context.getDir(dirName, Context.MODE_PRIVATE)

        val fileName = "photo_${System.currentTimeMillis()}.jpg"
        val file = File(dir, fileName)

        try {
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getContext(): Context? {
        return contextRef.get()
    }
}
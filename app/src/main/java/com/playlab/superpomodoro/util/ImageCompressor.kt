package com.playlab.superpomodoro.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.playlab.superpomodoro.util.Constants.IMAGE_COMPRESSION_QUALITY
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object ImageCompressor {
    private suspend fun uriToFile(context: Context, uri: Uri): File = withContext(Dispatchers.IO){
        val buffer = ByteArray(8 * 1024)
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File.createTempFile("temp_file", null, context.cacheDir)
        inputStream.use { input ->
            FileOutputStream(file).use { output ->
                var bytesRead = input?.read(buffer)
                while (bytesRead != -1) {
                    if (bytesRead != null) {
                        output.write(buffer, 0, bytesRead)
                    }
                    bytesRead = input?.read(buffer)
                }
                output.flush()
            }
        }
        return@withContext file
    }

    suspend fun compressImage(context: Context, result: Uri): Uri? {
        val compressedImageFile = Compressor.compress(
            context,
            uriToFile(context, result),
            coroutineContext = Dispatchers.IO
        ) {
            quality(IMAGE_COMPRESSION_QUALITY)
            format(Bitmap.CompressFormat.JPEG)
        }
        return Uri.fromFile(compressedImageFile)
    }
}
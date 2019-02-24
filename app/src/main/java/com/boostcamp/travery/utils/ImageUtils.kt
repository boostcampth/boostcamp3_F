package com.boostcamp.travery.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ImageUtils {

    fun createImage(context: Context, path: String): File {
        val directory = context.filesDir
        // 폴더가 존재하지않을 경우 생성
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val temp = File(path)

        val file = File(directory, temp.name)
        try {
            file.createNewFile()
            val options = BitmapFactory.Options()
            val bitmap = rotateImage(BitmapFactory.decodeFile(path, options), path)
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30 /*ignored for PNG*/, bos)
            val bitmapData = bos.toByteArray()
            val fos = FileOutputStream(file)
            fos.write(bitmapData)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }

    private fun rotateImage(bitmap: Bitmap, path: String): Bitmap {
        var result = bitmap
        try {
            val exif = ExifInterface(path)
            val exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            val exifDegree = exifOrientationToDegrees(exifOrientation)
            result = rotate(bitmap, exifDegree)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }


    /**
     * EXIF정보를 회전각도로 변환하는 메서드
     *
     * @param exifOrientation EXIF 회전각
     * @return 실제 각도
     */
    private fun exifOrientationToDegrees(exifOrientation: Int): Int {
        when (exifOrientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> return 90
            ExifInterface.ORIENTATION_ROTATE_180 -> return 180
            ExifInterface.ORIENTATION_ROTATE_270 -> return 270
            else -> return 0
        }
    }

    private fun rotate(bitmap: Bitmap, degrees: Int): Bitmap {
        var bitmap = bitmap
        if (degrees != 0 && bitmap != null) {
            val m = Matrix()
            m.setRotate(
                    degrees.toFloat(), bitmap.width.toFloat() / 2,
                    bitmap.height.toFloat() / 2
            )

            try {
                val converted = Bitmap.createBitmap(
                        bitmap, 0, 0,
                        bitmap.width, bitmap.height, m, true
                )
                if (bitmap != converted) {
                    bitmap.recycle()
                    bitmap = converted
                }
            } catch (ex: OutOfMemoryError) {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }

        }
        return bitmap

    }

}
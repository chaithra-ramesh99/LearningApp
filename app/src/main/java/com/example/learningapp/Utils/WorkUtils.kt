package com.example.learningapp.Utils

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.net.Uri
import android.renderscript.Allocation
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.renderscript.Element;
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*


object WorkUtils {
    private const val BITMAP_SCALE = 0.4f
    private const val BLUR_RADIUS = 20.5f

    fun blurImage(image: Bitmap, context: Context?): Bitmap {
        val width = Math.round(image.width * BITMAP_SCALE)
        val height = Math.round(image.height * BITMAP_SCALE)

        val inputBitmap = Bitmap.createScaledBitmap(image, width, height, false)
        val outputBitmap = Bitmap.createBitmap(inputBitmap)

        //render class to blur image
        val rs = RenderScript.create(context)
        val theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        val tmpIn = Allocation.createFromBitmap(rs, inputBitmap)
        val tmpOut = Allocation.createFromBitmap(rs, outputBitmap)
        theIntrinsic.setRadius(BLUR_RADIUS)
        theIntrinsic.setInput(tmpIn)
        theIntrinsic.forEach(tmpOut)
        tmpOut.copyTo(outputBitmap)
        return outputBitmap
    }

    fun WriteBitmapToFile(mContext: Context?, bitmap: Bitmap): Uri {
        val mTimeStamp: String = SimpleDateFormat("ddMMyyyy_HHmm").format(Date())
        val mImageName = "snap_$mTimeStamp.jpg"
        val wrapper = ContextWrapper(mContext)
        var file: File = wrapper.getDir("Images", Context.MODE_PRIVATE)
        file = File(file, "snap_$mImageName.jpg")
        try {
            var stream: OutputStream? = null
            stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return Uri.parse(file.getAbsolutePath())
    }
}
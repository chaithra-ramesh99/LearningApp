package com.example.learningapp.workers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.learningapp.Utils.WorkUtils


class BlurWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    companion object{
        lateinit var output:Bitmap
        var uri: Uri? = null
    }



    override fun doWork(): Result {
        try {
            val picture = BitmapFactory.decodeResource(applicationContext.resources,
                com.example.learningapp.R.drawable.profile)
            output = WorkUtils.blurImage(picture,applicationContext)
            uri = WorkUtils.WriteBitmapToFile(applicationContext, output)
            return Result.success()
        }
        catch (t:Throwable)
        {
            Log.d("tag",t.localizedMessage)
            return Result.failure()
        }
    }
}
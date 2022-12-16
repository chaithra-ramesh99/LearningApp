package com.example.learningapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.learningapp.workers.BlurWorker

class BlurImgActivity : AppCompatActivity() {

   lateinit var blurButton: Button
   lateinit var img:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blur_img)
        blurButton = findViewById(R.id.blur)
        img = findViewById(R.id.img)
        var oneTimeRequest = OneTimeWorkRequest.Builder(BlurWorker::class.java).build()

        blurButton.setOnClickListener {
            WorkManager.getInstance(applicationContext).enqueue(oneTimeRequest)
            img.setImageURI(BlurWorker.uri)
        }
    }
}
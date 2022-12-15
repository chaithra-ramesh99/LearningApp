package com.example.learningapp

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.learningapp.databinding.ActivityMainBinding
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
    companion object{
        var CAMERA_REQUEST_CODE = 123
        var STORAGE_PERMISSION_CODE= 789
    }
    lateinit var uri:Uri
    lateinit var binding:ActivityMainBinding
     lateinit var scanner:BarcodeScanner



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
         binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        binding.btnCamera.setOnClickListener{

            if(isCameraPErmissionGranted())
            {
                pickImageFromCamer()
            }
            else{
                RequesCameraPermission()
            }


        }
        binding.btnGallery.setOnClickListener {
            /*activityResultLauncher.launch(
                arrayOf(Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
            )*/
           // checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE)
            if(isStoragePErmissionGranted())
            {
                SelectImageFromGallery()
            }
            else{
                RequesStoragePermission()
            }
        }

        binding.btnScan.setOnClickListener {
            if(uri==null)
            {
                Toast.makeText(this,"Select Image.",Toast.LENGTH_LONG).show()
            }
            else{
                detectResultFromImage()
            }
        }

        //initalizing barcode
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_ALL_FORMATS,
                )
            .build()

          scanner = BarcodeScanning.getClient(options)


    }

    private fun detectResultFromImage() {
        val image: InputImage
        try {
            image = InputImage.fromFilePath(this, uri)
             scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    // Task completed successfully
                extractBarcodeQrcodeInfo(barcodes)
                }
                .addOnFailureListener {
                    // Task failed with an exception
                    Toast.makeText(this,"on failed listner",Toast.LENGTH_LONG).show()

                }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this,"exceptio",Toast.LENGTH_LONG).show()

        }
    }

    private fun extractBarcodeQrcodeInfo(barcodes: List<Barcode>) {

        for (barcode in barcodes) {
            val bounds = barcode.boundingBox
            val corners = barcode.cornerPoints

            val rawValue = barcode.rawValue
            rawValue?.let { Log.e("Barcode data:", it) }

            binding.res.text = rawValue

            val valueType = barcode.valueType
            // See API reference for complete list of supported types
            when (valueType) {
                Barcode.TYPE_WIFI -> {
                    val ssid = barcode.wifi!!.ssid
                    val password = barcode.wifi!!.password
                    val type = barcode.wifi!!.encryptionType
                }
                Barcode.TYPE_URL -> {
                    val title = barcode.url!!.title
                    val url = barcode.url!!.url
                }
            }
        }
    }

    fun isCameraPErmissionGranted():Boolean{
        var camera = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED
        var storage = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED
return camera && storage
    }

    fun isStoragePErmissionGranted():Boolean{
        var storage = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED
        return  storage
    }



    // Function to check and request permission.

    fun RequesStoragePermission(){
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
    }
    fun RequesCameraPermission(){
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE), CAMERA_REQUEST_CODE)
    }


    /* // Receiver
     private val activityResultLauncher =
         registerForActivityResult(
             ActivityResultContracts.RequestMultiplePermissions())
         { permissions ->
             // Handle Permission granted/rejected
             permissions.entries.forEach {
                 val permissionName = it.key
                 Log.e("permis:",permissionName)

                 val isGranted = it.value
                 if (permissionName==Manifest.permission.CAMERA) {
                     // Permission is granted
                     pickImageFromCamer()
                 }


             }
         }

          private val CameratyResultLauncher =
         registerForActivityResult(
             ActivityResultContracts.StartActivityForResult())
         {result->
           if(result.resultCode==Activity.RESULT_OK)
           {
               var data =result.data
               Log.d("data",uri.toString())
               binding.img.setImageURI(uri)

         }
             else{
                 Toast.makeText(this,"Cancelled",Toast.LENGTH_LONG).show()

           }
 }
         */




    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        /*when(requestCode)
        {
            CAMERA_REQUEST_CODE->{
                if(grantResults.size>0)
                {
                    var cameraPermissionAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED
                    var storagePermissionGrantged = grantResults[1]==PackageManager.PERMISSION_GRANTED
                    if(cameraPermissionAccepted && storagePermissionGrantged)
                    {
                        pickImageFromCamer()
                    }
                    else{
                    Toast.makeText(this,"Camera and Storage Permission required ",Toast.LENGTH_LONG).show()
                    }
                }
            }
            STORAGE_PERMISSION_CODE->{
                if(grantResults.size>0)
                {
                    var storagePErmisisonaccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED
                if(storagePErmisisonaccepted){
                    SelectImageFromGallery()
                }
                    else{
                    Toast.makeText(this," Storage Permission required ",Toast.LENGTH_LONG).show()

                }

                }
            }
        }*/
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_SHORT) .show();
                pickImageFromCamer()
            }
            else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT) .show();
            }
        }
        else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.size > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();

                SelectImageFromGallery()
            } else {
                Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

    }

    //Pick Image from gallery
    private fun SelectImageFromGallery() {

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        galleryResultLauncher.launch(intent)

    }

    private val galleryResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult())
        {result->
            if(result.resultCode==Activity.RESULT_OK)
            {
                var data =result.data
                uri = data!!.data!!
                Log.d("data gallery:",uri.toString())
                binding.img.setImageURI(uri)

            }
            else{
                Toast.makeText(this,"Cancelled",Toast.LENGTH_LONG).show()

            }
        }




    private fun pickImageFromCamer() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        uri  = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        CameratyResultLauncher.launch(cameraIntent)
    }

    private val CameratyResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult())
        {result->
            if(result.resultCode==Activity.RESULT_OK)
            {
                var data =result.data
                Log.d("data:",uri.toString())
                binding.img.setImageURI(uri)

            }
            else{
                Toast.makeText(this,"Cancelled",Toast.LENGTH_LONG).show()

            }
        }




}



package com.example.kotlinphotoeditortest

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btChoosePhoto.setOnClickListener {

            btnListener = "btChoose"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED) {

                    val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    
                    requestPermissions(permissions , PERMISSION_CODE)

                } else {

                    pickImageFromGallery()
                    
                }


            } else {

                pickImageFromGallery()
                
            }

        }

        btTakePhoto.setOnClickListener {

            btnListener = "btTake"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                if (checkSelfPermission(android.Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED){

                    val permission = arrayOf(android.Manifest.permission.CAMERA ,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

                    requestPermissions(permission , PERMISSION_CODE)

                } else {
                    openCamera()
                }

            } else {
                openCamera()
            }

        }

    }



    private fun openCamera() {

        val values = ContentValues()

        values.put(MediaStore.Images.Media.TITLE ,"New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION , "From the Camera")
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI ,values)

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT , image_uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)

    }

    private fun pickImageFromGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent , IMAGE_PICK_CODE)
    }

    companion object {

        private val IMAGE_PICK_CODE = 1000

        private val PERMISSION_CODE = 1001

        var image_uri : Uri? = null

        private val IMAGE_CAPTURE_CODE: Int = 1001

        var btnListener : String? = null

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when(requestCode){

            PERMISSION_CODE -> {
                if (grantResults.size >0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){

                    if (btnListener.equals("btChoose")){
                        pickImageFromGallery()
                    } else {
                        openCamera()
                    }


                } else {
                    Toast.makeText(this, "Permission denied" ,Toast.LENGTH_SHORT).show()
                }
            }

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (btnListener.equals("btChoose")){
            if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
                ivPhoto.setImageURI(data?.data)
            }
        } else {
            if (resultCode == Activity.RESULT_OK){
                ivPhoto.setImageURI(image_uri)
            }
        }


    }

}

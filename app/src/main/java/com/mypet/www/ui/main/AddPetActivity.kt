package com.mypet.www.ui.main

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mypet.www.R
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_add_post.*
import java.io.File


class AddPetActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        const val PERMISSIONS_REQUEST_READ_STORAGE = 301
        const val GALLERY_REQUEST_CODE = 201
    }

    private lateinit var resultUri: Uri

    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        firebaseFirestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        firebaseAuth = FirebaseAuth.getInstance()


        initView()

    }

    private fun initView() {
        iv_add_photo.setOnClickListener(this)
        tv_add_photo.setOnClickListener(this)
        tv_select_kind.setOnClickListener(this)
        tv_gender.setOnClickListener(this)
        button_upload.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        p0?.let { view ->
            when (view.id) {
                R.id.iv_add_photo, R.id.tv_add_photo -> {
                    // Add Photo
                    if (isStoragePermissionGranted()) {
                        pickFromGallery()
                    }
                }
                R.id.tv_select_kind -> {
                    showSelectDialog(view, R.array.cat_kinds)
                }
                R.id.tv_gender -> {
                    showSelectDialog(view, R.array.genders)
                }
                R.id.button_upload -> {
                    uploadItems()
                }
            }
        }
    }

    private fun uploadItems() {
        // 1. upload image to firestore
        val file = File(resultUri.path)
        val ref = storageReference.child("images/" + file.name)
        ref.putFile(resultUri).addOnSuccessListener {
            //SUCCESS
            val name = edt_cat_name.text.toString()
            val age = edt_cat_age.text.toString()
            val kind = tv_select_kind.text.toString()
            val gender = tv_gender.text.toString()

            val data = hashMapOf(
                "name" to name,
                "age" to age,
                "kind" to kind,
                "gender" to gender,
                "image" to file.name,
                "email" to firebaseAuth.currentUser?.email,
                "uid" to firebaseAuth.currentUser?.uid
            )
            firebaseFirestore.collection("cats")
                .add(data)
                .addOnSuccessListener {
                    //SUCCESS
                    Toast.makeText(this, "정상적으로 등록되었습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    //ERROR
                    Toast.makeText(this, "에러 발생! 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                }
        }.addOnFailureListener {
            //ERROR
            Toast.makeText(this, "에러 발생! 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
        }

    }

    private fun showSelectDialog(textView: View, itemsId: Int) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Choose")
            .setSingleChoiceItems(
                itemsId, 0
            ) { _, _ ->
            }
            .setPositiveButton(
                "확인"
            ) { dialog, id ->
                // or return them to the component that opened the dialog
                val pos = (dialog as AlertDialog).listView.checkedItemPosition
                val selectedItem = dialog.listView.getItemAtPosition(pos) as String
                (textView as TextView).text = selectedItem
            }
            .setNegativeButton("취소") { _, _ ->
            }
            .show()
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image.png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun isStoragePermissionGranted(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                PERMISSIONS_REQUEST_READ_STORAGE
            )
            return false
        } else {
            // Permission has already been granted
            return true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GALLERY_REQUEST_CODE -> {
                    data?.data?.let { uri ->
                        launchImageCrop(uri)
                    }
                }
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)
                    resultUri = result.uri
                    Glide.with(this)
                        .load(resultUri)
                        .centerCrop()
                        .into(iv_add_photo)
                }
                CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE -> {
                    //ERROR
                }
            }
        }
    }

    private fun launchImageCrop(uri: Uri?) {
        CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON)
            .start(this)
    }
}
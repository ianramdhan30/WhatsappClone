package com.example.whatsappclone.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.whatsappclone.utils.Constants.DATA_IMAGES
import com.example.whatsappclone.utils.Constants.DATA_USERS
import com.example.whatsappclone.utils.Constants.DATA_USER_EMAIL
import com.example.whatsappclone.utils.Constants.DATA_USER_IMAGE_URL
import com.example.whatsappclone.utils.Constants.DATA_USER_NAME
import com.example.whatsappclone.utils.Constants.DATA_USER_PHONE
import com.example.whatsappclone.utils.User
import com.example.whatsappclone.utils.populateImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import idn.whatsappproject.whatsappclone.R
import kotlinx.android.synthetic.main.activity_profile.*
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class ProfileActivity : AppCompatActivity() {
    private val firebaseDb = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseStorage = FirebaseStorage.getInstance().reference
    private var imageUrl: String? = null
    private var nama : String = ""
    private var email : String = ""
    private var phone : String = ""

    var file: File? = null

    private val RC_CAMERA = 1
    val REQUEST_TAKE_PHOTO = 1
    val REQUEST_CHOOSE_PHOTO = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        imbtn_profile.setOnClickListener {
            checkCameraPermission()
            cropImageAutoSelection()

            if (userId.isNullOrEmpty()) {
                finish()                    // jika userId null, ProfileActivity akan dihentikan finish() dan kembali ke MainActivity
            }
        }

        progress_layout.setOnTouchListener { v, event -> true }
        btn_apply.setOnClickListener {
            onApply()
        }

        btn_delete_account.setOnClickListener {
            onDelete()
        }
        populateInfo()
    }

    private fun populateInfo() {
        progress_layout.visibility = View.VISIBLE
        firebaseDb.collection(DATA_USERS)
            .document(userId!!)
            .get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                edt_name_profile.setText(user?.name, TextView.BufferType.EDITABLE)
                edt_email_profile.setText(user?.email, TextView.BufferType.EDITABLE)
                edt_phone_profile.setText(user?.phone, TextView.BufferType.EDITABLE)
                nama = user?.name.toString()
                email = user?.email.toString()
                phone = user?.phone.toString()
                progress_layout.visibility = View.GONE
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                finish()
            }
    }

    private fun onApply() {
        uploadImage()
        progress_layout.visibility = View.VISIBLE
        val name = edt_name_profile.text.toString()
        val email = edt_email_profile.text.toString()
        val phone = edt_phone_profile.text.toString()
        val map = HashMap<String, Any>()

        map[DATA_USER_NAME] = name
        map[DATA_USER_EMAIL] = email
        map[DATA_USER_PHONE] = phone

        if (nama.equals(name) && email.equals(email) && phone.equals(phone)) {
            Toast.makeText(this, "Belum ada data yang diubah", Toast.LENGTH_SHORT).show()
            progress_layout.visibility = View.GONE
        } else {
            if (file == null) {
                Toast.makeText(this, "Belum ada data gambar", Toast.LENGTH_SHORT).show()
                progress_layout.visibility = View.GONE
            } else {
                firebaseDb.collection(DATA_USERS).document(userId!!).update(map) // perintah update
                    .addOnSuccessListener {
                        Toast.makeText(this, "Update Successful", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                        Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show()
                        progress_layout.visibility = View.GONE
                    }
            }
        }
    }

    private fun uploadImage() {
        if (file == null) {
            Toast.makeText(this, "Masih belum ada image", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("InflateParams")
    private fun onDelete() {
        val dialog =  AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.form_data, null)
        dialog.setView(dialogView)
        dialog.setTitle("Hapus akun?")
        dialog.setPositiveButton("Yes") { _, _ ->
            val edtPassword : EditText = dialogView.findViewById(R.id.edt_password)
            val password = edtPassword.text.toString()
            firebaseDb.collection(DATA_USERS)
                .document(userId!!)
                .get()
                .addOnSuccessListener {
                    val user = it.toObject(User::class.java)
                    val pass = user?.password.toString()
                    if (password.equals(pass)) {
                        firebaseDb.collection(DATA_USERS).document(userId).delete()
                        Toast.makeText(applicationContext, "Profile deleted", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(applicationContext, LoginActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(applicationContext, "Password salah", Toast.LENGTH_SHORT).show()
                        progress_layout.visibility = View.GONE
                    }
                }
        }
        dialog.setNegativeButton("No") { _, _ ->
            progress_layout.visibility = View.GONE
        }
        dialog.show()
        dialog.setCancelable(false)
    }

    private fun storeImage(uri: Uri?) {
        if (uri != null) {
            Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show()
            progress_layout.visibility = View.VISIBLE
            val filePath = firebaseStorage.child(DATA_IMAGES).child(userId!!) // membuat folder
            filePath.putFile(uri)
                .addOnSuccessListener {
                    filePath.downloadUrl
                        .addOnSuccessListener {
                            val url = it.toString()
                            firebaseDb.collection(DATA_USERS).document(userId)
                                .update(DATA_USER_IMAGE_URL, url).addOnSuccessListener {
                                    imageUrl = url
                                    populateImage(this, imageUrl, img_profile, R.drawable.ic_user)
                                }
                        }
                    progress_layout.visibility = View.GONE
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            val extras: Bundle? = data?.extras
            val bitmap: Bitmap = extras?.get("data") as Bitmap

            val filesDir: File = applicationContext.filesDir
            val imageFile = File(filesDir, "image" + ".jpg")

            val os: OutputStream
            try {
                os = FileOutputStream(imageFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
                os.flush()
                os.close()
                file = imageFile
                img_profile?.setImageBitmap(bitmap)
            } catch (e: Exception) {
                Log.e(javaClass.simpleName, "Error writing bitmap", e)
            }
        } else if (requestCode == REQUEST_CHOOSE_PHOTO && resultCode == Activity.RESULT_OK) {
            val uri: Uri? = data?.data

            CropImage.activity(uri).setAspectRatio(1, 1).start(this)
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val imageUri: Uri = result.uri
                try {
                    val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)

                    val filesDir: File = this.filesDir!!
                    val imageFile = File(filesDir, "image" + ".jpg")

                    val os: OutputStream = FileOutputStream(imageFile)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
                    os.flush()
                    os.close()
                    file = imageFile
                    if (bitmap != null) {
                        img_profile?.setImageBitmap(bitmap)
                    }
                } catch (e: IOException) {
                    Log.e(javaClass.simpleName, "Error writing bitmap", e)
                    e.printStackTrace()
                }
                storeImage(imageUri)
            }
        }
    }

    private fun checkCameraPermission() {
        val perm: String = Manifest.permission.CAMERA
        if (this.let { EasyPermissions.hasPermissions(it, perm) }) {
        } else {
            EasyPermissions.requestPermissions(this, "Butuh permission camera", RC_CAMERA, perm)
        }
    }

    private fun cropImageAutoSelection() {
        this.let {
            CropImage.activity()
                .setAspectRatio(2, 2)
                .start(this)
        }
    }

}
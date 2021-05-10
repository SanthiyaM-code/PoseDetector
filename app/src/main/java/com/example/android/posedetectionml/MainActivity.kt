package com.example.android.posedetectionml

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.*
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.android.posedetectionml.databinding.ActivityMainBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private lateinit var auth: FirebaseAuth

    private lateinit var logoutBtn: Button
    private lateinit var updatePass: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        if(auth.currentUser == null){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            Toast.makeText(this, "Already logged in", Toast.LENGTH_LONG).show()
        }

        setContentView(R.layout.activity_main)

        logoutBtn = findViewById(R.id.logout_btn)
        updatePass = findViewById(R.id.update_pass_btn)

        logoutBtn.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        updatePass.setOnClickListener{
            val intent = Intent(this, UpdatePassword::class.java)
            startActivity(intent)
        }

//        val options = PoseDetectorOptions.Builder()
//            .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
//            .build()
        binding.takepic.setOnClickListener {
            dispatchTakePictureIntent()
        }


    }

    val REQUEST_IMAGE_CAPTURE = 1
    lateinit var currentPhotoPath: String
    lateinit var photoUri: Uri

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
            Log.d("Successfull",currentPhotoPath.toString())
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    photoUri = FileProvider.getUriForFile(
                        this,
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            binding.imageview.setImageURI(photoUri)

            val options = AccuratePoseDetectorOptions.Builder()
                .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
                .build()

            val poseDetector = PoseDetection.getClient(options)

            val image = InputImage.fromFilePath(applicationContext, photoUri)

            var bitmap = image.bitmapInternal
            bitmap = bitmap?.copy(Bitmap.Config.ARGB_8888, true);
            val canvas =Canvas(bitmap)
            canvas.drawBitmap(bitmap, 0f, 0f, null)
            val paint = Paint()
            paint.color = Color.RED
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 10f


            val taskpose = poseDetector.process(image).addOnSuccessListener {
                Toast.makeText(this, "Task Successful", Toast.LENGTH_SHORT).show()
                val pose = it
//                val allPoseLandmarks = pose.allPoseLandmarks
//                Log.d("POSEMAN",allPoseLandmarks.toString())

                val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
                val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
                val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
                val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
                val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
                val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
                val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
                val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
                val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
                val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
                val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
                val rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)
                val leftPinky = pose.getPoseLandmark(PoseLandmark.LEFT_PINKY)
                val rightPinky = pose.getPoseLandmark(PoseLandmark.RIGHT_PINKY)
                val leftIndex = pose.getPoseLandmark(PoseLandmark.LEFT_INDEX)
                val rightIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX)
                val leftThumb = pose.getPoseLandmark(PoseLandmark.LEFT_THUMB)
                val rightThumb = pose.getPoseLandmark(PoseLandmark.RIGHT_THUMB)
                val leftHeel = pose.getPoseLandmark(PoseLandmark.LEFT_HEEL)
                val rightHeel = pose.getPoseLandmark(PoseLandmark.RIGHT_HEEL)
                val leftFootIndex = pose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX)
                val rightFootIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX)
                val nose = pose.getPoseLandmark(PoseLandmark.NOSE)
                val leftEyeInner = pose.getPoseLandmark(PoseLandmark.LEFT_EYE_INNER)
                val leftEye = pose.getPoseLandmark(PoseLandmark.LEFT_EYE)
                val leftEyeOuter = pose.getPoseLandmark(PoseLandmark.LEFT_EYE_OUTER)
                val rightEyeInner = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE_INNER)
                val rightEye = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE)
                val rightEyeOuter = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE_OUTER)
                val leftEar = pose.getPoseLandmark(PoseLandmark.LEFT_EAR)
                val rightEar = pose.getPoseLandmark(PoseLandmark.RIGHT_EAR)
                val leftMouth = pose.getPoseLandmark(PoseLandmark.LEFT_MOUTH)
                val rightMouth = pose.getPoseLandmark(PoseLandmark.RIGHT_MOUTH)


                val allPoseLandmarks = pose.allPoseLandmarks
                for (item in allPoseLandmarks) {
                    val xPos = item.position.x
                    val yPos = item.position.y
                    canvas.drawCircle(xPos, yPos, 10f, paint)
                }

                binding.imageview.setImageBitmap(bitmap)

            }.addOnFailureListener {
                Toast.makeText(this, "Task Failure", Toast.LENGTH_SHORT).show()
            }




        }
    }



}
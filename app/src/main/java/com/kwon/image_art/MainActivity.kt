package com.kwon.image_art

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.palette.graphics.Palette
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    val slice_list = MutableLiveData<ArrayList<Slice>>()
    val bitmap_list = MutableLiveData<ArrayList<Bitmap>>()

    var onImageCallback: ((bitmap: Bitmap) -> Unit)? = null
    private val request_activity: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == RESULT_OK) {
            activityResult.data!!.data?.let { selectedImage ->
                Thread(Runnable {
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    val cursor: Cursor? = contentResolver?.query(selectedImage, filePathColumn, null, null, null)
                    cursor?.let { cur ->
                        cur.moveToFirst()
                        val columnIndex: Int = cur.getColumnIndex(filePathColumn[0])
                        val picturePath: String = cur.getString(columnIndex)
                        cur.close()
                        val bitmap = BitmapFactory.decodeFile(picturePath)
                        onImageCallback?.let {
                            Handler(Looper.getMainLooper()).post{
                                it.invoke(bitmap)
                            }
                        }
                    }
                }).start()
                return@registerForActivityResult
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val th = StrictMode.ThreadPolicy.Builder().permitDiskReads().permitDiskWrites().build()
        StrictMode.setThreadPolicy(th)

        CoroutineScope(Dispatchers.IO).launch {
            getPathOfAllImg()
        }

        slice_list.observe(this, {
            if(it.isNotEmpty()) {
                try {
                    // Kotlin DSL example
                    val pieChartDSL = buildChart {
                        slices { it }
                        sliceWidth { 40f }
                        sliceStartPoint { 0f }
                        clickListener { angle, index ->

                        }
                    }
                    val pieChart = PieChart(
                        slices = it, clickListener = null, sliceStartPoint = 0f, sliceWidth = 40f
                    ).build()

                    chart_1.setPieChart(pieChartDSL)
                    chart_1.setPieChart(pieChart)

                    chart_2.setPieChart(pieChartDSL)
                    chart_2.setPieChart(pieChart)

                    chart_3.setPieChart(pieChartDSL)
                    chart_3.setPieChart(pieChart)

                    chart_4.setPieChart(pieChartDSL)
                    chart_4.setPieChart(pieChart)

                    chart_5.setPieChart(pieChartDSL)
                    chart_5.setPieChart(pieChart)

                    chart_6.setPieChart(pieChartDSL)
                    chart_6.setPieChart(pieChart)

                    chart_7.setPieChart(pieChartDSL)
                    chart_7.setPieChart(pieChart)

                    chart_8.setPieChart(pieChartDSL)
                    chart_8.setPieChart(pieChart)

                    chart_9.setPieChart(pieChartDSL)
                    chart_9.setPieChart(pieChart)
                } catch (e: Exception) {
                }

            }
        })

//        test_btn.setOnClickListener {
//            val intent_to_gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            intent_to_gallery.resolveActivity(packageManager)?.let {
//                request_activity.launch(intent_to_gallery)
//            }
//
//            onImageCallback = { bitmap ->
//                Palette.from(bitmap).generate {
//                    Log.d("TEST", "팔레트 -> $it")
//                    it?.let { palette ->
//                        val dominantSwatch = palette.dominantSwatch
//
//                        test_la.setBackgroundColor(dominantSwatch?.rgb!!)
//
//                        Log.d("TEST", "RGB -> ${dominantSwatch?.rgb}")
//                        Log.d("TEST", "HSL -> ${dominantSwatch?.hsl}")
//                        Log.d("TEST", "HASHCODE -> ${dominantSwatch?.hashCode()}")
//                        Log.d("TEST", "POPULATION -> ${dominantSwatch?.population}")
//                    }
//                }
//            }
//        }
    }

    private fun getSliceList(bitmap_list: ArrayList<Bitmap>): ArrayList<Slice> {
        val temp_slices = arrayListOf<Slice>()
        var count = 1f
        for(bitmap in bitmap_list) {
            val result = Palette.from(bitmap).generate()
            result.dominantSwatch?.let { swatch ->
                swatch.rgb?.let { rgb ->
                    temp_slices.add(
                        Slice(
                            count,
                            rgb,
                            "$count"
                        )
                    )
                    count++
                }
            }
        }
        slice_list.postValue(temp_slices)

        return temp_slices
    }

    fun getPathOfAllImg(): ArrayList<String> {
        val result: ArrayList<String> = ArrayList()
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI //이미지를 가져오는 경우
        val projection = arrayOf(
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.DISPLAY_NAME
        )
        val cursor = contentResolver.query(
            uri,
            projection,
            null,
            null,
            MediaStore.MediaColumns.DATE_ADDED + " desc"
        )
        val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        val temp_list = arrayListOf<Bitmap>()
        while (cursor.moveToNext()) {
            try {
                val absolutePathOfImage = cursor.getString(columnIndex)
                if (!TextUtils.isEmpty(absolutePathOfImage)) {
                    result.add(absolutePathOfImage)
                    temp_list.add(BitmapFactory.decodeFile(absolutePathOfImage))
                    getSliceList(temp_list)
                }
            } catch (e: Exception) {
                break
            }
        }
        getSliceList(temp_list)
        return result
    }
}
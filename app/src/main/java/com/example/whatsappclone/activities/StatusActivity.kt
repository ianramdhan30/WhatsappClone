package com.example.whatsappclone.activities

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.whatsappclone.listeners.ProgressListener
import com.example.whatsappclone.utils.StatusListElement
import com.example.whatsappclone.utils.populateImage
import idn.whatsappproject.whatsappclone.R
import kotlinx.android.synthetic.main.activity_status.*

class StatusActivity : AppCompatActivity() {
    private lateinit var statusElement: StatusListElement

    companion object {
        val PARAM_STATUS_ELEMENT = "element"
        fun getIntent(context: Context?, statusElement: StatusListElement): Intent {
            val intent = Intent(context, StatusActivity::class.java)
            intent.putExtra(PARAM_STATUS_ELEMENT, statusElement)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)

        if (intent.hasExtra(PARAM_STATUS_ELEMENT)) {
            statusElement = intent.getParcelableExtra(PARAM_STATUS_ELEMENT)!!
        } else {
            Toast.makeText(this, "Unable to get status", Toast.LENGTH_SHORT).show()
            finish() }
        txt_status.text = statusElement.status
        populateImage(this, statusElement.statusUrl, img_status)
        progress_bar.max = 100
       TimerTask(this).execute("")
    }

    override fun onProgressUpdate(progress: Int) {
        progress_bar.progress = progress
        if (progress == 100) {
            finish()
        }
    }

    private class TimerTask(val listener: ProgressListener) : AsyncTask<String, Int, Any>() {
        override fun doInBackground(vararg params: String?): Any {
            var i = 0
            val sleep = 20L // memberikan batas waktu untuk progressBar
            while (1 < 100) { // lakukan perulangan untuk memberikan batas waktu ProgressBar
                i++
                publishProgress(i)
                Thread.sleep (sleep)
            }
        }

        override fun onProgressUpdate(vararg values: Int?) { super.onProgressUpdate(*values)
            if (values[0] != null) {
                listener.onProgressUpdate(values[0]!!) }
        }

    }
}
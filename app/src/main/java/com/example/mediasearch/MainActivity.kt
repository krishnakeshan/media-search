package com.example.mediasearch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import androidx.room.RoomDatabase
import com.clarifai.channel.ClarifaiChannel
import com.clarifai.credentials.ClarifaiCallCredentials
import com.clarifai.grpc.api.*
import com.example.mediasearch.databinding.ActivityMainBinding
import com.example.mediasearch.model.Media
import com.example.mediasearch.model.MediaDAO
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val storage = Firebase.storage

    private lateinit var db: AppDatabase

    private lateinit var mediaDAO: MediaDAO

    private val channel = ClarifaiChannel.INSTANCE.jsonChannel
    private val stub = V2Grpc.newBlockingStub(channel)
        .withCallCredentials(ClarifaiCallCredentials("2bc80d2aa2924b73b3c7e04405feb9b8"))

    private val searchAdapter = SearchAdapter()

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) {
            Toast.makeText(this@MainActivity, "Upload started", Toast.LENGTH_SHORT).show()

            // upload to storage
            val fileRef =
                storage.reference.child("${UUID.randomUUID()}.jpg")
            val uploadTask = fileRef.putFile(it)
            uploadTask.continueWithTask { _ ->
                fileRef.downloadUrl
            }.addOnCompleteListener { task ->
                processUploadedFile(task.result.toString())

                Toast.makeText(this@MainActivity, "Upload completed", Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "main"
        ).build()

        mediaDAO = db.mediaDAO()

        setupViews()
    }

    private fun setupViews() {
        // setup recyclerview
        binding.searchResultsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchAdapter
        }

        clickListeners()

        // setup search listener
        binding.searchEditText.addTextChangedListener {
            searchMedia(it.toString())
        }
    }

    private fun clickListeners() {
        binding.addMediaButton.setOnClickListener {
            getContent.launch("image/*")
        }
    }

    private fun processUploadedFile(url: String) {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                val response = stub.postModelOutputs(
                    PostModelOutputsRequest.newBuilder()
                        .setModelId("aaa03c23b3724a16a56b629203edc62c")
                        .addInputs(
                            Input.newBuilder().setData(
                                Data.newBuilder().setImage(
                                    Image.newBuilder().setUrl(url)
                                )
                            )
                        ).build()
                )

                val media = Media(
                    type = Media.MediaType.IMAGE,
                    url = url,
                    tags = response.outputsList.first().data.conceptsList.joinToString {
                        it.name
                    }
                )

                mediaDAO.insert(media)
            }
        }
    }

    private fun searchMedia(query: String) {
        if (query.isNotEmpty()) {
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    val result = mediaDAO.findByTag(query)
                    searchAdapter.setData(result)
                }
            }
        }
    }
}
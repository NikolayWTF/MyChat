package com.example.mychat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.mychat.databinding.ActivityChatSelectionBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

// Окно с выбором собеседника
class ChatSelection : AppCompatActivity() {
    lateinit var binding: ActivityChatSelectionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val id = getIntent().getStringExtra("id") // Передал id Вошедшего пользователя
        val database = Firebase.database
        val IdRef = database.getReference("User").child(id.toString()) // Ссылка на пользователя
        val MainRef = database.getReference("User") // Ссылка на всю таблицу User

        fun sendPost(id: String, api: String): StringBuffer {
            var urlPost: String
            if (api == "get_name") {
                if (id == "") {
                    urlPost = "http://10.0.2.2:8888/$api" // URL для запроса
                } else {
                    urlPost = "http://10.0.2.2:8888/$api?id=$id" // URL для запроса
                }
            }
            else{
                if (id == ""){
                    urlPost = "http://10.0.2.2:8888/$api" // URL для запроса
                }
                else{
                    urlPost = "http://10.0.2.2:8888/$api?name=$id" // URL для запроса
                }
            }
            val mURL = URL(urlPost)
            with(mURL.openConnection() as HttpURLConnection) {
                requestMethod = "POST"

                val wr = OutputStreamWriter(outputStream)
                wr.flush()
                println("\nSending 'POST' request to URL : $url")
                println("Response Code : $responseCode")

                BufferedReader(InputStreamReader(inputStream)).use {
                    val response = StringBuffer()
                    var inputLine = it.readLine()
                    while (inputLine != null) {
                        response.append(inputLine)
                        inputLine = it.readLine()
                    }
                    it.close()
                    println("POST Response : $response")
                    return response
                }
            }
        }
        var name: StringBuffer
        // Вывод в левом верхнем углу экрана имени пользователя
        GlobalScope.launch { // запуск новой сопрограммы в фоне
            name = sendPost(id.toString(), "get_name.php")
            binding.UserName.text = name
        }

        // Обработка кнопки для входа в глобальный чат
        binding.bGeneralChat.setOnClickListener {
            val EntryOnChat = Intent(this, MainActivity::class.java)
            EntryOnChat.putExtra("id", id)
            startActivity(EntryOnChat)
        }

        // Обработка кнопки CreateChat
        binding.CreateChat.setOnClickListener {
            val intent = Intent(this, CreateChat::class.java)
            intent.putExtra("UserId", id)
            startActivity(intent)
            }

        binding.MyChats.setOnClickListener {

            val intent = Intent(this, MyChats::class.java)
            intent.putExtra("UserId", id)
            startActivity(intent)

        }

        fun NoPerson(){
            println("Данный пользователь не зарегистрирован")
        }
        fun PersonChat(index: String){
            val intent = Intent(this, PrivateChat::class.java)
            intent.putExtra("UserId", id)
            intent.putExtra("ReceivedId", index)
            startActivity(intent)
        }

        // Обработка поисковой строки
        binding.Search.setOnClickListener {
            val name = binding.SearchName.text.toString()
            GlobalScope.launch { // запуск новой сопрограммы в фоне
                val id = getIntent().getStringExtra("id") // Передал id Вошедшего пользователя
                val index = sendPost(name, "get_id.php")
                binding.UserName.text = name
                if (index.toString() == "Пользователь не зарегистрирован в чате" || id.toString() == "Некорректный запрос")
                {
                    NoPerson()
                }
                else
                {
                    PersonChat(index.toString())
                }
            }
        }

        }
    }



package com.example.mychat


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.mychat.databinding.ActivitySignInBinding
import com.google.firebase.database.core.utilities.Validation
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.security.MessageDigest
import kotlinx.coroutines.*

// Окно регистрации
class SignInActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Переход на окно выбора чата и передача туда id вошедшего
        fun EntryOnChatSelectionActivity(id: String) {
            val EntryOnChat = Intent(this, ChatSelection::class.java)
            EntryOnChat.putExtra("id", id)
            startActivity(EntryOnChat)
        }

        fun getHash(text: ByteArray): String {
            var result = MessageDigest.getInstance("SHA256").digest(text)
            return with(StringBuilder()) {
                result.forEach { b -> append(String.format("%02X", b)) }
                toString().toLowerCase()
            }
        }

        fun RegisterValidation(id: String) {
            if (id == "Данное имя пользователя занято") {
                println("Данное имя пользователя занято")
            } else {
                if (id == "Некорректный запрос") {
                    println("Некорректный запрос")
                } else {
                    println("Регистрация прошла успешно")
                    EntryOnChatSelectionActivity(id)
                }
            }
        }

        fun EntryValidation(id: String) {
            if (id == "Пароль и имя пользователя не совпадают") {
                println("Пароль и имя пользователя не совпадают")
            } else {
                if (id == "Пользователь с таким именем не зарегистрирован") {
                    println("Пользователь с таким именем не зарегистрирован")
                } else {
                    if (id ==  "Некорректный запрос"){
                        println("Некорректный запрос")
                    }
                    else{
                        println("Добро пожаловать")
                        EntryOnChatSelectionActivity(id)
                    }

                }
            }
        }

        fun sendPost(name: String, pass: String, api: String): StringBuffer {
            var urlPost: String
            if ((name == "") || (pass == "")) {
                urlPost = "http://10.0.2.2:8888/$api" // URL для запроса
            } else {
                urlPost =
                    "http://10.0.2.2:8888/$api?name=" + name + "&" + "pass=" + pass // URL для запроса
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

        // Проверка на корректную регистрацию
        binding.buttonSignUp.setOnClickListener {
            val name = binding.inputLogin.text.toString() // Считываю ввод с поля логин
            val pass = binding.inputPass.text.toString() // Считываю ввод с поля пароль
            val password = getHash(pass.toByteArray())
            var id: StringBuffer
            GlobalScope.launch { // запуск новой сопрограммы в фоне
                id = sendPost(name, password, "add_user.php")
                RegisterValidation(id.toString())
            }
        }

        binding.buttonSignIn.setOnClickListener(){
            val name = binding.inputLogin.text.toString() // Считываю ввод с поля логин
            val pass = binding.inputPass.text.toString() // Считываю ввод с поля пароль
            val password = getHash(pass.toByteArray())
            var id: StringBuffer
            GlobalScope.launch { // запуск новой сопрограммы в фоне
                id = sendPost(name, password, "auth.php")
                EntryValidation(id.toString())
            }
        }
    }
}


package com.example.mychat


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast

import com.example.mychat.databinding.ActivitySignInBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
// Окно регистрации
class SignInActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = Firebase.database
        // Ссыдка на таблицу пользователей
        val myRef = database.getReference("User")

        // Ошибка в регистрации
        fun ErrorRegisterToast()
        {
            val ErrorToast = Toast.makeText(this, "К сожалению данное имя уже занято", Toast.LENGTH_SHORT)
            ErrorToast.show()
        }
        // Успешная регистрация
        fun GoodRegisterToast()
        {
            val GoodToast = Toast.makeText(this, "Регистрация прошла успешно", Toast.LENGTH_SHORT)
            GoodToast.show()
        }
        // Вывод сообщения об ошибке входа
        fun ErrorEntryToast()
        {
            val ErrorToast = Toast.makeText(this, "К сожалению имя пользователя и пароль не совпадают", Toast.LENGTH_SHORT)
            ErrorToast.show()
        }
        // Вывод сообщения об успешном входе
        fun GoodEntryToast()
        {
            val GoodToast = Toast.makeText(this, "Добро пожаловать!", Toast.LENGTH_SHORT)
            GoodToast.show()
        }
        // Переход на окно выбора чата и передача туда id вошедшего
        fun EntryOnChatSelectionActivity(id: String)
        {
            val EntryOnChat = Intent(this, ChatSelection::class.java)
            EntryOnChat.putExtra("id", id)
            startActivity(EntryOnChat)
        }
        // Проверка на корректную регистрацию
        binding.buttonSignUp.setOnClickListener{
            val name = binding.inputLogin.text.toString() // Считываю ввод с поля логин
            val pass = binding.inputPass.text.toString() // Считываю ввод с поля пароль
            val user = User(name, pass) // создаю объект класса User, который будет добавлен в БД


            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                     var valid = 1 // Переменная для определения свободно ли имя
                     var id = 0 // Это будет id пользователя

                    for (postSnapshot in dataSnapshot.children) {
                        if (name == postSnapshot.child("name").getValue().toString())
                        {
                            // Такое имя уже зарегистрировано
                            valid = 0

                        }
                        id += 1
                    }

                    if (valid == 1)
                    {
                        //Логин свободен - добавляем его
                        myRef.child(id.toString()).setValue(user)
                        GoodRegisterToast()

                    }
                    else
                    {
                    //К сожалению такой логин уже занят.
                    ErrorRegisterToast()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })

        }
        // Проверка на корректный вход
        binding.buttonSignIn.setOnClickListener{
            val name = binding.inputLogin.text.toString()
            val pass = binding.inputPass.text.toString()
            myRef.addValueEventListener(object: ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var id = ""
                    var valid = 0
                    var Name = ""
                    var Pass = ""
                    for (postSnapshot in dataSnapshot.children) {
                        Name = postSnapshot.child("name").getValue().toString()
                        Pass = postSnapshot.child("pass").getValue().toString()
                        if ((name == Name) && (Pass == pass))
                        {
                            valid = 1
                            id = postSnapshot.key.toString()
                        }
                    }
                    if (valid == 0)
                    {
                        ErrorEntryToast()
                    }
                    else
                    {
                        GoodEntryToast()
                        EntryOnChatSelectionActivity(id)
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    }
}
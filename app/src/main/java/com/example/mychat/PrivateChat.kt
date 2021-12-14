package com.example.mychat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.mychat.databinding.ActivityPrivateChatBinding
import com.example.mychat.databinding.ActivitySignInBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class PrivateChat : AppCompatActivity() {
    lateinit var binding: ActivityPrivateChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivateChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val UserId = getIntent().getStringExtra("UserId") // Передал имя вошедшего пользователя
        val ReceivedId = getIntent().getStringExtra("ReceivedId") // Передал имя собеседника
        val database = Firebase.database // Ссылка на БД
        val MainRef = database.getReference("User") // Ссылка на Таблицу пользователей
        var ChatN = ""
        var UserName = ""
        var RecivedName = ""

        fun InputInChat(ChatName: DatabaseReference)
        {
            // Запись сообщения в чат
            binding.bSendInPrivateChat.setOnClickListener{
                ChatName.setValue(UserName + ": " + binding.PrivateChatInput.text.toString())
            }
        }

        fun  OutputInChat(ChatName: DatabaseReference)
        {
            // Считывание сообщения из БД
            ChatName.addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    binding.apply {
                        PrivateChatOutput.append("\n")
                        PrivateChatOutput.append(snapshot.value.toString())
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }

        fun ChatNameFun(UserName: String, RecivedName: String)
        {
            // Называем чат. Проверка нужна, потому что чат между А и Б это то же самое, что и чат между Б и А
            if (UserId!!.toInt() > ReceivedId!!.toInt())
            {
                ChatN = ("Chat between " + UserName + " and " + RecivedName)
                
            }
            else
            {
                ChatN = ("Chat between " + RecivedName + " and " + UserName)
            }
            binding.PrivateChatName.text = ChatN
            var ChatName = database.getReference("Chats").child(ChatN) // Ссылка на чат
            InputInChat(ChatName)
            OutputInChat(ChatName)
        }

        // Узнаю имена собеседников
        MainRef.addValueEventListener(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                UserName = snapshot.child(UserId.toString()).child("name").getValue().toString()
                binding.UserNameinPrivateChat.text = UserName
                RecivedName = snapshot.child(ReceivedId.toString()).child("name").getValue().toString()
                ChatNameFun(UserName, RecivedName)
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })


    }
}
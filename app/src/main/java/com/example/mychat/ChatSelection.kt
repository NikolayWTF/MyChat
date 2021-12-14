package com.example.mychat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.mychat.databinding.ActivityChatSelectionBinding
import com.example.mychat.databinding.ActivitySignInBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.jar.Attributes


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

        var name = ""
        var IdFirstPerson = ""
        var IdSecondPerson = ""
        var IdThirdPerson = ""
        // Вывод в левом верхнем углу экрана имени пользователя
        IdRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                name = dataSnapshot.child("name").getValue().toString()
                binding.UserName.text = name
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        // Подписываю кнопки, которые начинают быстрый чат с пользователем.
        MainRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var count = 0;
                for (index in dataSnapshot.children) {

                    if (id != index.key.toString()) {
                        if (count == 0) {
                            IdFirstPerson = index.key.toString()
                            binding.bFirstPerson.text = index.child("name").getValue().toString()
                        } else
                            if (count == 1) {
                                IdSecondPerson = index.key.toString()
                                binding.bSecondPerson.text = index.child("name").getValue().toString()
                            } else
                                if (count == 2) {
                                    IdThirdPerson = index.key.toString()
                                    binding.bThirdPerson.text = index.child("name").getValue().toString()
                                }

                        count++
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        // Обработка кнопки для входа в глобальный чат
        binding.bGeneralChat.setOnClickListener {
            val EntryOnChat = Intent(this, MainActivity::class.java)
            EntryOnChat.putExtra("id", id)
            startActivity(EntryOnChat)
        }

        // Обработка кнопки bFirstPerson
        binding.bFirstPerson.setOnClickListener{

            val intent = Intent(this, PrivateChat::class.java)
            intent.putExtra("UserId", id)
            intent.putExtra("ReceivedId", IdFirstPerson)
            startActivity(intent)

            }

        // Обработка кнопки bSecondPerson
        binding.bSecondPerson.setOnClickListener {

            val intent = Intent(this, PrivateChat::class.java)
            intent.putExtra("UserId", id)
            intent.putExtra("ReceivedId", IdSecondPerson)
            startActivity(intent)

            }

        // Обработка кнопки bThirdPerson
        binding.bThirdPerson.setOnClickListener {

                    val intent = Intent(this, PrivateChat::class.java)
                    intent.putExtra("UserId", id)
                    intent.putExtra("ReceivedId", IdThirdPerson)
                    startActivity(intent)

            }






    }
}


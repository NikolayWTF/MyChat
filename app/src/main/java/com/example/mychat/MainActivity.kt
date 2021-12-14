package com.example.mychat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mychat.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = getIntent().getStringExtra("id") // Передал id Вошедшего пользователя
        val database = Firebase.database // Ссылка на Базу Данных
        val MessageRef = database.getReference("Chats").child("General Chat") // Ссылка на общий чат
        val MainRef = database.getReference("User")
        // Определяю имя вошедшего пользователя
        var name = ""
        MainRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                name = snapshot.child(id.toString()).child("name").getValue().toString()
                binding.UserNameinGeneralChat.text = name
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        // Записываю сообщение в БД
        binding.buttonSend.setOnClickListener{
            MessageRef.setValue(name + ": " + binding.input.text.toString())
        }
        // Читаю сообщение из БД
        MessageRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.apply {
                    output.append("\n")
                    output.append(snapshot.value.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
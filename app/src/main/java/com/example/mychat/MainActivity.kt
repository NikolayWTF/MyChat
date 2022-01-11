package com.example.mychat

import android.content.Intent
import android.icu.util.Output
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.ListAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mychat.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.theartofdev.edmodo.cropper.CropImage

// Это глобальный чат
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var adapter: UserRCAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Передал id Вошедшего пользователя
        val id = getIntent().getStringExtra("id")
        // Ссылка на Базу Данных
        val database = Firebase.database
        // Ссылка на глобальный чат
        val MessageRef = database.getReference("Chats").child("General Chat")
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
        var message_id = 0 // Это будет id для нового сообщения
        MessageRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                message_id = 0
                for (postSnapshot in dataSnapshot.children) {
                    message_id += 1
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })




        // Записываю сообщение в БД
        binding.buttonSend.setOnClickListener{
            val user = UserRC(name, binding.input.text.toString())
            MessageRef.child(message_id.toString()).setValue(user)
        }

        // Читаю сообщение из БД и вывожу его на экран
        MessageRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var list = ArrayList<UserRC>()
                for (s in snapshot.children) {
                    val user = s.getValue(UserRC::class.java)
                    if (user != null) {
                        list.add(user)
                    }
                }
                adapter.submitList(list)

            }

            override fun onCancelled(error: DatabaseError) {}
        })
        initRcView()


    }
    private fun initRcView() = with(binding){
        adapter = UserRCAdapter()
        rcView.layoutManager = LinearLayoutManager(this@MainActivity)
        rcView.adapter = adapter
    }


}
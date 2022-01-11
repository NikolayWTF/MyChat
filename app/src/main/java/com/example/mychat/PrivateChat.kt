package com.example.mychat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mychat.databinding.ActivityPrivateChatBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class PrivateChat : AppCompatActivity() {
    lateinit var binding: ActivityPrivateChatBinding
    lateinit var adapter: UserRCPrivateAdapter
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

        fun InputInChat(ChatName: DatabaseReference, message: Int) {
            // Запись сообщения в чат

            binding.bSendInPrivateChat.setOnClickListener {
                var user = UserRC(UserName, binding.PrivateChatInput.text.toString())
                ChatName.child((message - 1).toString())
                    .setValue(user)
            }
        }
        fun OutputInChat(ChatName: DatabaseReference, message: Int, snapshot: DataSnapshot){
            // Вывод на экран

                if (snapshot.child((message).toString()).value.toString() != null) {
                    var list = ArrayList<UserRC>()
                    for (s in snapshot.children) {
                        val user = s.getValue(UserRC::class.java)
                        if (user != null) {
                            list.add(user)
                        }
                    }
                    Log.d("MyLog", list.toString())
                    adapter.submitList(list)
                }
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
            // Считаю id для того чтобы хранить сообщения
            var ChatName = database.getReference("Chats").child(ChatN) // Ссылка на чат
            ChatName.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var message_id = 0
                    for (i in snapshot.children) {
                        message_id += 1
                    }
                    Log.d("MyLog", "Вызываю Input")
                    InputInChat(ChatName, message_id)
                    OutputInChat(ChatName, message_id, snapshot)
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            } )
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
        initRcView()
    }
    private fun initRcView() = with(binding){
        adapter = UserRCPrivateAdapter()
        rcViewPrivate.layoutManager = LinearLayoutManager(this@PrivateChat)
        rcViewPrivate.adapter = adapter
    }
}
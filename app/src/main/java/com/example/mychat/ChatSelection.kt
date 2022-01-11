package com.example.mychat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.mychat.databinding.ActivityChatSelectionBinding
import com.example.mychat.databinding.ActivitySignInBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import org.jetbrains.annotations.Contract
import java.util.jar.Attributes

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

        var name = ""
        // У нас есть 3 кнопки с быстрым доступом к сообщениям. Если в базе меньше 3 пользователей то выводить нечего - для проверки этого
        // Нужны эти пеерменные
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

        fun NoPersonToast(){
            val NoPerson = Toast.makeText(this, "Данный пользователь не зарегистрирован", Toast.LENGTH_SHORT)
            NoPerson.show()
        }
        fun PersonChat(index: String){
            val intent = Intent(this, PrivateChat::class.java)
            intent.putExtra("UserId", id)
            intent.putExtra("ReceivedId", index)
            startActivity(intent)
        }

        // Это функция для добавления аватарки пользователя
//        fun ChangePhotoUser(){
//            CropImage.activity() // Запускаю activity
//                .setAspectRatio(1, 1) // activity не будет расстягиваться
//                .setRequestedSize(600, 600)// Уменьшаю размер передаваемого фото
//                .setCropShape(CropImageView.CropShape.OVAL) // Делаю фото круглым
//                .start(this) // Запускаю
//        }
//
//        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//
//            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null)
//            {
//                val uri = CropImage.getActivityResult(data).uri
//                val path = database.getReference("User").child(id.toString()).child("avatar")
//                path.setValue(uri.toString()).addOnCompleteListener{
//                    if (it.isSuccessful){
//                        Toast.makeText(this,"Данные обновлены", Toast.LENGTH_SHORT)
//                    }
//                }
//            }
//        }


        // Обработка поисковой строки
        binding.Search.setOnClickListener{
            val name = binding.SearchName.text.toString()
            MainRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var id = ""
                    var valid = 1
                    for (index in dataSnapshot.children)
                    {
                        if (name == index.child("name").getValue().toString())
                        {
                            id = index.key.toString()
                            valid = 0
                        }
                    }
                    if (valid == 1)
                    {
                        NoPersonToast()
                    }
                    else
                    {
                        PersonChat(id)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }

//        binding.Camera.setOnClickListener{
//            ChangePhotoUser()
//        }





    }
}


package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.model.ContactData
import com.example.myapplication.view.ContactAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var addsBtn: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var contactList: ArrayList<ContactData>
    private lateinit var contactAdapter: ContactAdapter
    val db = Firebase.firestore
    val CONTACT_COLLECTION = "contacts";
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.fetchContacts();
        /**set List*/
        contactList = ArrayList()
        /**set find Id*/
        addsBtn = findViewById(R.id.addingBtn)
        recyclerView = findViewById(R.id.mRecycler)
        /**set Adapter*/
        contactAdapter = ContactAdapter(this, contactList)
        /**setRecycler view Adapter*/
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = contactAdapter
        /**set Dialog*/
        addsBtn.setOnClickListener { addNewContact() }

    }

    private fun addNewContact() {
        val v = LayoutInflater.from(this)
            .inflate(R.layout.add_new, null)

        val name = v.findViewById<EditText>(R.id.ed_name)
        val phoneNumber = v.findViewById<EditText>(R.id.ed_phone)
        val address = v.findViewById<EditText>(R.id.ed_address)

        val addDialog = AlertDialog.Builder(this)

        addDialog.setView(v)
        addDialog.setPositiveButton("Ok") { dialog, _ ->
            val names = name.text.toString()
            val phoneNumbers = phoneNumber.text.toString()
            val addresses = address.text.toString()
            val objectModel = ContactData(names, phoneNumbers, addresses);
            this.addContactToFireStore(objectModel);

            contactList.add(objectModel)

            contactAdapter.notifyDataSetChanged()
            Toast.makeText(this, "Adding User Information Success", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        addDialog.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
            Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show()

        }
        addDialog.create()
        addDialog.show()
    }

    private fun fetchContacts() {
        db.collection(this.CONTACT_COLLECTION)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val data = document.toObject(ContactData::class.java)
                    contactList.add(data)
                    Log.d("TAG", "${document.id} => $data")
                }

                recyclerView.apply {
                    layoutManager =
                        LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
                    adapter = ContactAdapter(this@MainActivity, contactList)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents.", exception)
            }
    }

    private fun addContactToFireStore(document : ContactData) {
        // Add a new document with a generated ID
        db.collection(this.CONTACT_COLLECTION)
            .add(document)
            .addOnSuccessListener { documentReference ->
                documentReference.set(document)
                Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error adding document", e)
            }
    }
}

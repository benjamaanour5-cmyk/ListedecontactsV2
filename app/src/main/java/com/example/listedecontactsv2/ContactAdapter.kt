package com.example.listedecontactsv2

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.google.android.material.imageview.ShapeableImageView
import java.io.File

class ContactAdapter(context: Context, private val contacts: List<Contact>) :
    ArrayAdapter<Contact>(context, 0, contacts) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_contact, parent, false)

        val contact = contacts[position]
        val photo = view.findViewById<ShapeableImageView>(R.id.imgPhoto)
        val txtNom = view.findViewById<TextView>(R.id.txtNom)
        val txtTel = view.findViewById<TextView>(R.id.txtTel)

        txtNom.text = "${contact.nom} ${contact.prenom}"
        txtTel.text = contact.telephone

        if (contact.photoPath != null) {
            photo.setImageURI(Uri.fromFile(File(contact.photoPath!!)))
        } else {
            photo.setImageResource(R.drawable.ic_person)
        }
        return view
    }
}
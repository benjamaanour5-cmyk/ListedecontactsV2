package com.example.listedecontactsv2

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.imageview.ShapeableImageView
import java.io.File
import java.io.FileOutputStream

class DetailActivity : AppCompatActivity() {

    private lateinit var contact: Contact
    private lateinit var imgPhoto: ShapeableImageView
    private lateinit var edtNom: EditText
    private lateinit var edtPrenom: EditText
    private lateinit var edtTel: EditText
    private lateinit var edtEmail: EditText

    // Galerie
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri == null) {
                Toast.makeText(this, "Sélection de photo annulée", Toast.LENGTH_SHORT).show()
            } else {
                val input = contentResolver.openInputStream(uri)
                if (input != null) {
                    val file = newPhotoFile()
                    FileOutputStream(file).use { out -> input.copyTo(out) }
                    input.close()
                    setPhoto(file.absolutePath)
                } else {
                    Toast.makeText(this, "Impossible de lire l'image", Toast.LENGTH_SHORT).show()
                }
            }
        }

    // Caméra
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
            if (bitmap == null) {
                Toast.makeText(this, "Prise de photo annulée", Toast.LENGTH_SHORT).show()
            } else {
                val file = newPhotoFile()
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                }
                setPhoto(file.absolutePath)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        @Suppress("DEPRECATION")
        contact = intent.getSerializableExtra("contact") as Contact

        imgPhoto = findViewById(R.id.imgPhoto)
        edtNom = findViewById(R.id.edtNom)
        edtPrenom = findViewById(R.id.edtPrenom)
        edtTel = findViewById(R.id.edtTel)
        edtEmail = findViewById(R.id.edtEmail)

        edtNom.setText(contact.nom)
        edtPrenom.setText(contact.prenom)
        edtTel.setText(contact.telephone)
        edtEmail.setText(contact.email)
        showPhoto()

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { annuler() }
        findViewById<Button>(R.id.btnAnnuler).setOnClickListener { annuler() }
        findViewById<Button>(R.id.btnAppeler).setOnClickListener { appeler() }
        findViewById<Button>(R.id.btnEmail).setOnClickListener { envoyerEmail() }
        findViewById<Button>(R.id.btnGalerie).setOnClickListener { galleryLauncher.launch("image/*") }
        findViewById<Button>(R.id.btnCamera).setOnClickListener { prendrePhoto() }
        findViewById<Button>(R.id.btnEnregistrer).setOnClickListener { enregistrer() }
    }

    // ---------- Photo ----------
    private fun newPhotoFile(): File = File(filesDir, "photo_${System.currentTimeMillis()}.jpg")

    private fun setPhoto(path: String) {
        contact.photoPath = path
        showPhoto()
    }

    private fun showPhoto() {
        if (contact.photoPath != null) {
            imgPhoto.setImageURI(Uri.fromFile(File(contact.photoPath!!)))
        } else {
            imgPhoto.setImageResource(R.drawable.ic_person)
        }
    }

    private fun prendrePhoto() {
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            Toast.makeText(this, "Caméra indisponible sur cet appareil", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            cameraLauncher.launch(null)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Aucune application caméra trouvée", Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            Toast.makeText(this, "Accès à la caméra refusé", Toast.LENGTH_SHORT).show()
        }
    }

    // ---------- Intents implicites ----------
    private fun appeler() {
        val tel = edtTel.text.toString().trim()
        if (tel.isEmpty()) {
            edtTel.error = "Numéro vide"
            return
        }
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$tel"))
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Aucune application d'appel disponible", Toast.LENGTH_SHORT).show()
        }
    }

    private fun envoyerEmail() {
        val email = edtEmail.text.toString().trim()
        if (email.isEmpty()) {
            edtEmail.error = "E-mail vide"
            return
        }
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        }
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Aucune application de messagerie disponible", Toast.LENGTH_SHORT).show()
        }
    }

    // ---------- Enregistrer / Annuler ----------
    private fun enregistrer() {
        val nom = edtNom.text.toString().trim()
        val prenom = edtPrenom.text.toString().trim()
        val tel = edtTel.text.toString().trim()
        val email = edtEmail.text.toString().trim()

        if (nom.isEmpty()) { edtNom.error = "Nom obligatoire"; return }
        if (prenom.isEmpty()) { edtPrenom.error = "Prénom obligatoire"; return }
        if (tel.isEmpty()) { edtTel.error = "Téléphone obligatoire"; return }

        contact.nom = nom
        contact.prenom = prenom
        contact.telephone = tel
        contact.email = email

        val result = Intent().putExtra("contact", contact)
        setResult(Activity.RESULT_OK, result)
        finish()
    }

    private fun annuler() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}
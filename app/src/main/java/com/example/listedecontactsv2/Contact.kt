package com.example.listedecontactsv2

import java.io.Serializable

data class Contact(
    var nom: String,
    var prenom: String,
    var telephone: String,
    var email: String,
    var photoPath: String? = null
) : Serializable
package com.example.datamanager.util

object FieldFormatter {

    fun formatFieldLabel(key: String): String {
        return when (key) {
            "username" -> "Kullanıcı Adı"
            "password" -> "Şifre"
            "email" -> "E-Posta"
            "website" -> "Web Sitesi / URL"
            "card_holder" -> "Kart Üzerindeki İsim"
            "card_number" -> "Kart Numarası"
            "expiry_date" -> "Son Kullanma Tarihi"
            "cvv" -> "CVV / Güvenlik Kodu"
            "iban" -> "IBAN"
            "bank_name" -> "Banka Adı"
            "account_holder" -> "Hesap Sahibi"
            "account_number" -> "Hesap Numarası"
            "first_name" -> "Ad"
            "last_name" -> "Soyad"
            "id_number" -> "T.C. Kimlik No"
            "phone" -> "Telefon"
            "birth_date" -> "Doğum Tarihi"
            "address" -> "Adres"
            "postal_code" -> "Posta Kodu"
            else -> key.replace("_", " ").replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }
}

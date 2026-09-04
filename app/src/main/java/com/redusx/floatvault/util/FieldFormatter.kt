package com.redusx.floatvault.util

import android.content.Context
import androidx.annotation.StringRes
import com.redusx.floatvault.R

object FieldFormatter {

    @StringRes
    fun getFieldLabelRes(key: String): Int? {
        return when (key.lowercase()) {
            "username" -> R.string.field_username
            "password" -> R.string.field_password
            "email" -> R.string.field_email
            "website", "url" -> R.string.field_website
            "card_holder", "cardholder_name" -> R.string.field_card_holder
            "card_number" -> R.string.field_card_number
            "expiry_date" -> R.string.field_expiry_date
            "cvv", "cvc" -> R.string.field_cvv
            "iban" -> R.string.field_iban
            "bank_name" -> R.string.field_bank_name
            "account_holder" -> R.string.field_account_holder
            "account_number" -> R.string.field_account_number
            "branch_code" -> R.string.field_branch_code
            "first_name" -> R.string.field_first_name
            "last_name" -> R.string.field_last_name
            "id_number", "tc_no" -> R.string.field_id_number
            "serial_number" -> R.string.field_serial_number
            "birth_date" -> R.string.field_birth_date
            "phone" -> R.string.field_phone
            "address" -> R.string.field_address
            "neighborhood" -> R.string.field_neighborhood
            "district" -> R.string.field_district
            "city" -> R.string.field_city
            "postal_code" -> R.string.field_postal_code
            "country" -> R.string.field_country
            "note_content" -> R.string.field_note_content
            "notes" -> R.string.field_notes
            else -> null
        }
    }

    fun formatFieldLabel(context: Context, key: String): String {
        val resId = getFieldLabelRes(key)
        return if (resId != null) {
            context.getString(resId)
        } else {
            key.replace("_", " ").replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }

    fun formatFieldLabel(key: String): String {
        return key.replace("_", " ").replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
}

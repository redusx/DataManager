package com.redusx.floatvault.data.model

import androidx.annotation.StringRes
import com.redusx.floatvault.R

data class FieldItem(
    val key: String,
    val value: String,
    val type: FieldType = FieldType.TEXT,
    val isSensitive: Boolean = false
)

fun FieldItem.isEffectivelySensitive(category: String): Boolean {
    if (this.isSensitive) return true
    val nameKeys = listOf(
        "full_name", "first_name", "last_name",
        "card_holder", "cardholder_name",
        "account_holder", "bank_name", "title"
    )
    val keyLower = this.key.lowercase()
    if (nameKeys.contains(keyLower)) return false

    val cat = Category.fromId(category)
    return when (cat) {
        Category.FINANCIAL -> true // All bank and card data except names
        Category.PERSONAL -> {
            // Identity doc numbers, dates, serials
            keyLower in listOf("id_number", "tc_no", "serial_number", "birth_date", "expiry_date", "document_no")
        }
        else -> this.isSensitive
    }
}

enum class FieldType {
    TEXT,
    PASSWORD,
    CARD_NUMBER,
    IBAN,
    DATE,
    NUMBER,
    MULTILINE;

    companion object {
        fun fromString(value: String): FieldType {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: TEXT
        }
    }
}

enum class Category(val id: String, val label: String) {
    PERSONAL("personal", "Kişisel"),
    FINANCIAL("financial", "Finansal"),
    ACCOUNT("account", "Hesaplar"),
    CUSTOM("custom", "Özel");

    companion object {
        fun fromId(id: String?): Category {
            return entries.firstOrNull { it.id == id } ?: CUSTOM
        }
    }
}

enum class TemplateType(
    val id: String,
    val title: String,
    val description: String,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    val category: Category
) {
    LOGIN(
        id = "login",
        title = "Giriş & Hesap",
        description = "Web sitesi, uygulama hesapları ve şifreler",
        titleRes = R.string.template_login_title,
        descriptionRes = R.string.template_login_desc,
        category = Category.ACCOUNT
    ),
    CARD(
        id = "card",
        title = "Banka & Kart Bilgileri",
        description = "Kredi kartı, banka kartı, IBAN ve hesap bilgileri",
        titleRes = R.string.template_card_title,
        descriptionRes = R.string.template_card_desc,
        category = Category.FINANCIAL
    ),
    BANK_ACCOUNT(
        id = "bank_account",
        title = "Banka & Kart Bilgileri",
        description = "Kredi kartı, banka kartı, IBAN ve hesap bilgileri",
        titleRes = R.string.template_card_title,
        descriptionRes = R.string.template_card_desc,
        category = Category.FINANCIAL
    ),
    IDENTITY(
        id = "identity",
        title = "Kimlik Belgesi",
        description = "T.C. Kimlik, Pasaport, Ehliyet",
        titleRes = R.string.template_identity_title,
        descriptionRes = R.string.template_identity_desc,
        category = Category.PERSONAL
    ),
    ADDRESS(
        id = "address",
        title = "Adres & İletişim",
        description = "Ev, iş adresi ve teslimat bilgileri",
        titleRes = R.string.template_address_title,
        descriptionRes = R.string.template_address_desc,
        category = Category.PERSONAL
    ),
    SECURE_NOTE(
        id = "secure_note",
        title = "Güvenli Not",
        description = "Kurtarma kodları, Wi-Fi ve özel notlar",
        titleRes = R.string.template_note_title,
        descriptionRes = R.string.template_note_desc,
        category = Category.CUSTOM
    ),
    CUSTOM(
        id = "custom",
        title = "Özel Şablon",
        description = "Serbest alan tanımlı özel kayıt",
        titleRes = R.string.template_custom_title,
        descriptionRes = R.string.template_custom_desc,
        category = Category.CUSTOM
    );

    companion object {
        fun fromId(id: String?): TemplateType {
            return entries.firstOrNull { it.id == id } ?: LOGIN
        }

        fun detect(category: String, fields: List<FieldItem>): TemplateType {
            val keys = fields.map { it.key.lowercase() }
            return when {
                keys.contains("card_number") || keys.contains("cvv") -> CARD
                keys.contains("iban") && !keys.contains("card_number") -> BANK_ACCOUNT
                keys.contains("password") || keys.contains("website") || keys.contains("username") -> LOGIN
                keys.contains("id_number") || keys.contains("tc_no") || keys.contains("serial_number") -> IDENTITY
                keys.contains("address") || keys.contains("city") || keys.contains("district") -> ADDRESS
                keys.contains("note_content") || category == Category.CUSTOM.id -> SECURE_NOTE
                category == Category.ACCOUNT.id -> LOGIN
                category == Category.FINANCIAL.id -> CARD
                category == Category.PERSONAL.id -> IDENTITY
                else -> CUSTOM
            }
        }
    }
}

data class ServicePreset(
    val name: String,
    val domain: String,
    val defaultUsernameHint: String = ""
)

object PopularServices {
    val all = listOf(
        ServicePreset("Google", "accounts.google.com", "ornek@gmail.com"),
        ServicePreset("Apple ID", "appleid.apple.com", "ornek@icloud.com"),
        ServicePreset("Microsoft", "login.live.com", "ornek@outlook.com"),
        ServicePreset("GitHub", "github.com", "kullaniciadi"),
        ServicePreset("Instagram", "instagram.com", "kullaniciadi"),
        ServicePreset("Twitter / X", "x.com", "kullaniciadi"),
        ServicePreset("Netflix", "netflix.com", "ornek@gmail.com"),
        ServicePreset("Spotify", "spotify.com", "ornek@gmail.com"),
        ServicePreset("Amazon", "amazon.com.tr", "ornek@gmail.com"),
        ServicePreset("LinkedIn", "linkedin.com", "ornek@email.com"),
        ServicePreset("Discord", "discord.com", "ornek@email.com"),
        ServicePreset("Steam", "store.steampowered.com", "hesap_adi")
    )
}

data class EntryTemplate(
    val name: String,
    val category: Category,
    val fields: List<FieldItem>
)

object Templates {
    val creditCard = EntryTemplate(
        name = "credit_card",
        category = Category.FINANCIAL,
        fields = listOf(
            FieldItem("card_holder", "", FieldType.TEXT),
            FieldItem("card_number", "", FieldType.CARD_NUMBER, isSensitive = true),
            FieldItem("expiry_date", "", FieldType.DATE, isSensitive = true),
            FieldItem("cvv", "", FieldType.NUMBER, isSensitive = true),
            FieldItem("bank_name", "", FieldType.TEXT)
        )
    )

    val bankAccount = EntryTemplate(
        name = "bank_account",
        category = Category.FINANCIAL,
        fields = listOf(
            FieldItem("bank_name", "", FieldType.TEXT),
            FieldItem("account_holder", "", FieldType.TEXT),
            FieldItem("iban", "", FieldType.IBAN, isSensitive = true),
            FieldItem("account_number", "", FieldType.NUMBER, isSensitive = true)
        )
    )

    val loginAccount = EntryTemplate(
        name = "login_account",
        category = Category.ACCOUNT,
        fields = listOf(
            FieldItem("website", "", FieldType.TEXT),
            FieldItem("username", "", FieldType.TEXT),
            FieldItem("password", "", FieldType.PASSWORD, isSensitive = true)
        )
    )

    val identityDoc = EntryTemplate(
        name = "identity",
        category = Category.PERSONAL,
        fields = listOf(
            FieldItem("id_number", "", FieldType.NUMBER, isSensitive = true),
            FieldItem("full_name", "", FieldType.TEXT),
            FieldItem("birth_date", "", FieldType.DATE, isSensitive = true),
            FieldItem("serial_number", "", FieldType.TEXT, isSensitive = true)
        )
    )

    val address = EntryTemplate(
        name = "address",
        category = Category.PERSONAL,
        fields = listOf(
            FieldItem("address", "", FieldType.TEXT),
            FieldItem("city", "", FieldType.TEXT),
            FieldItem("postal_code", "", FieldType.TEXT)
        )
    )

    val secureNote = EntryTemplate(
        name = "secure_note",
        category = Category.CUSTOM,
        fields = listOf(
            FieldItem("note_content", "", FieldType.MULTILINE)
        )
    )
}

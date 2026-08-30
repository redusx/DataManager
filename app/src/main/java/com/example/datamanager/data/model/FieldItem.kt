package com.example.datamanager.data.model

enum class FieldType {
    TEXT,
    PASSWORD,
    PHONE,
    EMAIL,
    DATE,
    NUMBER,
    CARD_NUMBER,
    IBAN
}

data class FieldItem(
    val key: String,
    val value: String,
    val type: FieldType = FieldType.TEXT,
    val isSensitive: Boolean = false
)

enum class Category(val id: String) {
    PERSONAL("personal"),
    FINANCIAL("financial"),
    ACCOUNT("account"),
    CUSTOM("custom");

    companion object {
        fun fromId(id: String): Category {
            return entries.firstOrNull { it.id == id } ?: CUSTOM
        }
    }
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
            FieldItem("expiry_date", "", FieldType.DATE),
            FieldItem("cvv", "", FieldType.NUMBER, isSensitive = true),
            FieldItem("iban", "", FieldType.IBAN)
        )
    )

    val bankAccount = EntryTemplate(
        name = "bank_account",
        category = Category.FINANCIAL,
        fields = listOf(
            FieldItem("bank_name", "", FieldType.TEXT),
            FieldItem("account_holder", "", FieldType.TEXT),
            FieldItem("iban", "", FieldType.IBAN),
            FieldItem("account_number", "", FieldType.NUMBER, isSensitive = true)
        )
    )

    val loginAccount = EntryTemplate(
        name = "login_account",
        category = Category.ACCOUNT,
        fields = listOf(
            FieldItem("website", "", FieldType.TEXT),
            FieldItem("username", "", FieldType.TEXT),
            FieldItem("email", "", FieldType.EMAIL),
            FieldItem("password", "", FieldType.PASSWORD, isSensitive = true)
        )
    )

    val personalInfo = EntryTemplate(
        name = "personal_info",
        category = Category.PERSONAL,
        fields = listOf(
            FieldItem("first_name", "", FieldType.TEXT),
            FieldItem("last_name", "", FieldType.TEXT),
            FieldItem("birth_date", "", FieldType.DATE),
            FieldItem("id_number", "", FieldType.NUMBER, isSensitive = true),
            FieldItem("phone", "", FieldType.PHONE),
            FieldItem("email", "", FieldType.EMAIL),
            FieldItem("address", "", FieldType.TEXT),
            FieldItem("postal_code", "", FieldType.NUMBER)
        )
    )

    fun all(): List<EntryTemplate> = listOf(personalInfo, creditCard, bankAccount, loginAccount)
}

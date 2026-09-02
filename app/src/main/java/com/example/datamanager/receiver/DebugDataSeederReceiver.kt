package com.example.datamanager.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.datamanager.data.model.Category
import com.example.datamanager.data.model.DataEntry
import com.example.datamanager.data.model.FieldItem
import com.example.datamanager.data.model.FieldType
import com.example.datamanager.data.repository.DataRepository
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DebugDataSeederReceiver : BroadcastReceiver() {

    @Inject
    lateinit var dataRepository: DataRepository

    private val gson = Gson()
    private val scope = CoroutineScope(Dispatchers.IO)

    companion object {
        private const val TAG = "DebugDataSeeder"
        const val ACTION_SEED_DATABASE = "com.example.datamanager.SEED_DATABASE"
        const val ACTION_SEED_DATA = "com.example.datamanager.SEED_DATA"
        const val ACTION_CLEAR_DATABASE = "com.example.datamanager.CLEAR_DATABASE"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        Log.d(TAG, "Received broadcast action: $action")

        val pendingResult = goAsync()

        scope.launch {
            try {
                when (action) {
                    ACTION_SEED_DATABASE, ACTION_SEED_DATA -> {
                        seedSampleData()
                        showToast(context, "✅ 10 adet örnek test kaydı başarıyla yüklendi!")
                        Log.i(TAG, "Sample data successfully seeded into database.")
                    }
                    ACTION_CLEAR_DATABASE -> {
                        dataRepository.deleteAllEntries()
                        showToast(context, "🗑️ Tüm test verileri temizlendi.")
                        Log.i(TAG, "All entries deleted from database.")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing broadcast action: $action", e)
                showToast(context, "❌ Hata: ${e.localizedMessage}")
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun seedSampleData() {
        val sampleEntries = listOf(
            // 1. Google Account (Login)
            DataEntry(
                category = Category.ACCOUNT.id,
                title = "Google Hesabı",
                fieldsJson = gson.toJson(
                    listOf(
                        FieldItem("username", "ahmet.yilmaz@gmail.com", FieldType.TEXT, false),
                        FieldItem("password", "G!8#kL9\$vP2@mQ", FieldType.PASSWORD, true),
                        FieldItem("website", "https://accounts.google.com", FieldType.TEXT, false),
                        FieldItem("note", "Kişisel ana Google hesabı ve 2FA yedek kodları", FieldType.MULTILINE, false)
                    )
                )
            ),

            // 2. GitHub Account (Login)
            DataEntry(
                category = Category.ACCOUNT.id,
                title = "GitHub",
                fieldsJson = gson.toJson(
                    listOf(
                        FieldItem("username", "ahmet-dev", FieldType.TEXT, false),
                        FieldItem("password", "ghp_Secr3tToken9988!XyZ", FieldType.PASSWORD, true),
                        FieldItem("website", "https://github.com", FieldType.TEXT, false),
                        FieldItem("note", "Open source projeler ve şirket reposu", FieldType.MULTILINE, false)
                    )
                )
            ),

            // 3. Netflix (Login)
            DataEntry(
                category = Category.ACCOUNT.id,
                title = "Netflix",
                fieldsJson = gson.toJson(
                    listOf(
                        FieldItem("username", "ahmet.yilmaz@gmail.com", FieldType.TEXT, false),
                        FieldItem("password", "N3tfl!x2026Premium#", FieldType.PASSWORD, true),
                        FieldItem("website", "https://netflix.com", FieldType.TEXT, false),
                        FieldItem("note", "4 Ekran UHD Aile Paketi", FieldType.MULTILINE, false)
                    )
                )
            ),

            // 4. Garanti Bonus Card (Credit Card - Visa)
            DataEntry(
                category = Category.FINANCIAL.id,
                title = "Garanti Bonus Platinum",
                fieldsJson = gson.toJson(
                    listOf(
                        FieldItem("card_number", "4543123456789010", FieldType.CARD_NUMBER, true),
                        FieldItem("cardholder_name", "AHMET YILMAZ", FieldType.TEXT, false),
                        FieldItem("expiry_date", "1228", FieldType.DATE, true),
                        FieldItem("cvv", "428", FieldType.NUMBER, true),
                        FieldItem("bank_name", "Garanti BBVA", FieldType.TEXT, false)
                    )
                )
            ),

            // 5. Yapı Kredi World Card (Credit Card - Mastercard)
            DataEntry(
                category = Category.FINANCIAL.id,
                title = "Yapı Kredi Worldcard",
                fieldsJson = gson.toJson(
                    listOf(
                        FieldItem("card_number", "5400987654321098", FieldType.CARD_NUMBER, true),
                        FieldItem("cardholder_name", "AHMET YILMAZ", FieldType.TEXT, false),
                        FieldItem("expiry_date", "0827", FieldType.DATE, true),
                        FieldItem("cvv", "753", FieldType.NUMBER, true),
                        FieldItem("bank_name", "Yapı Kredi", FieldType.TEXT, false)
                    )
                )
            ),

            // 6. Garanti BBVA Bank Account (IBAN)
            DataEntry(
                category = Category.FINANCIAL.id,
                title = "Garanti BBVA Maaş Hesabı",
                fieldsJson = gson.toJson(
                    listOf(
                        FieldItem("bank_name", "Garanti BBVA", FieldType.TEXT, false),
                        FieldItem("account_holder", "Ahmet Yılmaz", FieldType.TEXT, false),
                        FieldItem("iban", "TR120006200000012345678901", FieldType.IBAN, true),
                        FieldItem("account_number", "1234-5678901", FieldType.NUMBER, true)
                    )
                )
            ),

            // 7. Identity Document (T.C. Kimlik)
            DataEntry(
                category = Category.PERSONAL.id,
                title = "T.C. Kimlik Kartı",
                fieldsJson = gson.toJson(
                    listOf(
                        FieldItem("id_number", "12345678901", FieldType.NUMBER, true),
                        FieldItem("full_name", "Ahmet Yılmaz", FieldType.TEXT, false),
                        FieldItem("birth_date", "15.05.1992", FieldType.DATE, true),
                        FieldItem("serial_number", "A12B34567", FieldType.TEXT, true)
                    )
                )
            ),

            // 8. Home Address (Address)
            DataEntry(
                category = Category.PERSONAL.id,
                title = "Ev Adresi",
                fieldsJson = gson.toJson(
                    listOf(
                        FieldItem("address", "Bağdat Caddesi No: 124 Daire: 8", FieldType.MULTILINE, false),
                        FieldItem("neighborhood", "Fenerbahçe Mah.", FieldType.TEXT, false),
                        FieldItem("district", "Kadıköy", FieldType.TEXT, false),
                        FieldItem("city", "İstanbul", FieldType.TEXT, false),
                        FieldItem("postal_code", "34726", FieldType.NUMBER, false)
                    )
                )
            ),

            // 9. Work Address (Address)
            DataEntry(
                category = Category.PERSONAL.id,
                title = "Şirket / Ofis Adresi",
                fieldsJson = gson.toJson(
                    listOf(
                        FieldItem("address", "Büyükdere Cad. No: 195 Kule 2 Kat: 14", FieldType.MULTILINE, false),
                        FieldItem("neighborhood", "Levent Mah.", FieldType.TEXT, false),
                        FieldItem("district", "Beşiktaş", FieldType.TEXT, false),
                        FieldItem("city", "İstanbul", FieldType.TEXT, false),
                        FieldItem("postal_code", "34394", FieldType.NUMBER, false)
                    )
                )
            ),

            // 10. Secure Note (Wi-Fi & Server Keys)
            DataEntry(
                category = Category.CUSTOM.id,
                title = "Ev Wi-Fi & Sunucu Bilgileri",
                fieldsJson = gson.toJson(
                    listOf(
                        FieldItem(
                            "note_content",
                            "SSID: MyHome_5GHz_Ultra\nWi-Fi Şifresi: K9#mX2\$wQ8!vL4@\n\nAna Sunucu IP: 192.168.1.100\nSSH Port: 2222\nSSH User: root",
                            FieldType.MULTILINE,
                            true
                        )
                    )
                )
            )
        )

        for (entry in sampleEntries) {
            dataRepository.insertEntry(entry)
        }
    }

    private fun showToast(context: Context, message: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
}

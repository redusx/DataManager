package com.redusx.floatvault.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.redusx.floatvault.data.model.Category
import com.redusx.floatvault.data.model.DataEntry
import com.redusx.floatvault.data.model.FieldItem
import com.redusx.floatvault.data.model.FieldType
import com.redusx.floatvault.data.repository.DataRepository
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
        const val ACTION_SEED_DATABASE = "com.redusx.floatvault.SEED_DATABASE"
        const val ACTION_SEED_DATA = "com.redusx.floatvault.SEED_DATA"
        const val ACTION_CLEAR_DATABASE = "com.redusx.floatvault.CLEAR_DATABASE"
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
                        showToast(context, "✅ Her kategoriden 5'er adet (toplam 20) örnek kayıt yüklendi!")
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
            // ═══════════════════════════════════════════════════════════
            // CATEGORY: FINANCIAL (5 Entries: 3 Cards, 2 Bank Accounts)
            // ═══════════════════════════════════════════════════════════
            // 1. Garanti Bonus Card (Credit Card - Visa)
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

            // 2. Yapı Kredi World Card (Credit Card - Mastercard)
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

            // 3. İş Bankası Maximum (Credit Card - Visa)
            DataEntry(
                category = Category.FINANCIAL.id,
                title = "İş Bankası Maximum Kart",
                fieldsJson = gson.toJson(
                    listOf(
                        FieldItem("card_number", "4506341278905623", FieldType.CARD_NUMBER, true),
                        FieldItem("cardholder_name", "AHMET YILMAZ", FieldType.TEXT, false),
                        FieldItem("expiry_date", "0529", FieldType.DATE, true),
                        FieldItem("cvv", "194", FieldType.NUMBER, true),
                        FieldItem("bank_name", "Türkiye İş Bankası", FieldType.TEXT, false)
                    )
                )
            ),

            // 4. Garanti BBVA Bank Account (IBAN)
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

            // 5. Ziraat Bankası Bank Account (IBAN)
            DataEntry(
                category = Category.FINANCIAL.id,
                title = "Ziraat Bankası Birikim Hesabı",
                fieldsJson = gson.toJson(
                    listOf(
                        FieldItem("bank_name", "Ziraat Bankası", FieldType.TEXT, false),
                        FieldItem("account_holder", "Ahmet Yılmaz", FieldType.TEXT, false),
                        FieldItem("iban", "TR580001000123456789005001", FieldType.IBAN, true),
                        FieldItem("account_number", "5001-9876543", FieldType.NUMBER, true)
                    )
                )
            ),

            // ═══════════════════════════════════════════════════════════
            // CATEGORY: PERSONAL (5 Entries: 3 Identity, 2 Addresses)
            // ═══════════════════════════════════════════════════════════
            // 6. Identity Document (T.C. Kimlik)
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

            // 7. Passport
            DataEntry(
                category = Category.PERSONAL.id,
                title = "Bordo Umuma Mahsus Pasaport",
                fieldsJson = gson.toJson(
                    listOf(
                        FieldItem("passport_number", "U18492041", FieldType.TEXT, true),
                        FieldItem("full_name", "Ahmet Yılmaz", FieldType.TEXT, false),
                        FieldItem("expiry_date", "10.08.2032", FieldType.DATE, true),
                        FieldItem("issuing_country", "Türkiye Cumhuriyeti", FieldType.TEXT, false)
                    )
                )
            ),

            // 8. Driver License (Ehliyet)
            DataEntry(
                category = Category.PERSONAL.id,
                title = "Sürücü Belgesi (Ehliyet)",
                fieldsJson = gson.toJson(
                    listOf(
                        FieldItem("license_number", "982341", FieldType.NUMBER, true),
                        FieldItem("full_name", "Ahmet Yılmaz", FieldType.TEXT, false),
                        FieldItem("license_class", "B / A2", FieldType.TEXT, false),
                        FieldItem("valid_until", "24.11.2030", FieldType.DATE, true)
                    )
                )
            ),

            // 9. Home Address (Address)
            DataEntry(
                category = Category.PERSONAL.id,
                title = "Ev Adresi & İletişim",
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

            // 10. Work Address (Address)
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

            // ═══════════════════════════════════════════════════════════
            // CATEGORY: ACCOUNT (5 Entries: Google, GitHub, Netflix, Spotify, E-Devlet)
            // ═══════════════════════════════════════════════════════════
            // 11. Google Account
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

            // 12. GitHub Account
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

            // 13. Netflix Account
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

            // 14. Spotify Account
            DataEntry(
                category = Category.ACCOUNT.id,
                title = "Spotify Premium",
                fieldsJson = gson.toJson(
                    listOf(
                        FieldItem("username", "ahmet.yilmaz@gmail.com", FieldType.TEXT, false),
                        FieldItem("password", "Sp0t!fyMusic9988\$", FieldType.PASSWORD, true),
                        FieldItem("website", "https://spotify.com", FieldType.TEXT, false),
                        FieldItem("note", "Aile paketi ve favori podcastler", FieldType.MULTILINE, false)
                    )
                )
            ),

            // 15. E-Devlet Account
            DataEntry(
                category = Category.ACCOUNT.id,
                title = "e-Devlet Kapısı",
                fieldsJson = gson.toJson(
                    listOf(
                        FieldItem("username", "12345678901", FieldType.NUMBER, true),
                        FieldItem("password", "EDevlet#2026!Tr", FieldType.PASSWORD, true),
                        FieldItem("website", "https://giris.turkiye.gov.tr", FieldType.TEXT, false),
                        FieldItem("note", "Resmi devlet portali kimlik doğrulama", FieldType.MULTILINE, false)
                    )
                )
            ),

            // ═══════════════════════════════════════════════════════════
            // CATEGORY: CUSTOM (5 Entries: Wi-Fi, Safe, Crypto, Server, Insurance)
            // ═══════════════════════════════════════════════════════════
            // 16. Secure Note: Wi-Fi
            DataEntry(
                category = Category.CUSTOM.id,
                title = "Ev Wi-Fi & Fiber Modem",
                fieldsJson = gson.toJson(
                    listOf(
                        FieldItem(
                            "note_content",
                            "SSID: MyHome_5GHz_Ultra\nWi-Fi Şifresi: K9#mX2\$wQ8!vL4@\n\nModem Arayüz: 192.168.1.1\nAdmin Şifre: SuperUser_Admin2026!",
                            FieldType.MULTILINE,
                            true
                        )
                    )
                )
            ),

            // 17. Secure Note: Safe / Alarm
            DataEntry(
                category = Category.CUSTOM.id,
                title = "Çelik Kasa & Alarm Şifreleri",
                fieldsJson = gson.toJson(
                    listOf(
                        FieldItem(
                            "note_content",
                            "Ev Çelik Kasa Kodu: 849201#\nFiziksel Anahtar: Çalışma odası gizli bölme\n\nParadox Alarm Kodu: 2849\nİptal/Tehdit Kodu: 9482",
                            FieldType.MULTILINE,
                            true
                        )
                    )
                )
            ),

            // 18. Secure Note: Crypto Seed Phrase
            DataEntry(
                category = Category.CUSTOM.id,
                title = "Kripto Cüzdan Kurtarma Anahtarı",
                fieldsJson = gson.toJson(
                    listOf(
                        FieldItem(
                            "note_content",
                            "Ledger Nano X Ana Cüzdan (12 Kelime):\n1. abandon 2. ability 3. able 4. about 5. above 6. absent 7. absorb 8. abstract 9. absurd 10. abuse 11. access 12. accident\n\nPIN Kodu: 482910",
                            FieldType.MULTILINE,
                            true
                        )
                    )
                )
            ),

            // 19. Secure Note: SSH & API Keys
            DataEntry(
                category = Category.CUSTOM.id,
                title = "Sunucu SSH & API Anahtarları",
                fieldsJson = gson.toJson(
                    listOf(
                        FieldItem(
                            "note_content",
                            "Üretim Sunucusu (Prod Server):\nIP: 185.190.22.44\nSSH Port: 2222\nUser: deploy\nPassphrase: Sh!eldVault_Prod_2026\n\nOpenAI API Key: sk-proj-ab9842109841298412894129",
                            FieldType.MULTILINE,
                            true
                        )
                    )
                )
            ),

            // 20. Secure Note: Insurance & Policy
            DataEntry(
                category = Category.CUSTOM.id,
                title = "Önemli Sigorta & Sözleşme Notları",
                fieldsJson = gson.toJson(
                    listOf(
                        FieldItem(
                            "note_content",
                            "Allianz Özel Sağlık Sigortası:\nPoliçe No: POL-9923841-2026\nMüşteri No: 8841920\nAcil Çağrı: 0850 399 99 99\n\nAxa Kasko Poliçe No: 4829104-B",
                            FieldType.MULTILINE,
                            false
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

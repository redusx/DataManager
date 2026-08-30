# DataManager 🛡️

**DataManager**, kişisel ve hassas bilgilerinizi tamamen cihaz üzerinde donanım destekli şifreleme (**Android Keystore + SQLCipher**) ile saklayan, **PIN** ve **Biyometrik (Parmak İzi)** kimlik doğrulama ile koruyan ve uygulama üstü **Floating Action Button (Overlay)** ile formlara tek dokunuşla kopyalama imkanı sunan modern bir Android uygulamasıdır.

---

## ✨ Özellikler

- **🔒 Tamamen Çevrimdışı (Offline-First)**: Uygulama internet izni (`INTERNET`) içermez; verileriniz asla cihaz dışına çıkmaz.
- **🛡️ Çift Katmanlı Şifreleme**:
  - **Android Keystore (AES-256 GCM)**: Donanım destekli anahtar yönetimi.
  - **SQLCipher**: Veritabanı seviyesinde AES-256 tam şifreleme.
  - **PBKDF2 Hashing**: PIN kodu 100.000 iterasyon ve rastgele tuz (salt) ile hashlenerek saklanır.
- **👆 Parmak İzi ile Giriş**: Biyometrik sensör ile tek dokunuşla anında kilit açma.
- **🪟 Uygulama Üstü Hızlı Erişim Penceresi (Floating Window)**:
  - Diğer uygulamalarda veya web sitelerinde form doldururken ekran kenarında duran yüzen buton.
  - Tıklandığında doğrudan formun üzerinde açılan şık ve kompakt hızlı kopyalama penceresi.
  - Tek dokunuşla panoya kopyalama ve anında yapıştırma.
- **📋 Hazır Şablonlar & Dinamik Alanlar**:
  - Kişisel Bilgiler (Ad, Soyad, T.C. Kimlik, Telefon, E-posta, Adres vb.)
  - Kredi Kartı (Kart Sahibi, Kart No, SKT, CVV, IBAN)
  - Banka Hesabı (Banka Adı, IBAN, Hesap No)
  - Giriş Hesapları (Kullanıcı Adı, Şifre, Web Sitesi)
  - İstenilen sayıda özel alan ekleme ve hassas alan maskeleme.
- **🎨 Modern & Akıcı Tasarım**: Jetpack Compose, Material 3, dinamik animasyonlar ve koyu tema desteği.
- **🌐 Çoklu Dil Desteği**: Türkçe ve İngilizce tam dil desteği.

---

## 🏗️ Mimari & Teknolojiler

- **UI**: Jetpack Compose & Material 3
- **Mimari**: MVVM + Clean Architecture + Repository Pattern
- **Bağımlılık Enjeksiyonu**: Hilt / Dagger
- **Veritabanı**: Room Database + SQLCipher (Encrypted SQLite)
- **Güvenlik**: Android Keystore, EncryptedSharedPreferences, PBKDF2
- **Arka Plan Servisi**: Android Foreground Service + WindowManager Overlay
- **Asenkron**: Kotlin Coroutines & StateFlow

---

## 🚀 Kurulum & Çalıştırma

1. Projeyi klonlayın:
   ```bash
   git clone https://github.com/redusx/DataManager.git
   ```
2. Android Studio (Ladybug / Meerkat veya üzeri) ile projeyi açın.
3. Gradle senkronizasyonunu tamamlayın ve hedef cihazı seçerek çalıştırın.

---

## 📄 Lisans

Bu proje MIT lisansı altında lisanslanmıştır.

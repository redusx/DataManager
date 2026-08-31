# MyVault — Payment Card Template Specification

## 1. Mental Model & Purpose
The **Payment Card Template** represents physical debit and credit cards.
Users expect:
1. **Physical Card Mental Layout**: Card Number (spaced in 4s), side-by-side Expiration Date (`MM / YY`) + Security Code (`CVV`), and Cardholder Name.
2. **Instant Card Preview**: An elevated, compact visual representation reflecting the entered card number and details in real-time.
3. **Card Type Indicator**: Auto-detection of Visa (`4...`), Mastercard (`5...` / `2...`), Troy (`9792...`), Amex (`34...` / `37...`).

---

## 2. Visual Wireframe

```
┌────────────────────────────────────────────────────────┐
│  KART EKLE                                             │
│                                                        │
│  KART ÖNİZLEMESİ                                       │
│  ┌──────────────────────────────────────────────────┐  │
│  │ 💳 Garanti BBVA Bonus                     [VISA] │  │
│  │                                                  │  │
│  │  4242  4242  4242  4242                          │  │
│  │                                                  │  │
│  │  SON KULLANMA        CVV                         │  │
│  │  12 / 28             •••                         │  │
│  │                                                  │  │
│  │  KART SAHİBİ                                     │  │
│  │  AHMET YILMAZ                                    │  │
│  └──────────────────────────────────────────────────┘  │
│                                                        │
│  KART BİLGİLERİ                                        │
│  ┌──────────────────────────────────────────────────┐  │
│  │ Kart Başlığı / Takma Ad                          │  │
│  │ Garanti Bonus                                    │  │
│  └──────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────┐  │
│  │ Kart Numarası                             💳 ⧉  │  │
│  │ 4242 4242 4242 4242                              │  │
│  └──────────────────────────────────────────────────┘  │
│  ┌───────────────────────┐  ┌───────────────────────┐  │
│  │ Son Kullanma (AA/YY)  │  │ Güvenlik Kodu (CVV) 👁│  │
│  │ 12 / 28               │  │ •••                    │  │
│  └───────────────────────┘  └───────────────────────┘  │
│  ┌──────────────────────────────────────────────────┐  │
│  │ Kart Üzerindeki İsim                             │  │
│  │ AHMET YILMAZ                                     │  │
│  └──────────────────────────────────────────────────┘  │
│                                                        │
│  [ ＋ Ek Bilgiler Ekle (Banka, Kart Türü, Not) ]      │
└────────────────────────────────────────────────────────┘
```

---

## 3. Fields & Progressive Disclosure

### Essential Fields
- `title`: Card Nickname (e.g., "Maaş Kartım")
- `card_number`: 15-19 digits with automatic `4242 4242 4242 4242` formatting
- `expiry_date`: 4 digits formatted as `MM / YY`
- `cvv`: 3 or 4 digits, masked with quick reveal
- `card_holder`: Full name as written on the card (auto uppercase)

### Additional Fields
- `bank_name`: Bank name (e.g. "İş Bankası", "Garanti BBVA")
- `card_type`: Kredi Kartı / Banka Kartı (Debit)
- `notes`: ATM PIN reminder / Card notes

---

## 4. Viewing Representation (EntryDetail)
In `EntryDetailScreen`, payment cards render as a realistic, stylish obsidian payment card widget with individual 1-tap copy buttons on the Card Number, Expiration Date, CVV, and Cardholder Name.

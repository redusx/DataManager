# MyVault — Information Architecture & Data Model UX

## 1. Information Architecture Critique & Categorization Strategy

### The Flaw in 5-Category Proliferation
Traditional vaults often separate *"Identity"* from *"Personal Information"*. In mobile daily usage, this creates cognitive ambiguity:
* *Is a phone number or address "Personal Information" or "Identity"?*
* *Is an IBAN a "Payment Card" or "Bank Account"?*

### The Optimized 4-Category System for MyVault
To minimize cognitive friction and tap distance, MyVault consolidates data into **4 clear, mutually exclusive, user-centric categories**:

```
MyVault Information Architecture
├── 🔐 1. Accounts & Logins      (Websites, Apps, Streaming, Work Logins)
├── 💳 2. Payment & Banking       (Credit Cards, Debit Cards, IBANs, Accounts)
├── 👤 3. Identity & Personal     (National ID, Passport, Driver's License, Address, Contacts)
└── 📝 4. Secure Notes & Keys     (Recovery Codes, Wi-Fi Keys, Secret Snippets)
```

---

## 2. Category Specifications

### 🔐 Category 1: Accounts & Logins

* **Purpose**: Store service credentials, website accounts, and portal logins.
* **Fields & Characteristics**:

| Field Name | Data Type | Sensitive? | Masked by Default? | Quick-Copy Priority | Searchable? |
| :--- | :--- | :---: | :---: | :---: | :---: |
| **Title / Service** | Text (e.g., "GitHub", "Netflix") | ❌ No | ❌ No | ⚪ None | ✅ Yes (Primary) |
| **Service URL / App** | URL / Text (e.g., "github.com") | ❌ No | ❌ No | ⚪ None | ✅ Yes |
| **Username / Email** | Text / Email | ❌ No | ❌ No | 🟡 Secondary (Tap) | ✅ Yes (Primary) |
| **Password** | Secret Alphanumeric | 🚨 **YES** | 🛡️ **YES** (`••••••••••••`) | 🔴 **PRIMARY (1-Tap)** | ❌ No (Excluded) |
| **Notes / 2FA Hints** | Multiline Text | ⚠️ Context | ❌ No | ⚪ Tap | ✅ Yes |

---

### 💳 Category 2: Payment & Banking

* **Purpose**: Store credit/debit cards, IBANs, and banking credentials for fast checkout.
* **Fields & Characteristics**:

| Field Name | Data Type | Sensitive? | Masked by Default? | Quick-Copy Priority | Searchable? |
| :--- | :--- | :---: | :---: | :---: | :---: |
| **Card / Account Title** | Text (e.g., "Garanti Bonus", "İş Bankası IBAN")| ❌ No | ❌ No | ⚪ None | ✅ Yes (Primary) |
| **Bank Name** | Text (e.g., "Garanti BBVA") | ❌ No | ❌ No | ⚪ None | ✅ Yes |
| **Cardholder Name** | Text (e.g., "AHMET YILMAZ") | ❌ No | ❌ No | ⚪ Tap | ❌ No |
| **Card Number** | 16-19 Digits | 🚨 **YES** | 🛡️ **YES** (`•••• •••• •••• 4242`) | 🔴 **PRIMARY (1-Tap)** | ⚠️ Last 4 only |
| **Expiry Date** | MM/YY (e.g., "08/28") | ⚠️ Semi | ❌ No | 🟡 Secondary | ❌ No |
| **CVV / CVC** | 3-4 Digits | 🚨 **YES** | 🛡️ **YES** (`•••`) | 🟡 Secondary (Tap) | ❌ No (Excluded) |
| **IBAN** | 24-34 Characters | 🚨 **YES** | 🛡️ **YES** (`TR•• •••• •••• ••12 34`) | 🔴 **PRIMARY (for IBANs)** | ⚠️ Last 4 only |

---

### 👤 Category 3: Identity & Personal

* **Purpose**: Store official government identification numbers, addresses, and personal contact details for government and registration forms.
* **Fields & Characteristics**:

| Field Name | Data Type | Sensitive? | Masked by Default? | Quick-Copy Priority | Searchable? |
| :--- | :--- | :---: | :---: | :---: | :---: |
| **Title / Person** | Text (e.g., "Kendim - TC Kimlik", "Eşimin Pasaportu") | ❌ No | ❌ No | ⚪ None | ✅ Yes (Primary) |
| **Full Name** | Text (e.g., "Ahmet Yılmaz") | ❌ No | ❌ No | ⚪ Tap | ✅ Yes |
| **National ID (TC No)**| 11 Digits | 🚨 **YES** | 🛡️ **YES** (`123•••••89`) | 🔴 **PRIMARY (1-Tap)** | ⚠️ Last 3 only |
| **Passport / Serial No**| Alphanumeric | 🚨 **YES** | 🛡️ **YES** (`U••••123`) | 🟡 Secondary | ❌ No |
| **Phone Number** | Phone Format | ⚠️ Semi | ❌ No | 🟡 Secondary (Tap) | ✅ Yes |
| **Address** | Multiline Text | ❌ No | ❌ No | 🟡 Secondary (Tap) | ✅ Yes |
| **Birth Date** | Date (DD.MM.YYYY) | ⚠️ Semi | ❌ No | ⚪ Tap | ❌ No |

---

### 📝 Category 4: Secure Notes & Keys

* **Purpose**: Store recovery phrases, Wi-Fi master passwords, software licenses, and private text snippets.
* **Fields & Characteristics**:

| Field Name | Data Type | Sensitive? | Masked by Default? | Quick-Copy Priority | Searchable? |
| :--- | :--- | :---: | :---: | :---: | :---: |
| **Title** | Text (e.g., "Ev Wi-Fi", "Crypto Recovery Phrase") | ❌ No | ❌ No | ⚪ None | ✅ Yes (Primary) |
| **Content / Key** | Multiline Secret Text | 🚨 **YES** | 🛡️ User Choice (Preview masked) | 🔴 **PRIMARY (1-Tap)** | ❌ No (Excluded) |
| **Tags / Labels** | Text Tokens | ❌ No | ❌ No | ⚪ None | ✅ Yes |

---

## 3. Data Relationships & Grouping

```
DataEntry
├── id: Long (Unique auto-increment)
├── categoryId: Enum (ACCOUNTS, CARDS, IDENTITY, NOTES)
├── title: String (Indexed, Non-encrypted search anchor)
├── subtitle: String (e.g., Username, Last 4 digits, Issuer)
├── primarySecret: EncryptedString (Password, Card Number, TC No, Content)
├── secondaryFields: EncryptedJson / Map (CVV, Expiry, IBAN, Address, URL)
├── isFavorite: Boolean (Pins to top of list / overlay)
├── createdAt: Timestamp
└── updatedAt: Timestamp
```

---

## 4. Search Indexing Privacy Rule

* **Searchable Plaintext Fields**: `Title`, `Subtitle/Username`, `Bank/Service Name`, `Category`, `Tags`.
* **Strictly Non-Searchable Fields**: `Password`, `Full Card Number`, `CVV`, `Full National ID`.
* **Rationale**: Decrypting all secrets on every search keystroke causes severe CPU lag and battery drain while creating a memory vulnerability. Only indexed metadata is searched in-memory.

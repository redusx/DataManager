# MyVault — Screen Inventory & State Specifications

## 1. Complete Screen Inventory

MyVault consists of **5 Core Application Screens** plus **1 Specialized Overlay Window**:

1. `AuthLockScreen` (Master PIN / Biometric Entry & Initial Setup)
2. `VaultHomeScreen` (Primary Vault Canvas, Search & Category Filter)
3. `EntryDetailScreen` (Expanded Field Inspection, Individual Reveal & Copy)
4. `AddEditEntryScreen` (Form for Creating and Modifying Entries)
5. `SettingsScreen` (Security, Overlay Preferences & Vault Management)
6. `FloatingOverlayPanel` (System Overlay Search & 1-Tap Copy Window)

---

## 2. Detailed Screen Specifications

---

### 📱 Screen 1: `AuthLockScreen`

* **Purpose**: Guard the vault against unauthorized access via Biometrics or Master PIN; also acts as the First-Time Onboarding setup screen.
* **Entry Point**: App launch, returning from background after timeout, or manual lock tap.
* **Primary Action**: Enter 4-6 digit numeric PIN or authenticate via Biometric sensor.
* **Secondary Actions**: Toggle Biometric prompt, Backspace/Clear PIN.
* **Information Shown**:
  - MyVault Brand Mark & Shield Icon.
  - State message (`"PIN Giriniz"` / `"Yeni PIN Belirleyin"`).
  - 4-6 PIN Indicator Dots.
  - Numeric Keypad (1–9, Biometric icon, 0, Backspace icon).
* **Sensitive Information**: Master PIN (Never shown in plain text; represented solely as filled dots).
* **Navigation Out**:
  - On Successful Auth -> `VaultHomeScreen`
  - On Back Pressed -> Closes Application (`finish()`)
* **States**:
  - `Content`: Normal numeric keypad ready for input.
  - `Error`: PIN mismatch or verification failure -> Shake animation on dots + error message + red accent feedback.
  - `Loading`: Short decryption delay while opening database (masked with subtle indicator).
  - *`Empty` & `Locked`*: N/A (This screen *is* the lock state).

---

### 📱 Screen 2: `VaultHomeScreen`

* **Purpose**: Primary dashboard for instantly finding, filtering, and copying records with minimal taps.
* **Entry Point**: Successful authentication from `AuthLockScreen`.
* **Primary Action**: Tap Quick-Copy icon on any card OR type query into Search Bar.
* **Secondary Actions**:
  - Tap Category Chip (All, Logins, Cards, Identity, Notes).
  - Tap card body to open `EntryDetailScreen`.
  - Tap `+` FAB to open `AddEditEntryScreen`.
  - Tap "Yüzen Hızlı Erişimi Başlat" to launch overlay and minimize app.
  - Tap Lock icon in TopAppBar to manually lock vault.
  - Tap Settings icon in TopAppBar to navigate to `SettingsScreen`.
* **Information Shown**:
  - Search Bar + Clear button.
  - Quick-start Overlay Card.
  - Category Filter Chip row with count badges.
  - List of `EntryCard` items (Category Icon, Title, Masked Primary Secret / Username, Quick Copy button).
* **Sensitive Information**: Primary secrets (Passwords, Card numbers, TC numbers) displayed in masked form (`••••••••`).
* **Navigation Out**:
  - Tap Card -> `EntryDetailScreen`
  - Tap FAB -> `AddEditEntryScreen`
  - Tap Settings -> `SettingsScreen`
  - Tap Lock -> `AuthLockScreen`
* **States**:
  - `Loading`: Shimmer / spinner while reading encrypted records.
  - `Empty (No Vault Data)`: Friendly empty state illustration + `"Kasanız henüz boş"` + "İlk Kaydı Ekle" button.
  - `Empty (Search/Filter No Results)`: `"Aradığınız kriterde kayıt bulunamadı"` + "Aramayı Temizle" button.
  - `Content`: Populated scrollable list of categorized cards.
  - `Error`: Database decryption or read failure with recovery option.
  - `Locked`: Automatically covered when app loses focus (`FLAG_SECURE`).

---

### 📱 Screen 3: `EntryDetailScreen`

* **Purpose**: Inspect all fields of a specific record, reveal sensitive secrets on demand, copy individual secondary fields, and initiate edit or delete.
* **Entry Point**: Tapping an `EntryCard` on `VaultHomeScreen`.
* **Primary Action**: Copy specific field (Password, Username, CVV, Card Number, IBAN, National ID).
* **Secondary Actions**:
  - Toggle Reveal (`Eye` icon) on sensitive fields.
  - Tap "Düzenle" (Edit) action in TopAppBar.
  - Tap "Sil" (Delete) action with confirmation dialog.
  - Tap Back arrow to return to Home.
* **Information Shown**:
  - Category badge & Entry Title.
  - Grouped field cards (e.g., for Card: Cardholder, Number, Expiry, CVV, Bank).
  - Field labels, masked/unmasked values, Copy buttons, Reveal toggles.
  - Created & Last Updated timestamps.
* **Sensitive Information**: Passwords, Card Numbers, CVVs, National IDs, Secret Notes (Masked by default with manual reveal).
* **Navigation Out**:
  - Tap Back -> `VaultHomeScreen`
  - Tap Edit -> `AddEditEntryScreen` (with existing data pre-populated)
  - Confirm Delete -> Deletes entry and navigates back to `VaultHomeScreen`
* **States**:
  - `Loading`: Retrieving entry details.
  - `Content`: Full field-by-field breakdown.
  - `Error`: Entry not found or corrupted -> Error snackbar + auto-return to Home.
  - *`Empty` & `Locked`*: N/A (Parent navigates to lock if backgrounded).

---

### 📱 Screen 4: `AddEditEntryScreen`

* **Purpose**: Create a new record or modify an existing record with structured validation and password generation.
* **Entry Point**: Tapping `+` FAB on Home (Create mode) OR Tapping "Edit" on Detail (Edit mode).
* **Primary Action**: Tap "Kaydet" (Save) button in TopAppBar / bottom bar.
* **Secondary Actions**:
  - Select Category (Logins, Cards, Identity, Notes).
  - Generate Random Password (via built-in password generator sheet).
  - Add Custom Key-Value fields.
  - Tap "İptal" (Cancel) / Back (with unsaved changes confirmation if dirty).
* **Information Shown**:
  - Category Picker selector.
  - Dynamic input form fields matching the selected category.
  - Password generator quick-action button.
  - Form validation error hints.
* **Sensitive Information**: User-typed passwords, card numbers, and secret values in input fields (Maskable input fields).
* **Navigation Out**:
  - On Save -> Returns to `VaultHomeScreen` or `EntryDetailScreen` with success toast.
  - On Cancel -> Returns to previous screen.
* **States**:
  - `Content`: Active editable form.
  - `Error`: Validation errors inline on field (e.g., `"Kart numarası 16 haneli olmalıdır"`).
  - *`Loading`, `Empty`, `Locked`*: Form is always active in Content state.

---

### 📱 Screen 5: `SettingsScreen`

* **Purpose**: Configure master security settings, PIN, Biometric lock, Overlay behavior, and perform emergency data wipe.
* **Entry Point**: Tapping Settings icon on `VaultHomeScreen` TopAppBar.
* **Primary Action**: Toggle security or overlay options.
* **Secondary Actions**:
  - "PIN Değiştir" -> Opens Change PIN dialog with strict numeric validation.
  - "Biyometrik Kilit" -> Switch toggle (enables/disables Fingerprint/Face auth).
  - "Yüzen Buton (Sürekli Açık)" -> Switch toggle (Persistent vs. Session overlay mode).
  - "Tüm Verileri Sil" -> Red destructive button opening multi-step wipe confirmation.
  - "Hakkında & Gizlilik" -> Offline zero-telemetry guarantee info.
* **Information Shown**:
  - Security configuration options.
  - Overlay mode explanations.
  - App version & local database status.
* **Sensitive Information**: PIN inputs inside Change PIN dialog (Masked).
* **Navigation Out**:
  - Tap Back -> `VaultHomeScreen`
  - On Complete Data Wipe -> Resets database, clears keys, and navigates to `AuthLockScreen` for new setup.
* **States**:
  - `Content`: Standard settings list.
  - `Loading`: When executing database wipe / key reset.
  - `Error`: When old PIN is incorrect in Change PIN dialog.

---

### 🪟 Specialized Screen 6: `FloatingOverlayPanel`

* **Purpose**: Lightweight, floating overlay window rendered on top of third-party apps for zero-context-switch search and copying.
* **Entry Point**: Tapping the minimized floating shield bubble.
* **Primary Action**: Tap any record's field to copy to clipboard AND automatically collapse overlay back to bubble.
* **Secondary Actions**:
  - Real-time search query input.
  - Tap Category Chip filter.
  - Tap "Uygulamayı Aç" (Open Full App).
  - Tap "Küçült" (Minimize to Bubble) or tap outside the panel.
* **Information Shown**:
  - Compact Search Bar.
  - Mini Category Filter Chips.
  - Condensed list of entries (Title, Username / Masked Secret preview, 1-Tap Copy action).
* **Sensitive Information**: Passwords and secrets (Displayed masked; copied in plaintext to clipboard on tap).
* **Navigation Out**:
  - Tap Copy or Tap Outside -> Collapses to 48dp floating bubble on screen edge.
  - Tap "Uygulamayı Aç" -> Launches full MyVault activity.
* **States**:
  - `Bubble (Minimized)`: 48×48dp draggable icon on screen edge.
  - `Panel (Expanded Content)`: Compact floating card with search & list.
  - `Empty (Search No Results)`: `"Eşleşen kayıt yok"`.

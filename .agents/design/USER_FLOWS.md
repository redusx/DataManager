# MyVault — Core User Flows & Journeys

## FLOW A: Initial Setup & Vault Initialization

```mermaid
graph TD
    A[User Opens App First Time] --> B[Show Welcome & Security Intro]
    B --> C[Prompt: '4-6 Haneli PIN Belirleyin']
    C --> D[User Enters Master PIN on PinKeypad]
    D --> E[Prompt: 'PIN'i Tekrar Girerek Onaylayın']
    E --> F{PINs Match?}
    F -- No --> G[Show Error + Reset Dots + Re-try]
    G --> E
    F -- Yes --> H{Device has Biometrics?}
    H -- Yes --> I[Prompt: 'Parmak İzi ile Girişi Açmak İster misiniz?']
    I -- Yes --> J[Test Biometric & Enable]
    I -- No --> K[Vault Initialized]
    H -- No --> K
    J --> K
    K --> L[Navigate to VaultHomeScreen: Empty State]
    L --> M[Display 'Kasanız Boş' + Large 'İlk Kaydı Ekle' CTA]
```

---

## FLOW B: Adding a New Password / Account

```mermaid
graph TD
    A[VaultHomeScreen] --> B[User taps '+' FAB in bottom thumb zone]
    B --> C[Navigate to AddEditEntryScreen with Autofocus]
    C --> D[Select Category: 'Hesaplar / Şifreler']
    D --> E[Enter Title e.g., 'Spotify' & Username]
    E --> F{User has password or needs generator?}
    F -- Generate --> G[Tap 'Şifre Üret' icon next to Password field]
    G --> H[Open Password Generator BottomSheet]
    H --> I[Customize length/symbols & Tap 'Kullan']
    I --> J[Password inserted into form]
    F -- Manual --> J[Type password manually]
    J --> K[Tap 'Kaydet' in TopAppBar]
    K --> L[Encrypt & Save to Local SQLCipher Database]
    L --> M[Show 'Kayıt Başarıyla Eklendi' Haptic Toast]
    M --> N[Return to VaultHomeScreen with instant list update]
```

---

## FLOW C: Finding a Record & Copying (In-App Search)

```mermaid
graph TD
    A[VaultHomeScreen] --> B[User taps Search Bar at top of canvas]
    B --> C[User types query e.g., 'Garanti']
    C --> D[In-memory filter updates in <16ms on every keystroke]
    D --> E[Matching EntryCards rendered in viewport]
    E --> F{Direct copy or full view?}
    F -- Direct Quick Copy --> G[Tap Copy button directly on EntryCard]
    G --> H[Plaintext secret copied to Android Clipboard]
    H --> I[Haptic vibration + 'Şifre panoya kopyalandı' feedback]
    F -- Full Detail View --> J[Tap EntryCard body]
    J --> K[Open EntryDetailScreen showing all fields]
    K --> L[Tap Copy next to specific field (Username/Password/Notes)]
    L --> H
```

---

## FLOW D: Checkout / Card Usage Flow

```mermaid
graph TD
    A[VaultHomeScreen] --> B[User taps 'Kartlar' Category Filter Chip]
    B --> C[List instantly filters to Payment Cards only]
    C --> D[Tap target card e.g., 'İş Bankası Maximum']
    D --> E[EntryDetailScreen opens with masked card number]
    E --> F[Tap Copy next to Card Number]
    F --> G[Card number copied + Haptic vibration]
    G --> H[Tap Reveal 'Eye' icon on CVV to read 3-digit code]
    H --> I[CVV displayed for 30s auto-mask countdown]
    I --> J[User switches to shopping app and pastes credentials]
```

---

## FLOW E: Floating Overlay Usage (Zero-Context-Switch Flow)

```mermaid
graph TD
    A[User is filling login form in 3rd-party App e.g., Instagram] --> B[Tap MyVault Floating Bubble on screen edge]
    B --> C[Floating Search Panel expands on top of Instagram]
    C --> D[Type 'Insta' into Overlay Search Bar]
    D --> E[Matching 'Instagram' entry displayed in overlay list]
    E --> F[Tap 'Şifreyi Kopyala' button inside overlay card]
    F --> G[Secret copied to Clipboard + Haptic vibration]
    G --> H[Overlay immediately AUTO-COLLAPSES to Bubble]
    H --> I[User pastes password into Instagram input field]
    I --> J[Form submitted in <5 seconds without app switching]
```

---

## FLOW F: Return from Background & Re-Authentication

```mermaid
graph TD
    A[App placed in background by user] --> B[Background Lock Timer starts]
    B --> C[User re-opens MyVault from Recent Apps]
    C --> D{Timeout elapsed / Lock enabled?}
    D -- No --> E[Resume current screen immediately]
    D -- Yes --> F[Display AuthLockScreen covering all data (FLAG_SECURE active)]
    F --> G{Biometrics enabled & hardware available?}
    G -- Yes --> H[Auto-trigger BiometricPrompt]
    H -- Success --> I[Unlock Vault -> Navigate to VaultHomeScreen]
    H -- Cancel / Fail --> J[Fall back to PinKeypad on screen]
    G -- No --> J
    J --> K[User types 4-6 digit Master PIN]
    K -- Verified --> I
    K -- Wrong PIN --> L[Shake dots + Warning haptic + Retry]
    L --> J
```

---

## FLOW G: Editing & Destructive Deletion

```mermaid
graph TD
    A[EntryDetailScreen] --> B{Edit or Delete?}
    B -- Edit --> C[Tap 'Düzenle' in TopAppBar]
    C --> D[AddEditEntryScreen opens with fields pre-filled]
    D --> E[User modifies fields & taps 'Kaydet']
    E --> F[Database updated -> Return to Detail with updated values]
    B -- Delete --> G[Tap 'Sil' action in TopAppBar / bottom]
    G --> H[Open Red Destructive AlertDialog: 'Bu kaydı silmek istediğinize emin misiniz?']
    H -- İptal --> I[Dismiss dialog; remain on Detail]
    H -- Sil --> J[Delete record from SQLCipher database]
    J --> K[Show 'Kayıt Silindi' feedback]
    K --> L[Navigate back to VaultHomeScreen; entry removed from list]
```

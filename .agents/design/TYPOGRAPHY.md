# MyVault — Typography Architecture & Data Legibility

## 1. Typography Hierarchy & Material 3 Scale

MyVault uses a disciplined typography scale built directly on the **Material Design 3 Type System**. The scale ensures effortless readability, clear visual hierarchy, and full compatibility with Android Accessibility Dynamic Font Scaling (`sp`).

```
┌─────────────────────────────────────────────────────────────┐
│                    TYPOGRAPHY HIERARCHY                     │
│   Headline Medium (28sp/36sp) ──> Screen Headers (Kasa)     │
│   Title Medium (16sp/24sp) ────> Entry Card Titles (Netflix)│
│   Body Large Mono (16sp/24sp) ──> Masked Secrets (••••••••) │
│   Body Medium (14sp/20sp) ─────> Usernames, Form Inputs     │
│   Label Small (11sp/16sp) ─────> Category Badges, Timestamps│
└─────────────────────────────────────────────────────────────┘
```

---

## 2. Material 3 Type Token Mapping

| Material 3 Token | Size / Line Height | Font Weight | MyVault Implementation Location |
| :--- | :--- | :--- | :--- |
| **`headlineMedium`** | `28sp` / `36sp` | SemiBold (600) | TopAppBar Screen Headers (`"Kasa"`, `"Ayarlar"`, `"Yeni Kayıt"`). |
| **`headlineSmall`** | `24sp` / `32sp` | Medium (500) | PIN Keypad Display Header (`"PIN Giriniz"`), Section Dialog Headers. |
| **`titleLarge`** | `22sp` / `28sp` | SemiBold (600) | `EntryDetailScreen` Main Entry Title. |
| **`titleMedium`** | `16sp` / `24sp` | Medium (500) | `EntryCard` Title (`"Garanti Bonus"`, `"Spotify"`), BottomSheet Titles. |
| **`titleSmall`** | `14sp` / `20sp` | Medium (500) | Form Section Headers (`"Kişisel Bilgiler"`, `"Güvenlik"`). |
| **`bodyLarge` (Mono)**| `16sp` / `24sp` | Medium (500) | **Masked Secrets, Passwords, Card Numbers, IBAN, PIN Dots**. |
| **`bodyMedium`** | `14sp` / `20sp` | Regular (400) | Usernames, Form Input Text, General Content, Error Messages. |
| **`bodySmall`** | `12sp` / `16sp` | Regular (400) | Supporting form text, password strength warnings. |
| **`labelLarge`** | `14sp` / `20sp` | SemiBold (600) | Primary CTA Button Labels (`"Kaydet"`, `"İlk Kaydı Ekle"`). |
| **`labelMedium`** | `12sp` / `16sp` | Medium (500) | Category Filter Chip Text (`"Girişler"`, `"Kartlar"`). |
| **`labelSmall`** | `11sp` / `16sp` | Medium (500) | Category Badges, Created/Updated Timestamps, Keypad Secondary Hint. |

---

## 3. Data-Type Specific Typography Rules

### A. Passwords, PINs & Masked Bullets (`FontFamily.Monospace`)
* **Rule**: All passwords, PIN circles, and masked bullet strings (`••••••••`) must render in **Monospace with Tabular Figures (`tnum`)**.
* **Rationale**:
  - In standard proportional fonts, `1` is significantly narrower than `8`, and `•` is narrower than `W`. Toggling between masked and unmasked states causes the container width to jump jarringly (layout shift).
  - Monospace guarantees that every character and bullet occupies the exact same horizontal bounding box (e.g., 9.6sp per character).

### B. Credit Card Numbers & IBANs (`FontFamily.Monospace`)
* **Format**: Formatted in 4-digit grouped blocks: `4543 •••• •••• 1234` / `TR33 •••• •••• •••• •••• ••12 34`.
* **Style**: Monospace medium weight with generous letter spacing (`tracking = 0.5.sp`) for instant chunk-by-chunk human scanning during manual checkout entry.

### C. Usernames, Emails & URLs (`FontFamily.SansSerif`)
* **Format**: Standard clean sans-serif (Roboto / Inter).
* **Style**: Regular 400 weight, muted secondary color (`onSurfaceVariant`). Never monospaced.

---

## 4. Accessibility & Dynamic Scaling Rules

1. **Strict `sp` Unit Requirement**: Never specify `dp` or `px` for font sizing. All text must use `sp` to respect Android OS system font scaling (from 100% up to 200% accessibility zoom).
2. **Vertical Headroom (Line Height)**: All text containers must specify explicit `lineHeight` matching the M3 scale to prevent descenders (`g`, `y`, `p`, `j`) from being clipped when system font size is increased.
3. **No Decorative All-Caps**: Avoid forced uppercase on long titles or button labels, as all-caps reduces reading speed and causes screen readers to spell out words as acronyms.

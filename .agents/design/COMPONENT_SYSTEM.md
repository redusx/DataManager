# MyVault — Component Architecture & UI Pattern Specifications

## 1. List vs. Card Architectural Decision

We analyzed four presentation patterns for displaying vault credentials:

| Presentation Pattern | Visual Density | Tap Ergonomics | Multi-Field Utility | Verdict for MyVault |
| :--- | :--- | :--- | :--- | :--- |
| **A) Heavy Floating Cards** | Low (3–4 items/screen) | 🟢 Easy tap | 🟢 Ample space | ❌ **Rejected for main list**: Too much whitespace waste. |
| **B) Plain Flat List Rows** | High (8–10 items/screen) | 🟡 Cramped | 🔴 Hard to isolate 1-tap copy | ❌ **Rejected for main app**: Lacks visual grouping for cards vs logins. |
| **C) Tonal Elevated EntryCard (M3)**| **Balanced (6–7 items/screen)**| 🟢 **Ideal (48dp copy zone)** | 🟢 **Perfect Title + Masked field** | 🏆 **RECOMMENDED for Full App** |
| **D) Compact Dense List Row** | **Maximum (8–9 items/panel)** | 🟢 **Fast scanning** | 🟡 Single primary line | 🏆 **RECOMMENDED for Floating Overlay** |

### Definitive Policy:
* **In Full Application (`VaultHomeScreen`)**: Use **`EntryCard`** (12dp radius, `surfaceContainer` fill, 12dp vertical padding, 16dp horizontal margin). Provides distinct visual grouping with a dedicated right-aligned 48dp quick-copy button.
* **In Floating Overlay (`FloatingOverlayPanel`)**: Use **`CompactOverlayRow`** (8dp height compression, 8dp padding). Maximizes visible records in the constrained overlay window.
* **In Detail View (`EntryDetailScreen`)**: Use **`GroupedFieldContainer`** (Single surface card enclosing structured field rows with horizontal dividers).

---

## 2. Comprehensive Component Specifications

---

### Component 1: `VaultSearchBar`
* **Purpose**: Primary anchor for real-time in-memory vault filtering.
* **Anatomy**:
  - Container: 56dp height, `shape = Shape.large` (16dp), `surfaceContainerHigh` fill.
  - Leading Icon: 24dp Search Icon (`Icons.Default.Search`).
  - Text Input: `bodyLarge`, placeholder `"Kasada arayın..."`.
  - Trailing Icon: Clear `(X)` icon button (appears only when text is non-empty).
* **When to Use**: Anchored at the top of `VaultHomeScreen` and `FloatingOverlayPanel`.
* **When NOT to Use**: On sub-screens (`EntryDetailScreen`, `SettingsScreen`).
* **Interaction**: Instant in-memory filter execution on every keystroke (<16ms).
* **Accessibility**: `contentDescription = "Kasada arama yapın"`, `Clear` icon has `contentDescription = "Aramayı temizle"`.

---

### Component 2: `CategoryFilterChip`
* **Purpose**: Instant 1-tap filtering across the 4 core categories.
* **Anatomy**:
  - Height: 36dp, `shape = Shape.small` (8dp).
  - Selected State: `PrimaryContainer` background, `OnPrimaryContainer` text, `Primary` 1.5dp border.
  - Unselected State: `SurfaceContainer` background, `OnSurfaceVariant` text, subtle outline.
  - Leading Icon: 18dp category icon (🔐, 💳, 👤, 📝).
  - Trailing Badge: Small numeric count pill (e.g., `12`).
* **Ergonomics**: Horizontally scrollable row positioned in natural thumb zone beneath the search bar.

---

### Component 3: `EntryCard` (Full App)
* **Purpose**: Primary list element representing a saved credential or card.
* **Anatomy**:
  ```
  ┌─────────────────────────────────────────────────────────────┐
  │ [🔐]  Google Workspace                            [📋 Copy] │
  │       ahmet@company.com  •  ••••••••••••                    │
  └─────────────────────────────────────────────────────────────┘
  ```
  - Leading Badge: 36×36dp rounded container tinted with the category accent holding a 20dp icon.
  - Center Column:
    - Row 1: `titleMedium` Entry Title (`"Google Workspace"`).
    - Row 2: `bodySmall` Subtitle / Username (`"ahmet@company.com"`) + `bodySmall` Masked Secret (`"••••••••••••"`).
  - Trailing Action: Dedicated `CopyButton` (48×48dp touch container).
* **Touch Interaction**:
  - Tapping card body -> Navigates to `EntryDetailScreen`.
  - Tapping Copy button -> Copies secret directly to clipboard without opening detail screen.

---

### Component 4: `CopyButton` (Dedicated Quick-Action)
* **Purpose**: High-frequency, single-tap copy trigger.
* **Visual Styling**:
  - Container: 40×40dp visual pill inside a **48×48dp touch boundary**.
  - Background: `surfaceContainerHigh` (subtly elevated above the card).
  - Icon: 20dp `Icons.Outlined.ContentCopy`.
  - On Press Feedback: Scale down to `0.95f` + instantaneous haptic vibration.
  - On Successful Copy: Icon morphs briefly (<200ms) to `Icons.Default.Check` in `Success` green.

---

### Component 5: `SensitiveField` (Detail Screen Field Row)
* **Purpose**: Display, mask, reveal, and copy an individual data field.
* **Anatomy**:
  ```
  ┌─────────────────────────────────────────────────────────────┐
  │ ŞİFRE (PASSWORD)                                            │
  │ ••••••••••••                        [👁️ Reveal]  [📋 Copy]  │
  └─────────────────────────────────────────────────────────────┘
  ```
  - Label: `labelSmall` uppercase tracking (`"ŞİFRE"`, `"KART NUMARASI"`, `"CVV"`).
  - Value: `bodyLarge` Monospace tabular text (masked by default).
  - Action Controls:
    - `RevealButton`: 48dp touch target toggling `visibility` / `visibility_off`.
    - `CopyButton`: 48dp touch target copying plaintext to clipboard.
* **Auto-Remask**: Automatically toggles back to masked state after 30 seconds.

---

### Component 6: `PinKeypad`
* **Purpose**: Secure, thumb-friendly numeric entry for vault unlocking and PIN changes.
* **Anatomy**:
  - Top: 4–6 circular PIN state indicators (Empty ring vs Filled solid circle in `Primary`).
  - Keypad Grid: 3×4 matrix (Keys 1–9, Bottom-Left: Biometric trigger, Bottom-Center: `0`, Bottom-Right: Backspace `Delete`).
  - Key Dimensions: Minimum **64×64dp** circular touch area with `titleLarge` numeric labels.
* **Security & Interaction**:
  - Numeric only (No alpha keyboard).
  - Error State: Horizontal shake animation on PIN indicator dots + red accent flash on incorrect entry.

---

### Component 7: `OverlayPanel` & `OverlayBubble`
* **Purpose**: System-level floating access interface.
* **Bubble Anatomy**: 48×48dp circular shield with `Primary` tinted icon and 16dp elevation shadow. Snaps to side margins.
* **Panel Anatomy**:
  - Card: 92% screen width, 16dp radius, `surfaceContainerHighest` fill with high contrast border.
  - Top Row: Compact search bar + Minimize icon button.
  - Body: Scrollable list of `CompactOverlayRow` items.
  - Quick-copy triggers auto-collapse in <150ms.

---

## 3. Button Hierarchy & Styling Rules

```
┌─────────────────────────────────────────────────────────────┐
│                      BUTTON HIERARCHY                       │
│   1. Primary CTA: FilledButton (Primary + OnPrimary)        │
│   2. Secondary Action: OutlinedButton (Outline + Primary)   │
│   3. Quick-Copy Trigger: FilledTonalIconButton (Elevated)   │
│   4. Destructive Action: FilledButton (Error + OnError)     │
│   5. Text/Cancel Action: TextButton (OnSurfaceVariant)      │
└─────────────────────────────────────────────────────────────┘
```

1. **Primary Action ("Kaydet", "İlk Kaydı Ekle")**:
   - `ButtonDefaults.filledButtonColors(containerColor = MaterialTheme.colorScheme.primary)`.
   - Height: 48dp, `shape = Shape.small` (8dp), text `labelLarge`.
2. **Destructive Action ("Kaydı Sil", "Verileri Sıfırla")**:
   - `ButtonDefaults.filledButtonColors(containerColor = MaterialTheme.colorScheme.error)`.
   - Used exclusively inside confirmation dialogs.
3. **Quick-Copy Action**:
   - Styled as an elevated tonal icon button (`FilledTonalIconButton`) to clearly signify it is an instant mechanical tool, not a navigation link.

---

## 4. Form Design Standards (Add / Edit Screen)

1. **Text Field Style**: Standard **`OutlinedTextField`** with `Shape.small` (8dp) rounded corners.
2. **Supporting & Error Text**:
   - Helper text below the field in `bodySmall`.
   - On error: Outline transitions to `MaterialTheme.colorScheme.error` with error text replacing helper text.
3. **Integrated Password Generator Trigger**:
   - Trailing icon inside the Password input field: `Icons.Default.AutoAwesome` (Generate password).
   - Tapping immediately opens the Password Generator `ModalBottomSheet`.
4. **Focused State**: Focussed border color uses `MaterialTheme.colorScheme.primary` with 2dp stroke width.

---

## 5. Iconography Architecture (Material Symbols)

* **Style Family**: **Material Symbols Rounded** (provides soft, approachable edges without feeling cartoonish).
* **Standard Viewport**: 24×24dp icon vector size centered inside a **48×48dp touch container**.
* **Key Icons Standard**:

| Functional Concept | Material Symbol | `contentDescription` Requirement |
| :--- | :--- | :--- |
| **Search** | `Icons.Rounded.Search` | `"Kasada ara"` |
| **Clear Search** | `Icons.Rounded.Close` | `"Aramayı temizle"` |
| **Copy to Clipboard** | `Icons.Rounded.ContentCopy` | `"[Alan Adı] panoya kopyala"` |
| **Copied Success** | `Icons.Rounded.Check` | `"Kopyalandı"` |
| **Reveal Secret** | `Icons.Rounded.Visibility` | `"Şifreyi göster"` |
| **Hide Secret** | `Icons.Rounded.VisibilityOff` | `"Şifreyi gizle"` |
| **Lock Vault** | `Icons.Rounded.Lock` | `"Kasayı kilitle"` |
| **Settings** | `Icons.Rounded.Settings` | `"Ayarlar"` |
| **Add Entry** | `Icons.Rounded.Add` | `"Yeni kayıt ekle"` |
| **Edit Entry** | `Icons.Rounded.Edit` | `"Kaydı düzenle"` |
| **Delete Entry** | `Icons.Rounded.Delete` | `"Kaydı sil"` |
| **Biometric Key** | `Icons.Rounded.Fingerprint` | `"Biyometrik doğrulama ile aç"` |
| **Overlay Bubble** | `Icons.Rounded.Security` | `"MyVault hızlı erişim"` |

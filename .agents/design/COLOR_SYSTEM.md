# MyVault — Semantic Color Architecture & Tonal System

## 1. Color System Philosophy & UX Meaning

MyVault's color palette is engineered for **calm authority, maximum contrast (WCAG AA ≥ 4.5:1), and zero visual fatigue**. 

Instead of arbitrary decorative hex codes, MyVault strictly binds every element to a **Semantic Role** in accordance with Material Design 3.

```
┌─────────────────────────────────────────────────────────────┐
│                      SURFACE ELEVATION                      │
│   Background (Base canvas)                                  │
│   └── Surface Container Low (Search bar background)         │
│       └── Surface Container (Default EntryCard background)  │
│           └── Surface Container High (Modal sheets, dialogs)│
│               └── Surface Container Highest (Overlay panel) │
└─────────────────────────────────────────────────────────────┘
```

---

## 2. Semantic Color Roles & Intent

| Semantic Token | UX Role & Meaning | Applied Components in MyVault |
| :--- | :--- | :--- |
| **`Primary`** | Brand anchor & highest-priority action. Signals *"This is the main action to advance."* | `+ FAB`, Primary "Kaydet" buttons, Keypad confirm. |
| **`On Primary`** | High-contrast text/icon on top of `Primary`. | Text/Icons inside Primary Buttons. |
| **`Primary Container`** | Subdued brand fill for selected/active states. | Active Category Filter Chip fill, Focused Search Border. |
| **`On Primary Container`** | Text/Icon on top of `Primary Container`. | Active Filter Chip text and icon. |
| **`Secondary`** | Utility actions & functional chrome. | Quick-Copy button accent, Secondary chips, Lock icons. |
| **`Surface`** | Base canvas and foundational screen background. | App background behind scrollable lists. |
| **`Surface Container`** | Primary card fill. Creates subtle contrast above `Surface`. | `EntryCard` background, Settings group containers. |
| **`Surface Container High`**| Raised surface for temporary contextual windows. | `ModalBottomSheet`, `AlertDialog`, Dialog sheets. |
| **`Surface Container Highest`**| Highest-elevation surface. Pops above all third-party apps. | `FloatingOverlayPanel` floating search card. |
| **`Outline / Variant`** | Subtle structural boundary without heavy borders. | Inactive text field strokes, subtle card separators. |
| **`Error / Container`** | Critical warning, destructive action, auth failure. | "Sil" (Delete) button, PIN mismatch shake, Error text. |
| **`Success / Container`** | Positive affirmation & completed transactions. | Copy confirmation pill, database backup success. |
| **`Warning`** | Non-destructive caution (e.g., weak password). | Password strength indicator (medium). |

---

## 3. Light vs. Dark Theme Tonal Mapping

### 🌙 Dark Theme (Default Vault Mode)
Dark mode is the canonical aesthetic for MyVault to minimize glare, protect OLED battery life, and provide subtle contrast in low-light environments.

* **Background / Base Canvas**: Deep Obsidian / Charcoal (`#121316` tonal baseline).
* **Surface Container (Cards)**: Elevated Slate (`#1A1C20` tonal step).
* **Surface Container High (Dialogs/Sheets)**: Slate Gray (`#22252A`).
* **Primary Accent**: Crisp Cobalt / Tech Sapphire (`#7FA8FF` / `#ADC6FF` — optimized for dark contrast).
* **On Surface (Primary Text)**: Off-white (`#E2E2E6` — 90% opacity, eliminates harsh pure white glare).
* **On Surface Variant (Secondary Text)**: Muted Silver (`#C4C6D0` — 70% opacity for usernames/dates).
* **Success Accent**: Crisp Mint / Emerald (`#81D498` — for copy feedback).
* **Error Accent**: Clear Crimson / Coral (`#FF897D` — for delete/auth errors).

---

### ☀️ Light Theme (High-Sunlight Mode)
For users operating outdoors in bright ambient sunlight, Light Mode ensures razor-sharp text visibility.

* **Background / Base Canvas**: Pure Crisp Alabaster (`#FBF8FD` / `#F8F9FC`).
* **Surface Container (Cards)**: Pure Crisp White with subtle outline (`#FFFFFF`).
* **Surface Container High (Dialogs/Sheets)**: Soft Light Gray (`#F0F2F6`).
* **Primary Accent**: Deep Royal Cobalt (`#2B5EAA` / `#1E4E98`).
* **On Surface (Primary Text)**: Deep Charcoal Black (`#191C1E` — 14:1 contrast ratio).
* **On Surface Variant (Secondary Text)**: Slate Gray (`#44474E` — 7:1 contrast ratio).
* **Success Accent**: Deep Emerald (`#1B6D38`).
* **Error Accent**: Deep Crimson (`#BA1A1A`).

---

## 4. Category Accent Tints (Restrained Semantic Palette)

To distinguish categories at a glance without turning the interface into a rainbow, each category uses a single, subdued icon tint:

| Category | Semantic Tint Role | Dark Mode Tint | Light Mode Tint | UX Association |
| :--- | :--- | :--- | :--- | :--- |
| **🔐 Accounts & Logins** | Brand Blue / Sapphire | Crisp Azure | Royal Blue | Security & Authentication |
| **💳 Payment & Cards** | Emerald / Jade | Soft Mint | Forest Green | Currency & Transactions |
| **👤 Identity & Personal** | Amber / Topaz | Warm Gold | Ochre Bronze | Official Credentials & IDs |
| **📝 Secure Notes & Keys**| Amethyst / Lavender | Soft Violet | Deep Purple | Secrets & Custom Snippets |

*Rule: Category tints are applied ONLY to the 20dp category icon badge and the active filter chip border. Card bodies and text remain strictly neutral.*

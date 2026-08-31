# MyVault — Sensitive Data Visual Language & State Specifications

## 1. Visual Representation by Sensitive Data Type

Different sensitive fields require distinct masking patterns to allow instant human identification without exposing secret contents.

```
┌─────────────────────────────────────────────────────────────┐
│                 SENSITIVE DATA VISUAL PATTERNS              │
│   Password      ──> •••••••••••• (Uniform 12-dot sequence)  │
│   Credit Card   ──> •••• •••• •••• 4242 (Last 4 exposed)    │
│   CVV / CVC     ──> ••• (Exact 3-dot bullet sequence)       │
│   National ID   ──> 123•••••89 (First 3 & Last 2 exposed)   │
│   IBAN          ──> TR33 •••• •••• •••• •••• ••12 34        │
│   PIN Indicator ──> ○ ○ ○ ○ (Empty) ──> ● ● ● ● (Filled)    │
└─────────────────────────────────────────────────────────────┘
```

---

## 2. Masking Character & Formatting Specifications

| Field Category | Masked Visual Pattern | Revealed Visual Pattern | Typography Engine |
| :--- | :--- | :--- | :--- |
| **Password** | `••••••••••••` *(12 fixed bullets)* | `K#9m$vL!2xQz` | `FontFamily.Monospace`, `bodyLarge` |
| **Payment Card** | `•••• •••• •••• 4242` | `5421 8901 2345 4242` | `FontFamily.Monospace`, `bodyLarge` |
| **CVV Code** | `•••` *(3 bullets)* | `842` | `FontFamily.Monospace`, `bodyLarge` |
| **National ID (TC)**| `123•••••89` | `12345678989` | `FontFamily.Monospace`, `bodyLarge` |
| **IBAN** | `TR33 •••• •••• •••• •••• ••12 34` | `TR33 0006 2000 0001 2000 1234 56` | `FontFamily.Monospace`, `bodyLarge` |
| **Master PIN Input**| `○ ○ ○ ○` *(Empty circles)* | `● ● ● ●` *(Filled `Primary` dots)*| Custom Compose Canvas Dots |

### Masking Bullet Standard (`•` / `\u2022`)
* **Standard Bullet Glyph**: Unicode `U+2022` (Bullet) rendered in `FontFamily.Monospace`.
* **Bullet Sizing**: Scaled to 16sp with vertical centering to prevent baseline sagging.
* **Layout Invariance**: Because the bullet is monospaced, revealing the text replaces 12 bullets with 12 characters, producing **0px of horizontal layout shift**.

---

## 3. The 6 Visual States of a Sensitive Field

```
┌─────────────────────────────────────────────────────────────┐
│ 1. MASKED STATE (Default)                                   │
│    ••••••••••••                       [ 👁️ ]   [ 📋 ]       │
├─────────────────────────────────────────────────────────────┤
│ 2. REVEALED STATE (Active countdown: 30s)                   │
│    p@ssW0rd!2026                      [ 👁️ ]   [ 📋 ]       │
├─────────────────────────────────────────────────────────────┤
│ 3. COPY PRESSED STATE (Immediate touch feedback)            │
│    ••••••••••••                       [ 👁️ ]   [ 📋 0.95x ] │
├─────────────────────────────────────────────────────────────┤
│ 4. COPIED CONFIRMATION STATE (<200ms morph)                 │
│    ••••••••••••                       [ 👁️ ]   [ ✅ Green ] │
├─────────────────────────────────────────────────────────────┤
│ 5. DISABLED / LOCKED STATE (38% Opacity)                    │
│    ••••••••••••                       [ 🔒 Kilitli ]        │
├─────────────────────────────────────────────────────────────┤
│ 6. ERROR / RE-AUTH REQUIRED STATE (Red Accent)              │
│    Kasa Kilitlendi. Yeniden Doğrulayın. [ 🔑 PIN Girin ]    │
└─────────────────────────────────────────────────────────────┘
```

---

## 4. Visual State Behavior Details

### A. State 1: Masked State (Default)
* Text color: `onSurface` at 90% opacity.
* Container: `surfaceContainer` fill with subtle 1dp border.
* Icons: `visibility` (Eye open) and `content_copy`.

### B. State 2: Revealed State (30-Second Active Timer)
* Text color: `primary` or high-contrast `onSurface`.
* Icons: `visibility_off` (Eye closed).
* Active Indicator: Subtle animated border pulse or countdown hint indicating the 30-second auto-mask timer is ticking.

### C. State 3: Copy Pressed State
* Copy button scales down to `0.95f` using physics spring.
* Haptic feedback engine triggers `HapticFeedbackType.LongPress`.

### D. State 4: Copied Confirmation State
* Copy button icon transitions from `ContentCopy` -> `Check` in `Success` mint green for 1200ms before smoothly fading back to `ContentCopy`.
* Transient Pill Notification emerges at bottom of viewport: `"[Alan Adı] kopyalandı"`.

### E. State 5 & 6: App Backgrounded / Locked State (`FLAG_SECURE`)
* The entire activity surface is shielded from Android OS task switcher thumbnails.
* When returning to the app, all unmasked secrets are forcefully returned to State 1 (Masked).

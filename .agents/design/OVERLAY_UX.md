# MyVault — Floating Overlay Interaction Specification (`OverlayService`)

## 1. Role & UX Purpose of the Floating Overlay

The Floating Overlay is MyVault’s **flagship differentiator**. It solves the mobile task-switching penalty by projecting a miniature, highly focused credential search and copying interface directly over third-party applications (browsers, banking apps, shopping apps, delivery checkouts).

---

## 2. Dual-State Architecture (Bubble vs. Panel)

```
┌────────────────────────────────────────────────────────┐
│                   STATE 1: BUBBLE                      │
│   • Compact 48×48dp circular shield icon               │
│   • Draggable along left/right screen edges            │
│   • Minimal footprint (<3% screen area)                │
└───────────────────────────┬────────────────────────────┘
                            │ (Single Tap)
                            ▼
┌────────────────────────────────────────────────────────┐
│                   STATE 2: PANEL                       │
│   • Max 92% screen width, elevated rounded surface     │
│   • Real-time search bar + Mini category chips         │
│   • Compact list of matching entries                   │
│   • 1-Tap Copy on any record                           │
└───────────────────────────┬────────────────────────────┘
                            │ (Copy Executed OR Outside Touch)
                            ▼
                    (Auto-Minimize to Bubble)
```

---

## 3. Detailed Overlay Specifications

### A. Dimensions & Visual Footprint
* **Minimized Bubble**:
  - Size: **48×48dp** (meets accessibility touch target while remaining unobtrusive).
  - Shape: Rounded circular shield with 16dp elevation shadow.
  - Position: Snaps to left or right margin with slight inset (20px).
* **Expanded Search Panel**:
  - Width: **92% of device screen width** (capped at 560dp on foldables/tablets).
  - Height: Auto `WRAP_CONTENT` (capped at max 60% viewport height to keep underlying form partially visible).
  - Gravity: Centered in upper-middle viewport above software keyboard.

### B. Search & Filtering in Overlay
* **Instant In-Memory Filter**: Operates on decrypted records in RAM; zero network/disk lag (<16ms).
* **Search Anchor**: Focused search text field with immediate software keyboard invocation.
* **Mini Category Chips**: Horizontal row (`Tümü`, `Girişler`, `Kartlar`, `Kimlik`, `Notlar`) for 1-tap narrowing.

### C. Entry Representation & Field Selection
* Each row in the overlay list displays:
  - **Category Icon** (e.g., 💳 Card, 🔐 Login).
  - **Title** (e.g., `"Garanti Bonus"` / `"Amazon"`).
  - **Secondary Hint** (e.g., `"•••• 4242"` / `"ahmet@gmail.com"`).
  - **1-Tap Primary Copy Button**: Large copy icon button for the primary secret (Password / Card Number / TC No).
  - **Field Selector (Expandable sub-row)**: Tapping the row expands quick-copy chips for secondary fields (e.g., `[Kullanıcı Adı]`, `[Şifre]`, `[Son Kul.]`, `[CVV]`).

---

## 4. Post-Copy Behavior & Auto-Minimize Rules

1. **One-Tap Copy Action**:
   - User taps the copy button for a specific field.
   - Field value is copied to Android `ClipboardManager`.
   - Crisp haptic feedback is triggered.
   - Toast/Pill notification: `"Kopyalandı"`.
2. **Immediate Auto-Collapse**:
   - The overlay **immediately collapses back to the 48dp Bubble** in <150ms.
   - Focus is automatically restored to the underlying app's form input field.
   - User can immediately paste without having to manually close the overlay.
3. **Outside Touch Interception**:
   - Tapping anywhere outside the floating card (intercepted via `ACTION_OUTSIDE`) automatically collapses the panel back to the bubble without triggering unwanted clicks in the underlying app.

---

## 5. Security & Privacy Safeguards in Overlay

* **Never Show Plaintext Secrets**: Passwords and CVVs in the overlay list are **never** rendered in plain text; they are displayed as `••••••••` dots. Tapping copies the unmasked plaintext value directly to the clipboard.
* **Exclusion of Administrative Actions**:
  - Overlay does **NOT** allow changing master PIN, deleting vault data, or editing security settings.
  - Tapping "Uygulamayı Aç" (Open Full App) requires full biometric/PIN authentication if vault is locked.
* **Sensitive Clipboard Flag**:
  - All copied secrets are marked with Android 13+ `EXTRA_IS_SENSITIVE` to suppress system clipboard visual previews.

---

## 6. Operating Modes: Persistent vs. Session-Based

MyVault implements a clear user choice between two execution models:

| Mode | Trigger | Behavior on App Close | Best For |
| :--- | :--- | :--- | :--- |
| **Session Mode (Default)** | Started via Home Screen card *"Yüzen Hızlı Erişimi Başlat"* (Settings toggle OFF). | Active while MyVault is running in background. **Terminates automatically when MyVault is swiped away from recent tasks (`onTaskRemoved`).** | Users who only want the overlay during a temporary browsing or shopping session. |
| **Persistent Mode** | Turned ON in `SettingsScreen` (*"Yüzen Buton (Sürekli Açık)"*). | Service runs with `START_STICKY`. **Stays active on screen even after MyVault is swiped away from recent tasks.** Auto-starts on app launch. | Power users who want 24/7 instant floating access to their vault across all apps. |

---

## 7. Recommended Overlay Interaction Summary

1. **Minimized State**: 48dp bubble pinned to side margin.
2. **Expand**: Single tap opens 92% centered search panel.
3. **Find**: Type query or tap category chip.
4. **Copy**: Single tap on primary button copies secret.
5. **Dismiss**: Auto-collapses to bubble on copy OR on tap outside.
6. **Paste**: User pastes into target app with zero extra navigation.

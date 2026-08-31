# MyVault — Sensitive Data UX & Masking Interaction Model

## 1. Sensitive Data Matrix

This specification establishes the explicit UI/UX display, reveal, copying, and masking rules for all data fields managed by MyVault.

| Field Type | Default Display State | Reveal Interaction | Copy Interaction | Auto-Hide / Auto-Mask | Confirmation Required? |
| :--- | :--- | :--- | :--- | :--- | :---: |
| **Password** | 🛡️ Masked (`••••••••••••`) | 👁️ Tap Eye icon to unmask into monospace text | 📋 1-Tap Copy button | ⏱️ Auto-re-masks after **30s** | ❌ No |
| **Card Number** | 🛡️ Last 4 visible (`•••• •••• •••• 4242`) | 👁️ Tap Eye icon to show full 16 digits | 📋 1-Tap Copy button | ⏱️ Auto-re-masks after **30s** | ❌ No |
| **CVV / CVC** | 🛡️ Completely masked (`•••`) | 👁️ Tap Eye icon to reveal 3 digits | 📋 Tap Copy icon | ⏱️ Auto-re-masks after **30s** | ❌ No |
| **Expiration Date** | 👁️ Plaintext (`MM/YY`, e.g., "08/28") | N/A (Always visible) | 📋 Tap Copy icon | N/A | ❌ No |
| **Identity Number (TC)** | 🛡️ Partial mask (`123•••••89`) | 👁️ Tap Eye icon to reveal full 11 digits | 📋 1-Tap Copy button | ⏱️ Auto-re-masks after **30s** | ❌ No |
| **Phone Number** | 👁️ Plaintext (`+90 5XX XXX XX XX`) | N/A (Always visible) | 📋 Tap Copy icon | N/A | ❌ No |
| **Email** | 👁️ Plaintext (`user@domain.com`) | N/A (Always visible) | 📋 Tap Copy icon | N/A | ❌ No |
| **Username** | 👁️ Plaintext (`ahmet_yilmaz`) | N/A (Always visible) | 📋 Tap Copy icon | N/A | ❌ No |
| **Secure Notes** | 🛡️ Truncated Preview (2 lines) | 👁️ Tap to expand / reveal full note | 📋 1-Tap Copy button | N/A | ❌ No |
| **Master PIN** | 🛡️ Never plaintext (`••••`) | ❌ Cannot be revealed | ❌ Cannot be copied | N/A | ❌ No |

---

## 2. Reveal & Masking UX Rules

1. **Monospace Tabular Alignment (`tabular-nums`)**:
   - Masked dot characters (`•`) and revealed alphanumeric characters must share identical character metrics to eliminate layout jitter and visual shifting when toggled.
2. **Dedicated Eye Toggle**:
   - Positioned on the right side of the field value container with minimum **48×48dp** touch target.
   - States: `visibility` (eye open) vs `visibility_off` (eye closed).
3. **Auto-Re-Mask Timer (30 Seconds)**:
   - When a user reveals a secret (Password, CVV, Card Number, TC No), a 30-second background coroutine timer begins.
   - If no interaction occurs within 30 seconds, the field automatically collapses back to its masked state (`••••`).
   - If the user backgrounds the app or locks the vault, all revealed fields instantly reset to masked state.
4. **App Thumbnail Shielding (`FLAG_SECURE`)**:
   - The entire Android window enforces `WindowManager.LayoutParams.FLAG_SECURE` while sensitive data is active, preventing Android OS from taking clear screenshot thumbnails in the Recent Apps multitasking tray.

---

## 3. Copy Interaction & Clipboard Privacy

1. **Immediate Execution**: Tapping the copy button copies the unmasked plaintext secret directly into the Android system clipboard without requiring the user to reveal it first.
2. **Haptic Acknowledgment**: Instant tactile haptic feedback (`HapticFeedbackType.LongPress` / `CONFIRM`).
3. **Pill Notification**: Brief, non-intrusive notification: `"[Field Name] panoya kopyalandı"` (`"Password copied to clipboard"`).
4. **Clipboard Security Flag**:
   - Marks clipboard payload with `ClipDescription.EXTRA_IS_SENSITIVE = true` on Android 13+ (API 33+), preventing Android's system clipboard overlay from flashing plain text passwords on screen.

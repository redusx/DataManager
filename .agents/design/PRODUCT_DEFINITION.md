# MyVault — Product Definition & Core UX Strategy

## 1. One-Sentence Product Definition

> **MyVault is an offline-first, zero-knowledge Android personal data vault that eliminates repetitive typing across mobile apps through an instant-access floating overlay and single-tap credential copying.**

---

## 2. Target User & Persona

### Primary Persona: "The Mobile Power Utility User"
* **Demographics**: Active smartphone users handling dozens of daily transactions (banking, shopping, government portals/e-Devlet, travel, subscriptions).
* **Context of Use**: Multitasking on mobile, switching rapidly between banking apps, web checkouts, utility logins, and forms on crowded commutes or busy work environments.
* **Key Frustrations**:
  - Re-typing 11-digit national identity numbers, 26-character IBANs, and 16-digit credit card numbers manually.
  - Autofill failing frequently in non-browser Android apps or WebView wrappers.
  - Reluctance to trust cloud-based password managers with sensitive national IDs, secret notes, or banking PINs.
  - Needing to leave the current app, open a heavy password manager, authenticate, search, copy, switch back, and paste.

---

## 3. Core Problems Solved

1. **Repetitive Data Entry Friction**: Mobile keyboards are clumsy for long alphanumeric sequences (IBAN, Card Numbers, Passwords, National IDs).
2. **Context Switching Overhead**: Leaving a checkout form to copy a card number from a notes app often causes the checkout session to timeout or reload.
3. **Privacy & Cloud Anxiety**: Users want absolute physical sovereignty over their secrets without third-party server synchronization, mandatory cloud accounts, or telemetry.
4. **Fragile Android Autofill**: Standard Accessibility/Autofill services frequently fail inside native banking apps or dynamic forms.

---

## 4. Differentiators vs. Traditional Password Managers

| Dimension | Traditional Password Managers (Bitwarden, 1Password) | MyVault |
| :--- | :--- | :--- |
| **Data Scope** | Focused primarily on Web URLs + Login Credentials. | **Holistic Personal Vault**: National ID, IBAN, Bank Cards, Custom Profiles, Credentials, Secure Notes. |
| **Access Mechanism** | Relies heavily on System Autofill framework (often blocked by banking apps). | **Floating Overlay Quick Access**: Draws over any app with 1-tap copy & auto-collapse. |
| **Network Footprint** | Cloud-first / Remote sync required or default. | **100% Offline**: Zero internet permission, zero server dependency, zero telemetry. |
| **Interaction Target** | Fill whole form via autofill. | **Field-by-Field Quick Copy**: Optimized for single-tap clipboard copy + fast paste. |
| **Form Factor** | Fullscreen heavy application. | **Dual-Engine UI**: Full management vault + Compact floating overlay utility. |

---

## 5. The Core Loop

```
┌─────────────────────────────────────────────────────────────┐
│                       1. SAVE ONCE                          │
│   User inputs Identity, Card, or Login details once.        │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                    2. INSTANT DISCOVERY                     │
│   From home screen or Floating Overlay, user finds record   │
│   via in-memory real-time search (<16ms) or Category Chip.  │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                     3. ONE-TAP COPY                         │
│   User taps dedicated Quick-Copy icon button directly       │
│   on the card without opening or scrolling full detail.      │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                    4. PASTE & EXECUTE                       │
│   Overlay auto-minimizes back to bubble; user pastes into   │
│   target app immediately with zero context loss.            │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                    5. ZERO MANUAL RE-TYPING                 │
│   User accomplishes task in seconds without friction.       │
└─────────────────────────────────────────────────────────────┘
```

---

## 6. Core User Jobs Prioritization

### 🔴 Critical (Tier 1 — Core Survival & Loop)
* **Open & Unlock Vault**: Authenticate instantly via Biometrics (Fingerprint/Face) or 4-6 digit numeric PIN.
* **Instant Search**: Search records across title, username, and category with zero keystroke latency.
* **1-Tap Secret Copy**: Copy password, credit card number, IBAN, or National ID with one thumb tap.
* **Trigger Floating Overlay**: Expand floating search panel directly on top of third-party apps.
* **Auto-Collapse Overlay on Copy**: Seamlessly return to underlying app upon copying data.

### 🟡 Important (Tier 2 — Daily Management)
* **Create New Entry**: Fast, streamlined entry creation with pre-defined categories and field validation.
* **Generate Strong Password**: Built-in, configurable password generator directly in form context.
* **Mask / Reveal Sensitive Fields**: Visual toggle (`••••` vs plaintext) with automatic 30s re-mask timer.
* **Filter by Category**: Quick-switch between Logins, Cards, Identity, and Notes.
* **Auto-Lock on Backgrounding**: Lock vault immediately or after user-configured timeout upon app switch.

### ⚪ Secondary (Tier 3 — Configuration & Maintenance)
* **Edit / Update Existing Entry**: Modify fields, update expired card dates or passwords.
* **Delete Entry**: Destructive action protected by explicit confirmation dialog.
* **Change Master PIN**: Secure verification of old PIN before setting new numeric-only PIN.
* **Toggle Persistent vs. Session Overlay**: Choose whether floating button stays after app swipe.
* **Wipe All Data**: Emergency vault reset requiring multi-step explicit confirmation.

---
name: myvault-ui-ux
description: "Comprehensive native Android UI/UX design and interaction specification for MyVault, built with Jetpack Compose, Material 3, and privacy-first UX principles."
risk: safe
source: workspace
date_added: "2026-08-30"
author: MyVault Engineering
tags: [android, jetpack-compose, material3, ui-ux, mobile-design, privacy, vault, security, overlay]
tools: [antigravity, claude, codex, cursor, gemini]
---

# MyVault Native Android UI/UX Specification

## Overview

This skill establishes the definitive, production-grade UI/UX design system and interaction framework for **MyVault** (Offline Personal Data & Password Vault). It governs all screen designs, component architectures, user journeys, accessibility requirements, and motion standards built exclusively with **Android Native, Jetpack Compose, and Material Design 3 (M3)**.

---

## 1. Product Context

### What MyVault Is
MyVault is an **offline-first, zero-knowledge, privacy-centric personal data and secret vault for Android**. It encrypts and securely stores sensitive personal identities, credentials, bank/credit cards, and custom records directly on the user's physical device with zero telemetry and zero external server transmission.

### Core Problem Solved
Users continuously re-type repetitive personal details (National ID, IBAN, credit card numbers, addresses, complex passwords) into various mobile applications and web browsers. MyVault solves this by providing:
1. An encrypted, biometrically guarded central vault.
2. A **floating overlay access window** that opens on top of any target app, allowing users to find and copy any record with a single tap without switching tasks.
3. Total offline sovereignty—ensuring privacy and resilience against data breaches.

---

## 2. UX Principles

1. **Security Without Friction**: Security measures (PIN, Biometrics) must be robust but never sluggish. App unlocking and credential retrieval must feel instantaneous.
2. **Zero Ambient Data Leakage**: Sensitive data (passwords, card numbers, CVVs, identity numbers) must never be displayed in plain text by default in public environments.
3. **Single-Tap Utility**: The primary objective of opening a record is almost always to copy it. Copy actions must be the easiest, most accessible interaction on every card.
4. **Touch Ergonomics & Thumb Zone**: All critical interactive targets (Copy, Search, Add, Category Filters) must sit within the natural reach of the user's thumb (lower 60% of the viewport).
5. **No Visual Slop / Purpose-Built Aesthetics**: Every pixel, elevation, and card boundary must serve information hierarchy and readability. Avoid decorative gradients, unreadable glassmorphism, or non-functional card nesting.
6. **Predictable Native Android Gestures**: Respect Android navigation paradigms—system back gestures, dynamic insets, bottom sheets, edge-to-edge rendering, and hardware haptic feedback.

---

## 3. Information Architecture

Data in MyVault is structured into distinct, recognizable categories with specialized field types:

### Category Organization
| Category | Key Fields | Primary Copy Target | Masking Rule |
| :--- | :--- | :--- | :--- |
| **👤 Personal & Identity** | Full Name, National ID / TC, Birth Date, Phone, Email, Address | National ID / Phone / Address | National ID masked (`••••••1234`) |
| **💳 Banking & Cards** | Cardholder Name, Card Number, Expiry, CVV, IBAN, Bank Name | Card Number / IBAN | Card Number (`•••• •••• •••• 1234`), CVV (`•••`) |
| **🔐 Accounts & Logins** | Title, Service URL, Username / Email, Password, Notes | Password / Username | Password masked (`••••••••••••`) |
| **📝 Notes & Custom** | Title, Content, Custom Key-Value Pairs | Custom Value / Content | User-toggleable |

### Structural Hierarchy
```
Vault Root (Home Screen)
├── Search Bar (Global real-time filter)
├── Quick Actions (Start Floating Overlay / Add New Entry)
├── Category Filter Chips (All, Identity, Cards, Logins, Notes)
└── Entry List (Grouped / Filtered Cards with Quick-Copy Actions)
    └── Entry Detail Screen (Expanded view, field-by-field copy, reveal toggles, edit/delete)
```

---

## 4. Navigation Model

MyVault uses a deliberate, lightweight native navigation model:

1. **Top App Bar**:
   - Clean, functional `CenterAlignedTopAppBar` or `TopAppBar`.
   - Displays current context (Title, Vault Status / Lock icon, Settings shortcut).
   - Minimal action icons (Search, Lock, More).
2. **Modal Bottom Sheet (`ModalBottomSheet`)**:
   - Used for quick selection, category filtering, password generation tools, and confirmation prompts.
   - Dismissable via downward swipe, scrim tap, or Android system back.
3. **Detail Navigation (`NavHost`)**:
   - Screen-to-screen navigation uses Android Jetpack Navigation Compose with type-safe route definitions.
   - Smooth horizontal slide transitions (`slideInHorizontally` / `slideOutHorizontally`) respecting user motion preferences.
4. **Floating Action Button (FAB)**:
   - Primary `ExtendedFloatingActionButton` or `FloatingActionButton` anchored in the bottom-right thumb zone for creating new entries.
   - Automatically hides or contracts on fast list scroll to avoid obscuring card data.

---

## 5. Sensitive Data UI & Masking Rules

### Default Masking State
* **Passwords**: Displayed as bullet dots (`••••••••••••`).
* **Credit Cards**: Displayed with only the last 4 digits visible (`•••• •••• •••• 4242`).
* **CVV / CVC**: Completely masked (`•••`).
* **National ID Numbers**: Middle digits masked (`123•••••89`).
* **IBAN**: Middle blocks masked (`TR•• •••• •••• •••• •••• ••12 34`).

### Reveal & Toggle Behavior
1. **Eye Icon Toggle (`IconButton`)**:
   - Tapping the reveal icon displays the plain text immediately with zero lag.
   - Minimum touch target: **48×48dp**.
2. **Auto-Re-Mask Timer**:
   - If revealed, sensitive fields automatically re-mask after **30 seconds** of inactivity.
3. **Monospace Tabular Alignment**:
   - All masked and revealed numbers (PIN, Card Numbers, Passwords) must use **monospace, tabular figures** to prevent layout shift or visual jitter when toggling.

---

## 6. Copy Interaction & Clipboard Ergonomics

Copying data is the **core transaction** of MyVault:

1. **Dedicated Quick-Copy Button**:
   - Every credential card in the list must feature a prominent, thumb-friendly copy icon button for the primary secret (e.g., Password or Card Number).
2. **One-Tap Execution**:
   - Tapping the copy icon copies the plain-text secret directly to the Android `ClipboardManager`.
3. **Haptic Confirmation**:
   - Trigger a brief, crisp haptic feedback (`HapticFeedbackType.LongPress` or `View.performHapticFeedback(HapticFeedbackConstants.CONFIRM)`).
4. **Visual Toast / Snackbar Feedback**:
   - Display a lightweight, non-blocking visual feedback (e.g., `"Şifre panoya kopyalandı"` / `"Password copied"`).
   - Avoid intrusive full-screen blocking modals.
5. **Auto-Clear Clipboard Strategy**:
   - On Android 13+ (API 33+), flag sensitive content with `ClipDescription.EXTRA_IS_SENSITIVE` to prevent system clipboard preview overlays from leaking secrets.

---

## 7. Vault Screen UX Architecture

### Home / Vault Screen
* **Search Anchor**: Prominent search bar at top of scroll view with instant character-by-character filtering.
* **Category Selector**: Horizontally scrollable `FilterChip` row for instant filtering by category.
* **Entry Cards (`EntryCard`)**:
  - Clear visual hierarchy: Category Icon + Title + Secondary Label (Username / Last 4 digits).
  - Quick-action Copy button aligned on the right edge.
  - Tapping card opens `EntryDetailScreen`.
* **Floating Overlay Launch Card**:
  - Prominent quick-start card that activates the overlay and minimizes the app in one smooth motion.

### Entry Detail Screen
* Organized field-by-field in clean Material 3 surface sections.
* Each field displays: Label, Masked/Plain Value, Copy Button, Reveal Button (for secrets).
* Bottom actions: Edit Entry, Share (with confirmation), Delete Entry (destructive styling).

### Add / Edit Entry Screen
* Clean form layout with autofocus on first field.
* Password Generator widget integrated directly next to password field.
* Category picker with visual icon badges.
* Clear "Cancel" vs "Save" action buttons.

### Delete Confirmation
* Destructive actions require explicit `AlertDialog` confirmation with clear explanation.
* Delete button styled with `MaterialTheme.colorScheme.error`.

---

## 8. Search UX

1. **Instant, Zero-Latency Filtering**: Search filtering must execute in-memory across the loaded decrypted list via `StateFlow` (<16ms per keystroke).
2. **Multi-Field Matching**: Searches match against Title, Username, Service URL, Category, and Notes.
3. **Search Highlighting**: (Optional) Substring matches subtly highlighted.
4. **Clear Action**: A prominent `(X)` icon to immediately clear query.
5. **Zero-Result State**: When no match is found, display a clear, helpful empty state: `"No entries found for '[query]'"` with an "Add New Entry" shortcut.

---

## 9. Screen States (Empty / Loading / Error / Locked)

Every screen must explicitly define and render all five canonical states:

```
ScreenState
├── Loading    -> Centered CircularProgressIndicator / Subtle Shimmer
├── Empty      -> Relevant illustration/icon, clear explanation, primary CTA button
├── Content    -> Populated data list or form
├── Error      -> Clear message, non-technical explanation, retry button
└── Locked     -> PIN / Biometric verification prompt covering all sensitive UI
```

### Specific State Guidelines:
* **Empty Vault State**: `"Kasanız henüz boş. İlk kaydınızı ekleyin."` + Large "Kayıt Ekle" button.
* **Locked State**: Screen content is blurred/hidden from the Android recent tasks thumbnail (`FLAG_SECURE`).

---

## 10. Authentication UX (PIN & Biometrics)

1. **Biometric First**: If enabled and hardware supports it, trigger `BiometricPrompt` immediately on app resume.
2. **PIN Fallback**: If biometric fails, canceled, or unavailable, seamlessly present the custom `PinKeypad`.
3. **Numeric-Only PinPad**:
   - 4-6 digit numeric circles with instant visual feedback.
   - Large, thumb-friendly numeric keys (min 64×64dp).
   - Dedicated Delete and Biometric shortcut buttons.
4. **Error Feedback**:
   - On incorrect PIN: Shake animation on PIN dots + haptic warning vibration + clear error text.
   - Reset input dots immediately.
5. **App Inactivity Lock**:
   - Re-prompt authentication whenever the app returns from the background after user-defined timeout.

---

## 11. Floating Overlay Access UX (`OverlayService`)

The Floating Overlay is a specialized, compact UI designed to operate **on top of third-party apps**:

1. **Floating Bubble (Minimized State)**:
   - Compact 48×48dp floating shield icon anchored to the screen edge.
   - Draggable along viewport borders with momentum physics.
   - Single tap expands into the Floating Search Panel.
2. **Floating Search Panel (Expanded State)**:
   - Card dimensions: Max 92% screen width, elevated dark/light surface.
   - Real-time search bar + horizontal category filter chips.
   - Compact list of matching entries showing Title, Username, and Quick Copy button.
3. **One-Tap Copy & Auto-Minimize**:
   - Tapping any data field copies the secret to clipboard AND immediately minimizes the panel back to the bubble so the user can paste right away.
4. **Outside Touch Dismissal**:
   - Tapping outside the floating panel automatically collapses it back to the bubble without interrupting the underlying app.
5. **Session vs Persistent Behavior**:
   - **Persistent Mode (Settings ON)**: Floating button stays active even after closing MyVault from recent apps.
   - **Session Mode (Settings OFF, started from Home)**: Floating button automatically terminates when MyVault is swiped away from recent tasks.

---

## 12. Material Design 3 & Jetpack Compose Standards

* **Declarative UI**: 100% Jetpack Compose. No legacy XML views, ViewBinding, or synthetic layouts.
* **Theme Token Usage**:
  - Always consume `MaterialTheme.colorScheme.*` (e.g., `surface`, `onSurface`, `primary`, `error`).
  - Never hardcode arbitrary hex values inside Composable functions.
* **Typography Tokens**:
  - Always consume `MaterialTheme.typography.*` (`headlineMedium`, `titleMedium`, `bodyLarge`, `labelSmall`).
* **Shape Tokens**:
  - Use `MaterialTheme.shapes.*` or explicit `RoundedCornerShape(12.dp)` for consistent corner radii across cards, buttons, and dialogs.
* **Sub-Composable Rule**:
  - Keep Composable functions modular, focused, and under 80 lines.
  - Separate stateful screen containers from stateless presentation components.

---

## 13. Accessibility (a11y) & Inclusive Design

1. **Touch Target Size**: Every clickable element (Copy button, Reveal icon, Keypad button, Chip) must satisfy the Android minimum touch target of **48×48dp** (`Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)`).
2. **Content Descriptions**: All `Icon` and `IconButton` composables must provide descriptive localized `contentDescription` strings (e.g., `"Şifreyi panoya kopyala"`, `"Şifreyi göster"`).
3. **Color Contrast Ratio**: Text against surface must meet or exceed WCAG AA ratio of **4.5:1**; interactive controls must exceed **3:1**.
4. **Dynamic Font Scaling**: All text sizing must use `sp` units to scale gracefully when users increase system font size in Android Accessibility Settings.
5. **TalkBack Navigation**: Semantic headers, list indices, and masked field descriptions must be announced intelligibly by TalkBack screen readers.

---

## 14. Responsive Android UI & Screen Adaptability

* **WindowSizeClass**: Support Compact (Phones), Medium (Foldables/Small Tablets), and Expanded (Tablets/Desktops).
* **Landscape Orientation**:
  - Detail screen transforms to a two-pane layout or scrollable container ensuring form inputs and PIN keypads do not get clipped behind software keyboards.
* **Edge-to-Edge & Window Insets**:
  - Proper handling of `WindowInsets.safeDrawing`, `statusBars`, and `navigationBars` so UI content is never obscured by camera cutouts or Android gesture navigation bars.

---

## 15. Dark Mode & High-Contrast Readability

1. **Dynamic Theme Switching**: Full support for System Default, Forced Light, and Forced Dark themes.
2. **Surface Elevation in Dark Mode**: Use tonal elevation colors rather than harsh pure blacks/pure whites to maintain subtle depth between background, cards, and dialogs.
3. **Sensitive Text Legibility**: Ensure masked dot indicators (`••••`) and monospace passwords remain sharply legible with strong contrast against dark card surfaces.

---

## 16. Motion & Animation Guidelines

1. **Purpose-Driven Only**: In a security vault, animations must communicate state transitions or physical feedback. Never add distracting, purely decorative animations.
2. **Snappy & Rapid (<250ms)**:
   - Dialog and Bottom Sheet entrances: 200–250ms with `FastOutSlowInEasing`.
   - Button press feedback: Immediate visual scale feedback (`scale(0.97f)`) using Compose `animateFloatAsState`.
   - List filtering and layout morphing: Smooth, subtle transitions (`animateContentSize()`).
3. **Zero Animation on Heavy Repeat Actions**:
   - Keyboard PIN entry, real-time search typing, and clipboard copy feedback must react in **0ms** without sluggish transition delays.
4. **Reduced Motion**: Respect system settings for users who have disabled animations in Android OS settings.

---

## 17. Anti-Slop Design Rules

To ensure MyVault maintains a world-class, premium, and trustworthy utility interface:

* 🚫 **NO Generic AI Gradients**: Avoid loud, rainbow-colored, non-functional background gradients.
* 🚫 **NO Pointless Glassmorphism**: Do not use heavy blur effects that impair text readability or degrade battery/GPU performance on mid-range Android devices.
* 🚫 **NO Nested Card Clutter**: Avoid wrapping cards inside cards inside cards. Maintain a clean, flat surface elevation hierarchy.
* 🚫 **NO Fake Marketing Metric Counters**: Do not display artificial statistics or decorative progress rings that have no user utility.
* ✅ **Focus on Clean Information Density**: Clear typography, crisp borders, generous touch targets, meaningful color accents, and prominent copy actions.

---

## 18. Semantic Design Tokens Reference

When designing or implementing UI, always bind values to semantic design tokens:

| Token Category | Token Name | Android Compose Usage | Purpose |
| :--- | :--- | :--- | :--- |
| **Color** | `colorScheme.surface` | `MaterialTheme.colorScheme.surface` | Base app background & card surface |
| **Color** | `colorScheme.surfaceVariant` | `MaterialTheme.colorScheme.surfaceVariant` | Input fields, category chips, subtle borders |
| **Color** | `colorScheme.primary` | `MaterialTheme.colorScheme.primary` | Primary CTAs, active filters, key brand icons |
| **Color** | `colorScheme.error` | `MaterialTheme.colorScheme.error` | Destructive delete buttons, authentication error alerts |
| **Typography** | `typography.headlineMedium`| `MaterialTheme.typography.headlineMedium`| Screen titles (Home, Settings, Add Entry) |
| **Typography** | `typography.titleMedium` | `MaterialTheme.typography.titleMedium` | Entry card titles, category headings |
| **Typography** | `typography.bodyMedium` | `MaterialTheme.typography.bodyMedium` | Form labels, descriptions, usernames |
| **Typography** | `typography.bodyLarge` (Mono) | `FontFamily.Monospace` | Masked passwords, card numbers, IBANs, PINs |
| **Spacing** | `Spacing.xs` (4dp) .. `xl` (32dp) | `4.dp`, `8.dp`, `12.dp`, `16.dp`, `24.dp` | Consistent 4dp spacing grid |
| **Shape** | `shapes.medium` / `shapes.large` | `RoundedCornerShape(12.dp)` | Entry cards, dialogs, bottom sheets |

---

## 19. 10-Step UX Workflow for New Screens

Before implementing any new screen or revising an existing one in MyVault, follow this sequential design checkpoint:

```
Step 1: Identify User Goal (Why is the user opening this screen?)
Step 2: Define Primary Action (What is the single most important action? e.g., Copy Secret)
Step 3: Define Secondary Actions (Edit, Delete, Reveal, Share, Filter)
Step 4: Audit Sensitive Fields (Which fields must be masked by default?)
Step 5: Establish Navigation Path (Back gesture, TopAppBar actions, BottomSheet dismissal)
Step 6: Define All 5 Screen States (Loading, Empty, Content, Error, Locked)
Step 7: Enforce Accessibility (Min 48dp touch targets, TalkBack content descriptions, contrast)
Step 8: Implement Error Prevention (Confirmation dialogs on destructive deletion)
Step 9: Review Visual Hierarchy (Thumb zone reachability, zero slop, clear typography)
Step 10: Implement in Jetpack Compose (Stateless sub-composables, MVI/MVVM, StateFlow)
```

---

## 20. Do Not (Hard Prohibitions)

* ❌ **DO NOT** use web technologies, HTML, CSS, Tailwind, or React paradigms in any MyVault Android specification or code.
* ❌ **DO NOT** hardcode raw hex colors (e.g., `#FF0000`) or hardcoded DP paddings without referencing Material 3 tokens or the 4dp grid.
* ❌ **DO NOT** create clickable elements smaller than 48×48dp.
* ❌ **DO NOT** display passwords, credit cards, CVVs, or national IDs in plain text without an explicit user reveal interaction.
* ❌ **DO NOT** invent non-existent cryptographic algorithms or complex web-worker protocols.
* ❌ **DO NOT** add sluggish, decorative animations to frequently repeated user interactions (Search, PIN, Copy).
* ❌ **DO NOT** navigate away from the current third-party app when the user taps the floating overlay bubble.

---

## 21. Always Check (Pre-Ship Verification)

* [ ] Is the screen 100% Jetpack Compose and Material 3 compliant?
* [ ] Are all sensitive data fields masked by default?
* [ ] Is the primary copy button directly reachable in the thumb zone?
* [ ] Does the copy interaction trigger haptic feedback and clear clipboard status?
* [ ] Are all interactive touch targets at least **48×48dp**?
* [ ] Does the screen handle all 5 states (Loading, Empty, Content, Error, Locked)?
* [ ] Is `FLAG_SECURE` respected to prevent sensitive thumbnails in recent tasks?
* [ ] Are all icon buttons supplied with descriptive, localized `contentDescription`s?
* [ ] Is the layout fully responsive across Light Mode, Dark Mode, and Dynamic Font Scales?
* [ ] Does the floating overlay automatically minimize upon copying data and upon outside touches?

# MyVault — Design Token Specification Dictionary

This document serves as the master **Design Token Dictionary** for MyVault. It bridges the visual design architecture directly into the upcoming Jetpack Compose implementation.

---

## 1. Color Tokens (`MyVaultColors`)

| Token Name | Semantic Role | Dark Mode (Default) | Light Mode |
| :--- | :--- | :--- | :--- |
| `color.primary` | Primary action / Brand anchor | Tech Sapphire (`#ADC6FF`) | Royal Cobalt (`#2B5EAA`) |
| `color.onPrimary` | Text on Primary | Deep Navy (`#002E69`) | Pure White (`#FFFFFF`) |
| `color.primaryContainer` | Selected chip / Active highlight | Dark Blue Tint (`#1B447A`)| Soft Cobalt Tint (`#D8E2FF`) |
| `color.onPrimaryContainer`| Text on Primary Container | Crisp Pale Blue (`#D8E2FF`)| Deep Navy (`#001A41`) |
| `color.secondary` | Functional chrome / Copy accent | Slate Cyan (`#BBC7DB`) | Slate Blue (`#535F70`) |
| `color.onSecondary` | Text on Secondary | Slate Black (`#253140`) | Pure White (`#FFFFFF`) |
| `color.surface` | Foundational base background | Deep Obsidian (`#121316`) | Alabaster Canvas (`#F8F9FC`) |
| `color.onSurface` | High-contrast primary text | Off-White (`#E2E2E6`) | Charcoal Black (`#191C1E`) |
| `color.onSurfaceVariant` | Secondary text (Usernames, dates)| Muted Silver (`#C4C6D0`) | Slate Gray (`#44474E`) |
| `color.surfaceContainerLow`| Search bar container fill | Tonal Charcoal (`#17191D`)| Light Gray (`#F1F3F8`) |
| `color.surfaceContainer` | Entry card container fill | Tonal Slate (`#1A1C20`) | Pure Card White (`#FFFFFF`) |
| `color.surfaceContainerHigh`| Modal sheets & dialogs | Raised Slate (`#22252A`) | Soft Off-White (`#EBEFF5`) |
| `color.surfaceContainerHighest`| Floating Overlay Panel | High Slate (`#2A2D33`) | High Contrast White (`#FFFFFF`)|
| `color.outline` | Active border / Outlined input | Subtle Gray (`#8E9099`) | Mid Gray (`#74777F`) |
| `color.outlineVariant` | Inactive divider / Card stroke | Faint Slate (`#44474E` / 12%)| Faint Gray (`#C4C6D0` / 30%)|
| `color.error` | Destructive action / PIN error | Clear Crimson (`#FF897D`) | Deep Crimson (`#BA1A1A`) |
| `color.onError` | Text on Error | Dark Maroon (`#690005`) | Pure White (`#FFFFFF`) |
| `color.success` | Copy feedback / Verified status | Mint Emerald (`#81D498`) | Forest Emerald (`#1B6D38`) |
| `color.onSuccess` | Text on Success | Dark Forest (`#00391A`) | Pure White (`#FFFFFF`) |

---

## 2. Typography Tokens (`MyVaultTypography`)

| Token Name | Font Family | Size (`sp`) | Line Height (`sp`) | Weight | Applied Usage |
| :--- | :--- | :---: | :---: | :--- | :--- |
| `type.headlineMedium` | Sans-Serif (Roboto/Inter) | `28.sp` | `36.sp` | SemiBold (600) | TopAppBar Screen Titles |
| `type.headlineSmall` | Sans-Serif | `24.sp` | `32.sp` | Medium (500) | PIN Keypad Display Header |
| `type.titleLarge` | Sans-Serif | `22.sp` | `28.sp` | SemiBold (600) | Entry Detail Main Title |
| `type.titleMedium` | Sans-Serif | `16.sp` | `24.sp` | Medium (500) | `EntryCard` Titles, Sheet Titles |
| `type.titleSmall` | Sans-Serif | `14.sp` | `20.sp` | Medium (500) | Form Section Headers |
| `type.bodyLargeMono` | **Monospace (`tnum`)** | `16.sp` | `24.sp` | Medium (500) | **Masked Secrets, Passwords, PINs** |
| `type.bodyMedium` | Sans-Serif | `14.sp` | `20.sp` | Regular (400) | Usernames, Form Input Text |
| `type.bodySmall` | Sans-Serif | `12.sp` | `16.sp` | Regular (400) | Supporting labels, helper text |
| `type.labelLarge` | Sans-Serif | `14.sp` | `20.sp` | SemiBold (600) | Primary CTA Button Labels |
| `type.labelMedium` | Sans-Serif | `12.sp` | `16.sp` | Medium (500) | Category Filter Chip Text |
| `type.labelSmall` | Sans-Serif | `11.sp` | `16.sp` | Medium (500) | Badges, Timestamps, Section tags |

---

## 3. Spacing Tokens (`MyVaultSpacing`)

| Token Name | Dimension | Usage Rule |
| :--- | :---: | :--- |
| `spacing.xxs` | `4.dp` | Micro-gaps between badge icon and text. |
| `spacing.xs` | `8.dp` | Horizontal gap between chips, list item vertical separation. |
| `spacing.s` | `12.dp` | Gap between form inputs, inner padding of compact overlay items. |
| `spacing.m` | `16.dp` | **Standard outer screen margin**, card inner padding, TopAppBar padding. |
| `spacing.l` | `20.dp` | Spacing between grouped field containers on Detail screen. |
| `spacing.xl` | `24.dp` | Modal bottom sheet header padding, dialog margins. |
| `spacing.xxl` | `32.dp` | Vertical padding above PinKeypad numbers and Empty states. |
| `spacing.touchTarget` | `48.dp` | **Minimum accessible touch target width & height**. |

---

## 4. Shape Tokens (`MyVaultShapes`)

| Token Name | Radius Spec | Applied Usage |
| :--- | :--- | :--- |
| `shape.extraSmall` | `RoundedCornerShape(4.dp)` | Category count pills, password strength badges. |
| `shape.small` | `RoundedCornerShape(8.dp)` | `FilterChip`, Outlined TextFields, Primary Buttons. |
| `shape.medium` | `RoundedCornerShape(12.dp)`| **`EntryCard` containers**, `AlertDialog` boxes. |
| `shape.large` | `RoundedCornerShape(16.dp)`| `VaultSearchBar`, `FloatingOverlayPanel` container. |
| `shape.extraLarge` | `RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)` | `ModalBottomSheet` top sheet edge. |
| `shape.full` | `CircleShape` | `+ FAB`, 48dp Floating Bubble, PIN indicator dots. |

---

## 5. Motion Tokens (`MyVaultMotion`)

| Token Name | Value / Spec | Applied Usage |
| :--- | :--- | :--- |
| `motion.durationFast` | `120ms` | Button touch press scale down. |
| `motion.durationNormal` | `180ms` | Copy icon checkmark morph, Overlay expand. |
| `motion.durationScreen` | `220ms` | Screen-to-screen slide navigation. |
| `motion.durationSheet` | `250ms` | ModalBottomSheet upward entrance. |
| `motion.springPress` | `damping = 0.6f, stiffness = Medium` | Interactive button depression. |
| `motion.springOverlay` | `damping = 0.75f, stiffness = Medium`| Floating window expansion & snapping. |
| `motion.easingSnappy` | `FastOutSlowInEasing` | Standard UI state transitions. |

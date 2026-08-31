# MyVault — Spacing Scale, Shape Language & Elevation System

## 1. 4dp Grid Spacing System

MyVault strictly adheres to the Android **4dp Grid System**. All margins, paddings, icon dimensions, and layout offsets must map to values in the canonical spacing scale.

```
┌─────────────────────────────────────────────────────────────┐
│                        SPACING SCALE                        │
│   4dp  (XXS) ──> Icon/Text micro gaps, inner badge padding  │
│   8dp  (XS)  ──> Gap between chips, inner button padding    │
│   12dp (S)   ──> Gap between form fields, compact padding   │
│   16dp (M)   ──> Standard screen margin, card inner padding │
│   20dp (L)   ──> Vertical gap between form sections         │
│   24dp (XL)  ──> Header-to-content gap, dialog padding     │
│   32dp (XXL) ──> Gap between Keypad numbers & PIN dots      │
│   48dp (Min) ──> MINIMUM ACCESSIBLE TOUCH TARGET DIMENSION  │
└─────────────────────────────────────────────────────────────┘
```

---

## 2. Spacing Token Usage Matrix

| Token Name | Value | Android DP | Applied Usage in MyVault |
| :--- | :--- | :--- | :--- |
| **`Spacing.xxs`** | `4dp` | `4.dp` | Gap between category icon and chip label; badge padding. |
| **`Spacing.xs`** | `8dp` | `8.dp` | Horizontal gap between `FilterChip` items; list item vertical gap. |
| **`Spacing.s`** | `12dp` | `12.dp` | Gap between form text fields; inner padding of compact overlay items. |
| **`Spacing.m`** | `16dp` | `16.dp` | **Standard outer screen margin**; `EntryCard` internal padding; TopAppBar padding. |
| **`Spacing.l`** | `20dp` | `20.dp` | Spacing between grouped field containers on `EntryDetailScreen`. |
| **`Spacing.xl`** | `24dp` | `24.dp` | Top and bottom padding of `ModalBottomSheet` and `AlertDialog`. |
| **`Spacing.xxl`** | `32dp` | `32.dp` | Vertical breathing room above `PinKeypad` and Empty State illustrations. |
| **`TouchTarget.min`** | `48dp`| `48.dp` | Minimum width and height of any interactive tap target (`IconButton`, `Button`, `Chip`). |

---

## 3. Shape Language Scale (Material 3 Corner Radii)

MyVault avoids excessive, cartoonish pill radii on rectangular containers. It employs disciplined, mathematically harmonious radii that feel structured and trustworthy.

```
┌─────────────────────────────────────────────────────────────┐
│                         SHAPE SCALE                         │
│   4dp  (Extra Small) ──> Badges, Tooltips                   │
│   8dp  (Small)       ──> Filter Chips, Outlined Inputs      │
│   12dp (Medium)      ──> EntryCard, Dialogs                 │
│   16dp (Large)       ──> Floating Overlay Panel, Hero Cards │
│   24dp (Extra Large) ──> Top Corners of ModalBottomSheet    │
│   Pill (Full Circle) ──> FAB, Overlay Bubble, Circular Icon │
└─────────────────────────────────────────────────────────────┘
```

| Shape Token | Corner Radius | Material 3 Component Application |
| :--- | :--- | :--- |
| **`Shape.extraSmall`** | `4dp` (`RoundedCornerShape(4.dp)`) | Category count badges, security strength indicators. |
| **`Shape.small`** | `8dp` (`RoundedCornerShape(8.dp)`) | `FilterChip`, Outlined Text Fields, Secondary action buttons. |
| **`Shape.medium`** | `12dp` (`RoundedCornerShape(12.dp)`) | **`EntryCard` containers**, `AlertDialog` containers, Detail field cards. |
| **`Shape.large`** | `16dp` (`RoundedCornerShape(16.dp)`) | `FloatingOverlayPanel` expanded container, Search Bar container. |
| **`Shape.extraLarge`**| `24dp` (`RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)`) | `ModalBottomSheet` top header corners. |
| **`Shape.full`** | `1000dp` / `CircleShape` | `+ FAB`, 48dp Minimized Floating Bubble, PIN dot indicators. |

---

## 4. Elevation & Surface Hierarchy

In Material Design 3, elevation is primarily communicated via **Tonal Surface Shifts** rather than heavy drop shadows.

```
Elevation Level 0 (Base Canvas)   ──> MaterialTheme.colorScheme.surface
       ▲
Elevation Level 1 (Entry Cards)   ──> MaterialTheme.colorScheme.surfaceContainer
       ▲
Elevation Level 2 (Search Anchor) ──> MaterialTheme.colorScheme.surfaceContainerHigh
       ▲
Elevation Level 3 (Dialogs/Sheets)──> MaterialTheme.colorScheme.surfaceContainerHighest
       ▲
Elevation Level 4 (Overlay Panel) ──> Floating System Window (Tonal + 16dp System Shadow)
```

### Elevation Rules:
1. **No Gratuitous Card-in-Card Nesting**: A list of fields on `EntryDetailScreen` sits directly on a single `surfaceContainer` group rather than nesting individual cards inside parent cards.
2. **Subtle Outlines**: In Dark Mode, cards use a faint 1dp `outlineVariant` (12% opacity) to provide crisp boundary definition without glowing or high contrast lines.
3. **Floating Overlay Shadow**: Because the overlay sits on top of unpredictable third-party apps, it uses both a high-tonal surface (`surfaceContainerHighest`) and a 16dp system drop shadow to ensure separation against white, black, or colorful host apps.

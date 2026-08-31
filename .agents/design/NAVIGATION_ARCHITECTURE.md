# MyVault — Navigation Architecture & Ergonomics

## 1. Comparative Evaluation of Navigation Models

We evaluated five standard Android navigation patterns against MyVault's core objective: **minimizing time-to-copy (<3 seconds)**.

| Navigation Model | Ergonomics & Reach | Speed to Copy | Cognitive Load | Verdict for MyVault |
| :--- | :--- | :--- | :--- | :--- |
| **A) Classic Bottom Navigation (4-5 Tabs)** | 🟢 High (Bottom bar in thumb zone) | 🟡 Medium (Separating "Search", "Vault", "Categories" into tabs requires unnecessary tab hopping) | 🟡 Medium (Tabs fragment the single vault) | ❌ **Rejected**: Artificial fragmentation. Searching should be instant on the main canvas. |
| **B) Navigation Drawer (Side Menu)** | 🔴 Low (Hamburger icon top-left requires 2 hands or hand stretch) | 🔴 Slow (2 taps just to switch category) | 🔴 High (Hidden destinations reduce discoverability) | ❌ **Rejected**: Worst mobile ergonomics for high-frequency utility apps. |
| **C) Pure Single-Screen + Search** | 🟢 High (Everything on one page) | 🟢 Fast for search | 🔴 High when browsing large vaults without category filters | ❌ **Rejected**: Lacks structure when user has >30 entries. |
| **D) Top Tabs (Fixed Pager)** | 🟡 Medium (Top tabs are outside easy thumb reach) | 🟡 Medium (Horizontal swipe can conflict with Android back gestures) | 🟢 Low | ❌ **Rejected**: Top placement forces unnatural grip shift. |
| **E) Hybrid: Unified Vault Canvas + Search Anchor + Category Filter Chips + Modal Bottom Sheets** | 🟢 **Optimal** (Search, Chips, FAB, and Copy actions all anchored in lower 65% thumb zone) | 🟢 **Fastest (0-tap start)** | 🟢 **Lowest** (Unified list with instant single-tap filters) | 🏆 **RECOMMENDED** |

---

## 2. Recommended Navigation Architecture: The Unified Hybrid Canvas

```
                               ┌────────────────────────┐
                               │     MyVault TopBar     │ (Lock / Settings)
                               ├────────────────────────┤
                               │    [ Search Vault ]    │ (Instant in-memory filter)
                               ├────────────────────────┤
                               │ [All] [Logins] [Cards] │ (Thumb-friendly Category Chips)
                               ├────────────────────────┤
                               │ ┌────────────────────┐ │
                               │ │ [Category]  [Copy] │ │
                               │ │ Title / Subtitle   │ │ (1-Tap Quick-Copy Card)
                               │ └────────────────────┘ │
                               │ ┌────────────────────┐ │
                               │ │ [Category]  [Copy] │ │
                               │ └────────────────────┘ │
                               │                        │
                               │               [ + FAB] │ (Add Entry)
                               └────────────────────────┘
                                           │
                        ┌──────────────────┴──────────────────┐
                        ▼                                     ▼
             ┌─────────────────────┐               ┌─────────────────────┐
             │  EntryDetailScreen  │               │  ModalBottomSheet   │
             │ (Field-by-field,    │               │ (Password Gen,      │
             │  Edit, Delete)      │               │  Category Picker)   │
             └─────────────────────┘               └─────────────────────┘
```

---

## 3. Structural Hierarchy & Route Graph

MyVault uses a **flat, type-safe Jetpack Navigation Compose graph** with minimal stack depth:

```kotlin
NavHost(startDestination = "auth_lock") {
    composable("auth_lock")           // 1. Master PIN / Biometric verification
    composable("vault_home")          // 2. Main Vault Canvas (Search, Chips, Quick-Copy List)
    composable("entry_detail/{id}")   // 3. Detailed View (All fields, reveal, edit/delete)
    composable("add_edit_entry/{id}") // 4. Create or Edit Entry Form
    composable("settings")            // 5. App Settings (PIN change, Overlay modes, Wipe data)
}
```

### Contextual Overlays & Sheets (Non-Route Modals)
* **`ModalBottomSheet`**:
  * **Password Generator Sheet**: Configurable length/symbols, instant copy, and apply.
  * **Category Quick-Filter / Sort Sheet**: Sort by Name, Date Added, Most Used.
* **`AlertDialog`**:
  * **Destructive Confirmations**: Delete record, Clear database, Revoke permissions.
* **`Floating Window (OverlayService)`**:
  * Decoupled system overlay running independently via Android `WindowManager`.

---

## 4. Why This Navigation Model Wins

1. **Zero-Tap Discovery**: When the app unlocks, the user is immediately on the Vault Canvas with the search bar ready. No tab switching needed.
2. **Thumb-Zone Optimization**:
   - The Search Anchor is at the top of the content list but reachable without shifting grip.
   - The horizontal Category Filter Chips are positioned right beneath the search bar.
   - Every `EntryCard` places the Copy action on the right thumb edge.
   - The `FAB` (`+` Add) rests comfortably in the bottom-right corner.
3. **Deep Link & Overlay Synergy**:
   - The same flat architecture allows the Floating Overlay to reuse the exact same filtered state machine without running a full navigation stack.
4. **Predictable Back Navigation**:
   - `Back` from Detail -> Home.
   - `Back` from Settings -> Home.
   - `Back` from Home -> Closes app / moves task to back (does not get stuck in deep tab histories).

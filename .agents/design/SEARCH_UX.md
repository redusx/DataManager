# MyVault — Search Architecture & Home Canvas UX

## 1. Search Architecture Overview

Search is the primary discovery engine in MyVault. Because MyVault operates **100% offline**, search is performed completely in-memory against the decrypted cache in RAM, delivering **sub-16ms latency** on every keystroke.

---

## 2. Search Indexing & Field Matching

### A. Indexed (Searchable) Fields
* **Entry Title**: (e.g., `"Netflix"`, `"Garanti Bonus"`, `"Kendim TC"`) -> *Highest weight*.
* **Username / Email**: (e.g., `"ahmet@gmail.com"`, `"user123"`) -> *Medium weight*.
* **Service URL / App Name**: (e.g., `"netflix.com"`, `"github.com"`) -> *Medium weight*.
* **Category Name / Tags**: (e.g., `"Kartlar"`, `"İş"`, `"Finans"`) -> *Low weight*.
* **Notes**: Plaintext notes content.

### B. Strictly Non-Indexed (Excluded) Fields
* ❌ **Passwords & Secret Keys**: Never matched in search queries.
* ❌ **Full Credit Card Numbers & CVVs**: Never matched.
* ❌ **Full National IDs**: Never matched.
* *Rationale*: Searching through encrypted secrets creates memory exposure and potential shoulder-surfing leaks where typing a password fragment reveals which accounts use it.

---

## 3. Matching Algorithm & Ergonomics

* **Matching Strategy**: Case-insensitive substring matching (`contains(query, ignoreCase = true)`).
* **Turkish Character Normalization**: Seamless handling of Turkish characters (`ı/i`, `ş/s`, `ç/c`, `ğ/g`, `ü/u`, `ö/o`).
* **Debouncing**: Zero debounce needed for in-memory lists (<500 items). Immediate reactivity on every key event.

---

## 4. Search Privacy & History Policy

* **No Persistent Search History**: MyVault **never** writes search history to disk or preferences.
* *Why*: Storing recent search queries (e.g., `"Crypto"`, `"Tinder"`, `"Ziraat"`) creates a local digital footprint of what services the user possesses.
* *Session Behavior*: The search query lives purely in RAM within the `StateFlow` and clears automatically when the app is locked or closed.

---

## 5. Zero-Result State UX

When a search query yields no matches:

```
┌────────────────────────────────────────────────────────┐
│                        🔍                              │
│              Sonuç Bulunamadı                          │
│     "'garanti' aramasıyla eşleşen kayıt yok."          │
│                                                        │
│   [ Aramayı Temizle ]        [ + Yeni Kayıt Ekle ]     │
└────────────────────────────────────────────────────────┘
```

1. Clear, non-technical explanation showing the exact query.
2. Direct action to **Clear Search** (resets query with 1 tap).
3. Direct action to **Create New Entry** (pre-fills title with the search query).

---

## 6. Home / Vault Screen Model Comparison

We evaluated four models for the primary Vault Landing Canvas:

| Model | Mechanics | Speed to Copy | Discoverability | Verdict |
| :--- | :--- | :--- | :--- | :--- |
| **A) Search-First** | Screen is an empty search bar; records appear only after typing. | 🔴 Slow for browsing | 🔴 Low (user must remember what they have) | ❌ **Rejected** |
| **B) Category-First** | 4 large category folders; user must click a folder to see cards. | 🟡 Medium (Forces +1 tap for every action) | 🟢 Good | ❌ **Rejected** |
| **C) Recent-Items-First**| Shows only recent 5 items + "View All" button. | 🟢 Fast for top 3 items | 🔴 Poor for less frequent items | ❌ **Rejected** |
| **D) Hybrid Canvas** | **Search Anchor at Top + Horizontal Category Chips + Unified Categorized List with 1-Tap Copy.** | 🟢 **Optimal (<2s)** | 🟢 **Maximum** | 🏆 **RECOMMENDED** |

---

## 7. Recommended Home Canvas Architecture

```
┌───────────────────────────────────────────────────────────┐
│ [🛡️ MyVault]                     [🔒 Kilitle]  [⚙️ Ayarlar]│ (TopAppBar)
├───────────────────────────────────────────────────────────┤
│ ┌───────────────────────────────────────────────────────┐ │
│ │ 🔍  Kasada arayın...                             (X)  │ │ (Search Bar)
│ └───────────────────────────────────────────────────────┘ │
├───────────────────────────────────────────────────────────┤
│ ┌───────────────────────────────────────────────────────┐ │
│ │ 🚀  Yüzen Hızlı Erişimi Başlat                    >   │ │ (Overlay Card)
│ └───────────────────────────────────────────────────────┘ │
├───────────────────────────────────────────────────────────┤
│  [ (All) 24 ]  [ 🔐 Logins 12 ]  [ 💳 Cards 4 ]  [ 👤 ID 5]│ (Filter Chips)
├───────────────────────────────────────────────────────────┤
│ ┌───────────────────────────────────────────────────────┐ │
│ │ 🔐  Google Workspace                          [📋]    │ │
│ │     ahmet@company.com ••••••••••••                    │ │ (EntryCard)
│ └───────────────────────────────────────────────────────┘ │
│ ┌───────────────────────────────────────────────────────┐ │
│ │ 💳  Garanti Bonus                             [📋]    │ │
│ │     •••• •••• •••• 4242                               │ │ (EntryCard)
│ └───────────────────────────────────────────────────────┘ │
│                                                           │
│                                                 [ + FAB ] │ (Add Entry)
└───────────────────────────────────────────────────────────┘
```

### Why This Hybrid Model Delivers Superior UX:
1. **0-Tap Immediate Access**: Opening the app immediately presents the user's top entries with active 1-tap copy buttons.
2. **Instant Search Access**: The search bar is immediately interactive without opening a new screen.
3. **Instant 1-Tap Category Filtering**: Swapping between Logins and Cards takes a single thumb tap on the chips.
4. **Quick Overlay Launch**: One tap starts the floating button and minimizes MyVault right into the user's active workflow.

# MyVault — Secure Note Template Specification

## 1. Mental Model & Purpose
The **Secure Note Template** captures freeform private notes, crypto recovery seeds, master Wi-Fi keys, server access instructions, and secret snippets.
Users expect:
1. **Clean Paper / Text Editor Canvas**: An expansive, distraction-free writing surface with minimal administrative clutter.
2. **Monospace & Secret Toggle**: Option to render content in monospace font for code / seed keys.
3. **Instant 1-Tap Copy**: Quick button to copy the entire note to clipboard.

---

## 2. Visual Wireframe

```
┌────────────────────────────────────────────────────────┐
│  GÜVENLİ NOT EKLE                                      │
│                                                        │
│  ┌──────────────────────────────────────────────────┐  │
│  │ Not Başlığı                                      │  │
│  │ Kripto Cüzdan Kurtarma Anahtarları               │  │
│  └──────────────────────────────────────────────────┘  │
│                                                        │
│  NOT İÇERİĞİ                                           │
│  ┌──────────────────────────────────────────────────┐  │
│  │                                                  │  │
│  │ 1. apple 2. ocean 3. mountain 4. silver          │  │
│  │ 5. castle 6. river 7. mirror 8. forest           │  │
│  │ 9. solar 10. velvet 11. winter 12. harbor        │  │
│  │                                                  │  │
│  │                                                  │  │
│  │                                                  │  │
│  └──────────────────────────────────────────────────┘  │
│                                                        │
│  [ ＋ Etiket veya Kategori Ekle ]                     │
└────────────────────────────────────────────────────────┘
```

---

## 3. Fields & Progressive Disclosure

### Essential Fields
- `title`: Note Title (e.g. "Crypto Seed Backup", "Ev Wi-Fi Şifresi")
- `content`: Multiline rich/secret text content (Sensitive by default)

### Additional Fields
- `tags`: Tag labels for organization
- `is_monospace`: Monospace font toggle for code/keys

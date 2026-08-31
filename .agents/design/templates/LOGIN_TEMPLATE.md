# MyVault — Login & Account Template Specification

## 1. Mental Model & Purpose
The **Login Template** captures credentials for websites, applications, and digital services.
Instead of an empty database form, it offers:
1. **Popular Offline Service Quick-Pickers** (Google, GitHub, Apple, Microsoft, Instagram, Netflix, Spotify, Amazon, Twitter/X, Discord, Steam, LinkedIn). Tapping auto-fills the service name, website domain, and category icon.
2. **Dedicated Credential Grouping**: Username/Email, Password with built-in password generator, strength indicator, and visibility toggle.
3. **Progressive Additional Info**: 2FA recovery keys, custom notes.

---

## 2. Visual Wireframe

```
┌────────────────────────────────────────────────────────┐
│  HESAP / GİRİŞ EKLE                                    │
│                                                        │
│  HIZLI SERVİS SEÇİMİ (OFFLINE PRESETS)                 │
│  [ Google ] [ GitHub ] [ Apple ] [ Microsoft ] [ ＋ ]  │
│                                                        │
│  SERVİS VE BAŞLIK                                      │
│  ┌──────────────────────────────────────────────────┐  │
│  │ Servis Adı / Başlık                              │  │
│  │ Google Hesabım                                   │  │
│  └──────────────────────────────────────────────────┘  │
│                                                        │
│  GİRİŞ BİLGİLERİ                                       │
│  ┌──────────────────────────────────────────────────┐  │
│  │ E-Posta veya Kullanıcı Adı                       │  │
│  │ ahmet.yilmaz@gmail.com                           │  │
│  └──────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────┐  │
│  │ Şifre                                      👁 🎲 │  │
│  │ ••••••••••••••••••••                             │  │
│  └──────────────────────────────────────────────────┘  │
│  [■■■■■ Güçlü (16 karakter)]                           │
│                                                        │
│  WEB SİTESİ                                            │
│  ┌──────────────────────────────────────────────────┐  │
│  │ Web Sitesi / URL (İsteğe bağlı)                  │  │
│  │ accounts.google.com                              │  │
│  └──────────────────────────────────────────────────┘  │
│                                                        │
│  [ ＋ Ek Not ve Güvenlik Bilgisi Ekle ]                │
└────────────────────────────────────────────────────────┘
```

---

## 3. Fields & Progressive Disclosure

### Essential Fields (Visible by default)
- `title`: Service Name (e.g. "GitHub")
- `username`: Email or Username (e.g. "developer@gmail.com")
- `password`: Password (Masked, with generator `🎲` and reveal `👁`)
- `website`: Website URL (e.g. "github.com")

### Additional Fields (Progressive)
- `notes`: Security questions, recovery phrase, or context notes.

---

## 4. Viewing Representation (EntryDetail)
In `EntryDetailScreen`, logins are rendered as a clean, high-authority credential card with dedicated 1-tap copy buttons on both username and password.

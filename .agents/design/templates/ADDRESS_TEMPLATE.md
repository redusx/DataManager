# MyVault — Address & Contact Template Specification

## 1. Mental Model & Purpose
The **Address & Contact Template** captures physical delivery, residential, or billing addresses.
Users expect:
1. **Postal / Envelope Mental Layout**: Full open address text area combined with structured city, district, neighborhood, and postal code fields.
2. **Side-by-Side Co-Dependent Fields**: City + District and Neighborhood + Postal Code.
3. **Contact Integration**: Phone number and email linked to the address for one-stop delivery form completion.

---

## 2. Visual Wireframe

```
┌────────────────────────────────────────────────────────┐
│  ADRES EKLE                                            │
│                                                        │
│  ADRES TANIMI                                          │
│  ┌──────────────────────────────────────────────────┐  │
│  │ Adres Başlığı / Tanımı                           │  │
│  │ Ev Adresim                                       │  │
│  └──────────────────────────────────────────────────┘  │
│                                                        │
│  AÇIK ADRES                                            │
│  ┌──────────────────────────────────────────────────┐  │
│  │ Açık Adres (Cadde, Sokak, Bina, Daire)           │  │
│  │ Bağdat Cad. Nilüfer Sok. No:14 Daire:8           │  │
│  │                                                  │  │
│  └──────────────────────────────────────────────────┘  │
│                                                        │
│  KONUM BİLGİLERİ                                       │
│  ┌───────────────────────┐  ┌───────────────────────┐  │
│  │ İl / Şehir            │  │ İlçe                  │  │
│  │ İstanbul              │  │ Kadıköy               │  │
│  └───────────────────────┘  └───────────────────────┘  │
│  ┌───────────────────────┐  ┌───────────────────────┐  │
│  │ Mahalle               │  │ Posta Kodu            │  │
│  │ Caddebostan Mah.      │  │ 34728                 │  │
│  └───────────────────────┘  └───────────────────────┘  │
│                                                        │
│  [ ＋ İletişim Bilgisi Ekle (Telefon, Alıcı İsmi) ]   │
└────────────────────────────────────────────────────────┘
```

---

## 3. Fields & Progressive Disclosure

### Essential Fields
- `title`: Address Nickname (e.g. "Ev", "Ofis")
- `address`: Full multi-line street address
- `city`: City (İl)
- `district`: District (İlçe)
- `postal_code`: Postal / ZIP code

### Additional Fields
- `neighborhood`: Mahalle / Semt
- `recipient_name`: Contact person name
- `phone`: Contact telephone number
- `notes`: Delivery directions / door code

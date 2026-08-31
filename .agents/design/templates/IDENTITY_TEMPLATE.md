# MyVault — Identity Document Template Specification

## 1. Mental Model & Purpose
The **Identity Document Template** captures national identity numbers (e.g. T.C. Kimlik No), passports, driving licenses, and official personal identification data.
Users expect:
1. **Official Document Representation**: Clear segregation of personal identity from contact and peripheral records.
2. **Standardized Identity Masking**: TC Kimlik numbers are 11 digits and masked (`123•••••89`) with instant 1-tap copy.
3. **Structured Grouping**: Full Name, National ID Number, Birth Date, Serial Number.

---

## 2. Visual Wireframe

```
┌────────────────────────────────────────────────────────┐
│  KİMLİK BELGESİ EKLE                                   │
│                                                        │
│  BELGE TÜRÜ                                            │
│  [ T.C. Kimlik ] [ Pasaport ] [ Sürücü Belgesi ]      │
│                                                        │
│  KİMLİK BİLGİLERİ                                      │
│  ┌──────────────────────────────────────────────────┐  │
│  │ Belge Başlığı (örn. Kendim - TC Kimlik)          │  │
│  │ T.C. Kimlik Kartım                               │  │
│  └──────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────┐  │
│  │ T.C. Kimlik / Belge No                   👁 ⧉   │  │
│  │ ••••••••••• (11 hane)                            │  │
│  └──────────────────────────────────────────────────┘  │
│  ┌──────────────────────────────────────────────────┐  │
│  │ Ad Soyad                                         │  │
│  │ Ahmet Yılmaz                                     │  │
│  └──────────────────────────────────────────────────┘  │
│  ┌───────────────────────┐  ┌───────────────────────┐  │
│  │ Doğum Tarihi (GG.AA.YY│  │ Seri No               │  │
│  │ 15.04.1990            │  │ A12B34567             │  │
│  └───────────────────────┘  └───────────────────────┘  │
│                                                        │
│  [ ＋ Ek Bilgiler Ekle (Geçerlilik Tarihi, Not) ]      │
└────────────────────────────────────────────────────────┘
```

---

## 3. Fields & Progressive Disclosure

### Essential Fields
- `title`: Identity Title (e.g. "Ahmet - T.C. Kimlik")
- `id_number`: 11-digit national identity number or passport number (Sensitive)
- `full_name`: First and Last name
- `birth_date`: Date of birth
- `serial_number`: Document serial number

### Additional Fields
- `expiry_date`: Document valid until date
- `issue_date`: Issue date
- `notes`: Custom identity notes

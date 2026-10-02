# PRD: ShopiKu — Marketplace Mobile (Clone & Redesign)

**Mata Kuliah:** Pemrograman Mobile Dasar, STMIK IKMI Cirebon (Semester Ganjil 2026/2027)
**Platform:** Android (Kotlin, Android Studio) | **Backend:** Supabase (REST API, live cloud)
**Versi:** 1.0 | **Status:** Draft siap eksekusi

---

## 1. Ringkasan Produk
ShopiKu adalah aplikasi e-commerce Android dengan alur transaksi lengkap: katalog → pencarian → detail & varian → keranjang (CRUD) → checkout → pembayaran sandbox → tracking. Tampilan **wajib di-redesign** (bersih, modern, unik), bukan jiplakan visual Shopee. Data **100% live dari backend cloud**, bukan mock offline.

**Pilih satu tema komoditas** (contoh: Sneakers, Fashion Muslim, Aksesoris Gadget, Buku, atau Makanan Khas Cirebon). Tema memengaruhi varian produk (warna/ukuran/rasa).

## 2. Tujuan & Non-Tujuan
**Tujuan**
- G1: 7 tahap alur wajib berfungsi end-to-end tanpa crash.
- G2: Terhubung ke backend cloud aktif via Retrofit.
- G3: Arsitektur MVVM rapi + error handling koneksi yang elegan.
- G4: 12 layar dengan identitas visual sendiri.
- G5: Deliverable lengkap (GitHub, APK, video, laporan).

**Non-Tujuan:** payment gateway asli, chat penjual, sistem multi-seller, notifikasi push, fitur live streaming.

## 3. Pengguna
**Persona:** Pembeli muda (18–30 th) yang belanja dari smartphone, mengharapkan pencarian cepat, proses checkout singkat, dan status pesanan jelas.
**Penguji (dosen):** menilai lewat APK di HP fisik + video demo.

## 4. Kebutuhan Fungsional

| ID | Fitur | Prioritas | Kriteria Penerimaan |
|----|-------|-----------|---------------------|
| FR-01 | Katalog produk (Beranda & Daftar, grid 2 kolom) | Wajib | Memuat gambar, nama, harga Rp, rating bintang dari API; ada loading, empty, dan error state |
| FR-02 | Pencarian | Wajib | Hasil terfilter saat mengetik (debounce ±300 ms), tanpa reload layar total; param `search` ke API |
| FR-03 | Detail produk & varian | Wajib | Deskripsi, ulasan, pilih varian (warna/ukuran); varian wajib dipilih sebelum tambah keranjang |
| FR-04 | Tambah ke keranjang | Wajib | `POST /api/cart`; badge jumlah keranjang ter-update |
| FR-05 | Keranjang CRUD | Wajib | Tombol [-]/[+] (`PUT`), hapus item (`DELETE`), total ter-update otomatis; qty minimum 1 |
| FR-06 | Checkout | Wajib | Alamat tujuan (Kota Cirebon), pengiriman Reguler/Express, hitung subtotal + ongkir = total |
| FR-07 | Pembayaran sandbox | Wajib | Tampilkan VA BCA `123 456 7890`, countdown timer, tombol "Cek Status Pembayaran" → status Lunas |
| FR-08 | Tracking pesanan | Wajib (layar 10) | Timeline status (Dikemas → Dikirim → Tiba) dan info kurir |
| FR-09 | Login/Register | Wajib (layar 2) | Email + password (Supabase Auth); sesi tersimpan |
| FR-10 | Profil/Akun | Wajib (layar 11) | Menu pesanan, alamat, logout |
| FR-11 | Ulasan produk | Wajib (layar 12) | Tampil ringkasan rating dan daftar ulasan; form tulis ulasan |
| FR-12 | Kategori | Wajib (layar 4) | Grid kategori; klik membuka daftar produk terfilter |
| FR-13 | Login Google/Facebook | Opsional | Tombol tampil; fungsional bila waktu cukup |

## 5. Spesifikasi 12 Layar

| # | Layar | Komponen Utama | Catatan Redesign |
|---|-------|----------------|------------------|
| 1 | Splash | Logo, tagline, progress | Warna brand sendiri (bukan oranye Shopee persis) |
| 2 | Login/Register | Tab, form, tombol sosial | Validasi input inline |
| 3 | Beranda | Search bar, banner carousel, kategori, flash sale, bottom nav | Flash sale opsional dengan countdown |
| 4 | Kategori | Grid ikon kategori | |
| 5 | Daftar Produk | Tab sub-kategori, grid 2 kolom, filter | RecyclerView + GridLayoutManager |
| 6 | Detail Produk | Galeri, harga, varian, qty, tombol Keranjang/Beli | Bottom sheet untuk pilih varian |
| 7 | Keranjang | List item, checkbox, [-]/[+], hapus, total | Swipe-to-delete (bonus) |
| 8 | Checkout | Alamat, pengiriman, metode bayar, ringkasan | |
| 9 | Pembayaran | VA BCA, countdown, tombol cek status | Tombol salin nomor VA |
| 10 | Tracking | Stepper status, info kurir | |
| 11 | Profil | Header user, menu list | |
| 12 | Ulasan | Rating rata-rata, distribusi bintang, daftar ulasan | |

**Strategi redesign:** palet 1 warna primer + 1 aksen (kontras WCAG AA), tipografi konsisten (1 font family), sudut membulat seragam, spacing grid 8dp, ikon outline konsisten, dan Material 3. Dokumentasikan perbandingan "sebelum/sesudah" di laporan.

## 6. Arsitektur Teknis

**Stack:** Kotlin, XML layout + ViewBinding, Material 3, Retrofit + OkHttp + Gson/Moshi, Coroutines + Flow/LiveData, Glide/Coil, Navigation Component, Hilt/Koin (opsional), DataStore (token & sesi).

**Pola:** MVVM + Repository

```
app/
├── data/
│   ├── remote/   ShopeeApiService, RetrofitClient, dto/
│   ├── repository/  ProductRepository, CartRepository, OrderRepository, AuthRepository
│   └── model/    Product, CartItem, Order, Review, User
├── ui/
│   ├── splash/ auth/ home/ category/ product/ detail/
│   ├── cart/ checkout/ payment/ tracking/ profile/ review/
│   └── common/   adapters, state wrappers
├── util/         Resource<T>, Formatter (Rp), Extensions
└── MainActivity.kt
```

**Alur data:** `Fragment → ViewModel → Repository → ApiService → Supabase`
**State UI:** `sealed class Resource<T> { Loading, Success, Error }` dipakai semua ViewModel.

## 7. Backend & Model Data (Supabase)

**Tabel:**
- `products(id, name, description, price, image_url, category_id, rating, stock, variants jsonb)`
- `categories(id, name, icon_url)`
- `cart(id, user_id, product_id, variant, quantity, created_at)`
- `orders(id, user_id, address, shipping_type, shipping_cost, total, status, va_number, expired_at, created_at)`
- `order_items(id, order_id, product_id, variant, quantity, price)`
- `reviews(id, product_id, user_id, rating, comment, created_at)`

**Status pesanan:** `PENDING_PAYMENT → PAID → PACKED → SHIPPED → DELIVERED`

**Catatan:** endpoint kustom (`/api/checkout`, `/api/payment/simulate`) diimplementasikan lewat Supabase Edge Function / RPC, atau opsi Vercel Serverless bila perlu logika khusus. Aktifkan Row Level Security dan jangan menaruh service key di APK (gunakan anon key).

## 8. Spesifikasi API

| Method | Endpoint | Fungsi |
|--------|----------|--------|
| GET | `/api/products?search=` | Daftar & pencarian produk |
| POST | `/api/cart` | Tambah item keranjang |
| PUT | `/api/cart/{id}` | Ubah kuantitas (`{"quantity": 2}`) |
| DELETE | `/api/cart/{id}` | Hapus item |
| POST | `/api/checkout` | Buat pesanan, kembalikan `order_id` & VA |
| POST | `/api/payment/simulate?order_id=` | Simulasi pelunasan |

**Contoh JSON**
```json
// POST /api/cart
{ "product_id": "p123", "variant": "Hitam / 42", "quantity": 1 }

// POST /api/checkout
{ "address": "Jl. Contoh No. 1, Cirebon", "shipping": "REGULER",
  "items": [{ "cart_id": "c1" }] }

// Response checkout
{ "order_id": "o789", "total": 5213000,
  "va_number": "123 456 7890", "expired_at": "2026-12-01T10:00:00Z" }
```
Perhitungan: `total = Σ(harga × qty) + ongkir` (Reguler Rp15.000, Express Rp25.000, nilai dapat disesuaikan).

## 9. Penanganan Error & Edge Case
- Tanpa internet: pesan ramah + tombol "Coba Lagi" (cek `ConnectivityManager`).
- Timeout/5xx/4xx: map ke pesan yang manusiawi; jangan tampilkan stacktrace.
- Keranjang kosong: empty state dengan ajakan belanja; checkout dinonaktifkan.
- Stok habis / varian belum dipilih: validasi sebelum request.
- Double-tap: nonaktifkan tombol saat request berjalan.
- VA kedaluwarsa: status "Kedaluwarsa" saat countdown 0.
- Cold start backend (hosting free tier): tampilkan loading yang jelas, timeout OkHttp ≥ 30 dtk.

## 10. Kebutuhan Non-Fungsional
- **Kinerja:** daftar produk tampil < 3 dtk pada jaringan normal; gunakan paging/caching gambar.
- **Kompatibilitas:** minSdk 24, target SDK terbaru; diuji di HP fisik dan minimal 2 ukuran layar.
- **Keamanan:** HTTPS, API key di `local.properties`/BuildConfig (tidak di-commit), RLS aktif.
- **Kualitas kode:** konvensi Kotlin, tanpa logika bisnis di Fragment/Activity.

## 11. Rencana Pengerjaan (Milestone)

| Minggu | Fokus | Output |
|--------|-------|--------|
| 1 | Setup & desain | Project, tema/design tokens, backend & seed data (≥ 20 produk) |
| 2 | Layar statis | Splash, Login, Beranda, Kategori, Daftar, Detail (XML) |
| 3 | Integrasi katalog | Retrofit, Products, Search, Detail + varian |
| 4 | Keranjang & Checkout | Cart CRUD, Checkout, Pembayaran sandbox |
| 5 | Pelengkap | Tracking, Profil, Ulasan, Auth |
| 6 | Finalisasi | Error handling, polishing UI, APK, video, README, laporan |

## 12. Deliverable
1. Repo GitHub publik + `README.md` (deskripsi, screenshot, cara build, konfigurasi backend, arsitektur).
2. File APK (debug/release) yang berjalan di HP fisik.
3. Video demo 2–4 menit: alur lengkap sampai pembayaran sandbox, **dengan facecam & suara asli**.
4. Laporan (Word/PDF) dengan Tabel Identitas Mahasiswa di halaman muka.

## 13. Pemetaan Rubrik

| Kriteria | Bobot | Cara Memenuhi |
|----------|-------|---------------|
| UI/UX & Redesign | 20% | 12 layar konsisten, design tokens, bukan jiplakan |
| Fungsionalitas CRUD & Sandbox | 25% | Checklist FR-01 s.d. FR-07 lulus uji |
| Integrasi Live Backend | 25% | Semua data dari Supabase, tanpa data hardcode |
| Arsitektur MVVM & Error | 10% | Struktur §6, `Resource<T>`, error state §9 |
| Dokumentasi, Video, Repo | 20% | Deliverable §12 lengkap dan rapi |

## 14. Checklist Uji Penerimaan (UAT)
- [ ] Produk tampil dari cloud (gambar, nama, Rp, bintang)
- [ ] Search menyaring instan
- [ ] Detail memuat deskripsi, ulasan, varian
- [ ] Tambah keranjang tersimpan di database
- [ ] [+]/[-]/hapus memperbarui DB dan total
- [ ] Checkout menghitung total benar (alamat Cirebon, Reguler/Express)
- [ ] VA, countdown, dan "Cek Status" menghasilkan status Lunas
- [ ] Mode pesawat → pesan error elegan, tanpa crash
- [ ] APK terpasang dan berjalan di HP fisik

## 15. Risiko & Mitigasi

| Risiko | Mitigasi |
|--------|----------|
| Backend lambat/tidur (free tier) | Pilih Supabase; loading state; uji sebelum rekaman |
| Endpoint kustom rumit | Mulai dari tabel + RPC/Edge Function sederhana |
| Waktu mepet | Kerjakan urut prioritas Wajib; FR-13 opsional di akhir |
| UI terlalu mirip Shopee | Tetapkan palet & komponen sendiri sejak minggu 1 |
| Kebocoran API key | Gunakan anon key + RLS, jangan commit rahasia |

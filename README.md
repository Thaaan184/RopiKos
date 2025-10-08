<p align="center">
  <img src="https://github.com/Thaaan184/RopiKos/blob/master/public/Logo.png?raw=true" alt="RopiKost Logo" width="120"/>
</p>

<h1 align="center">🏠 RopiKost Manager (RKM)</h1>
<p align="center">
  <b>Prototipe Aplikasi Manajemen Keuangan & Pengelolaan Kost</b><br/>
  <i>Dokumen Spesifikasi Kebutuhan Perangkat Lunak (SRS)</i><br/>
  <i>Versi 1.0 — 07 Oktober 2025</i>
</p>

---

## 📘 Tentang Proyek

**RopiKost Manager (RKM)** adalah aplikasi mobile berbasis Android yang dirancang untuk membantu pemilik kos dalam mengelola data kamar, penyewa, tagihan, dan laporan keuangan dengan lebih efisien.  
Aplikasi ini mengadopsi pendekatan **offline-first**, memastikan fungsi utama tetap berjalan tanpa koneksi internet.  

🧩 **Status:** Prototipe tahap pengembangan UI  
🎓 **Tujuan:** Pemenuhan tugas **UTS Pemrograman Platform**  
👥 **Tim Pengembang:**
- 2210511128 — *Nabil Ihsan Syakir*  
- 2210511138 — *Muammar Faiz K. A. S.*  
- 2210511140 — *Muhammad Sabil Nur Raihan*  
- 2210511156 — *Bagas Abiyyu Rachman*  
- 2210511164 — *Muhammad Fathan Abriyanto*  

---

## 🎯 Tujuan & Ruang Lingkup

Dokumen ini menjelaskan kebutuhan sistem **RopiKost Manager (RKM)** secara menyeluruh, mencakup fungsi utama, batasan, dan karakteristik teknis untuk acuan pengembangan.  
Tujuan utama:
- Menyediakan dokumentasi teknis sistem manajemen kost.  
- Menjadi panduan bagi pengembang, penguji, dan pengguna dalam implementasi dan evaluasi aplikasi.  
- Membantu proses pengembangan agar lebih **terstruktur, efisien, dan terdokumentasi**.

---

## 🧠 Deskripsi Produk

RKM berfungsi sebagai alat bantu digital untuk menggantikan proses manual pengelolaan kos, seperti pencatatan penyewa, pembayaran sewa, dan pelaporan keuangan.  
Fitur utama:
- 🔐 Login & Otentikasi pengguna  
- 🏠 Manajemen kamar & tarif sewa  
- 👥 Pengelolaan data penyewa  
- 💳 Pencatatan tagihan dan status pembayaran  
- 🔧 Catatan biaya maintenance  
- 📊 Laporan keuangan bulanan & kuartalan  
- 🔔 Notifikasi pengingat pembayaran  
- 💾 Penyimpanan data lokal dengan **SQLite**

---

## 👥 Karakteristik Pengguna

| Kategori | Tugas Utama | Hak Akses | Kemampuan Diperlukan |
|-----------|-------------|-----------|-----------------------|
| **Pemilik Kos** | Mengelola kamar, penyewa, tagihan, dan laporan | CRUD Data | Dasar pengoperasian Android, pemahaman administrasi kos |

---

## ⚙️ Lingkungan Operasi

| Jenis | Spesifikasi Minimum |
|-------|----------------------|
| **Platform** | Android 7.0 (Nougat) atau lebih baru |
| **RAM** | ≥ 2 GB |
| **CPU** | 4-Core |
| **Penyimpanan Kosong** | ≥ 512 MB |
| **Database** | SQLite (lokal, offline-first) |
| **Bahasa Pemrograman** | Java |
| **IDE Pengembangan** | Android Studio |

---

## 🧩 Batasan Desain & Implementasi

- **Arsitektur:** Native Android (MVC Pattern)  
- **Metode CRUD:** Create, Read, Update, Delete melalui SQLite  
- **Akses:** Lokal & personal, tanpa server eksternal  
- **Keamanan:** Data disimpan secara lokal dengan proteksi SharedPreferences  

---

## 💻 Kebutuhan Fungsional (Functional Requirements)

| ID | Kebutuhan | Deskripsi |
|----|------------|-----------|
| FR-01 | Splash Screen | Menampilkan logo dan memuat data awal |
| FR-02 | Login | Autentikasi pengguna untuk akses aplikasi |
| FR-04 | Dashboard Statistik | Ringkasan total kamar, penyewa, dan pendapatan |
| FR-05–07 | CRUD Kamar | Tambah, ubah, hapus data kamar |
| FR-08–10 | CRUD Penyewa | Tambah, ubah, hapus data penyewa |
| FR-11 | Pencatatan Pembayaran | Tandai status lunas/belum lunas |
| FR-12 | Maintenance | Catat perbaikan kamar |
| FR-13 | Navigasi & Laporan | Menu Dashboard, Kamar, Penyewa, Keuangan |
| FR-15 | Penyimpanan Lokal | Semua data tersimpan di SQLite |
| FR-16 | Pembaruan Statistik | Dashboard diperbarui otomatis setiap perubahan data |

---

## 🧮 Kebutuhan Non-Fungsional (Non-Functional Requirements)

| ID | Parameter | Kebutuhan |
|----|------------|------------|
| NFR-01 | Availability | Minimal 99% waktu operasional tanpa crash |
| NFR-02 | Reliability | Data tidak hilang/duplikat setelah restart |
| NFR-03 | Ergonomy | UI intuitif, mengikuti pedoman Material Design |
| NFR-04 | Portability | Kompatibel Android 7.0+ |
| NFR-05 | Memory | Maksimal ukuran APK 100 MB |
| NFR-06 | Response Time | Respon tiap aksi < 2 detik |
| NFR-08 | Security | Data login terenkripsi dan auto logout 10 menit |
| NFR-09 | Maintainability | Struktur kode berbasis MVC |
| NFR-10 | Localization | Bahasa Indonesia |
| NFR-11 | Backup | Ekspor data ke CSV/PDF |
| NFR-12 | Scalability | Mendukung ≥ 500 entri data tanpa lag |

---

## 📊 Diagram dan Model Sistem

- **Use Case Diagram:**  
  Menggambarkan interaksi pengguna dengan fitur utama (Login, CRUD Data, Pembayaran, Laporan).  
- **Class Diagram:**  
  Menunjukkan hubungan antara entitas utama: `Kamar`, `Penyewa`, `Tagihan`, `Laporan`.  

*(Diagram disertakan pada dokumen SRS utama, bukan pada README ini.)*

---

## 🧱 Status Proyek

> ⚠️ **Catatan:**  
> Aplikasi **RopiKost Manager (RKM)** masih berupa **prototipe tahap pengembangan antarmuka (UI Prototype)**  
> dan belum mengimplementasikan seluruh logika sistem.  
> Tujuan pengembangan: **Tugas UTS Pemrograman Platform (Oktober 2025).**

---

## 🏁 Lisensi
Proyek ini bersifat **Open Source** untuk tujuan pembelajaran dan pengembangan akademik.  
© 2025 **RopiKost Manager Development Team**

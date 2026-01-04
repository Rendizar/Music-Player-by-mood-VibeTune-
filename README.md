VibeTune: Music Player by Mood

VibeTune adalah aplikasi pemutar musik Android yang dirancang untuk memberikan pengalaman mendengarkan yang unik dengan membuat playlist yang dipersonalisasi berdasarkan suasana hati pengguna. 
Cukup tuliskan apa yang Anda rasakan, dan VibeTune akan menyusun soundtrack yang sempurna untuk Anda.

Overview: 
<img width="1080" height="2400" alt="Screenshot_20260104_151507" src="https://github.com/user-attachments/assets/8f110d46-1ccd-4702-8554-70b94893b7e7" />
<img width="1080" height="2400" alt="Screenshot_20260104_151443" src="https://github.com/user-attachments/assets/625343e9-d39a-4c23-a4b7-a3096b620539" />
<img width="1080" height="2400" alt="Screenshot_20260104_151420" src="https://github.com/user-attachments/assets/035e8899-6053-4422-90f8-bf5697e15275" />
<img width="1080" height="2400" alt="Screenshot_20260104_151342" src="https://github.com/user-attachments/assets/142035bf-6488-47e8-952b-e8518c85246a" />

Fitur Utama : 

	•Analisis Mood: Secara cerdas menghasilkan playlist dengan menganalisis teks yang dimasukkan oleh pengguna
	•Playlist Dinamis: Mendukung playlist berdasarkan mood, hasil pencarian, dan daftar favorit pribadi
	•Pemutar Musik Fungsional: Kontrol pemutar standar termasuk Play, Pause, Next, Previous, dan progress bar.•Favorit Lokal: Simpan dan kelola lagu favorit Anda secara persisten langsung di perangkat
	•Pencarian: Temukan lagu atau artis dari seluruh perpustakaan musik yang tersedia.•Bagikan: Bagikan lagu yang sedang Anda dengarkan dengan teman-teman

Arsitektur & Teknologi yang Digunakan Aplikasi ini dibangun menggunakan praktik pengembangan Android modern dengan arsitektur yang memisahkan antara data, UI, dan logika bisnis.

	•Bahasa: Kotlin
	•UI: XML Layouts dengan Material Design Components.•Arsitektur: Mengikuti prinsip-prinsip arsitektur modern Android (UI Layer, Data Layer)
	•Asynchronous: Kotlin Coroutines untuk mengelola tugas di latar belakang seperti pemanggilan API dan akses database.•Networking: Retrofit & OkHttp untuk komunikasi yang efisien dan bersih dengan API
	•JSON Parsing: Gson untuk mengubah data JSON dari API menjadi objek Kotlin.•Image Loading: Glide untuk memuat, menyimpan (cache), dan menampilkan gambar sampul album secara efisien
	•Database Lokal: Room Persistence Library untuk menyimpan daftar lagu favorit pengguna secara lokal.•Backend: Aplikasi ini terhubung ke API Node.js kustom.•Database Cloud: API backend menggunakan Firebase Firestore sebagai database utama untuk menyimpan 	dan mengelola perpustakaan musik

Cara Menjalankan ProyekUntuk menjalankan proyek ini di lingkungan lokal Anda, ikuti langkah-langkah berikut:

	1.Clone repository ini:Kotlingit clone https://github.com/Rendizar/Music-Player-by-mood-VibeTune-.git
	2.Buka proyek menggunakan Android Studio.
	3.Biarkan Gradle melakukan sinkronisasi untuk mengunduh semua dependensi yang diperlukan. 
	4.Penting: Proyek ini memerlukan instance backend API VibeTune yang sedang berjalan. Pastikan API tersebut aktif dan aplikasi telah dikonfigurasi dengan BASE_URL yang benar di file ApiClient.kt.5.Build dan jalankan aplikasi pada emulator atau perangkat Android fisik.<img width="1080" height="2400" alt="Screenshot_20260104_151507" src="https://github.com/user-attachments/assets/e0be0961-30da-4fb6-9a18-7a306ccdd496" />



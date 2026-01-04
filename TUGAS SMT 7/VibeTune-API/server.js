/**
 * =========================================================
 * VibeTune API – Firebase Backend untuk Android App
 * Tech: Node.js + Express + Firebase
 * =========================================================
 */

require('dotenv').config();
const express = require('express');
const cors = require('cors');
const app = express();
const port = process.env.PORT || 3000;

// Import Firebase config dan routes
const { db } = require('./config/firebase');
const songsRoute = require('./routes/songs');

// Middleware
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// CORS - Allow requests dari Android app
app.use(cors({
  origin: '*', // Atau specify IP Android emulator: ['http://10.0.2.2:3000']
  methods: ['GET', 'POST', 'PUT', 'DELETE'],
  allowedHeaders: ['Content-Type', 'Authorization']
}));

/**
 * =========================================================
 * HELPER: GENERATE MANY SONGS (untuk seed ke Firestore)
 * =========================================================
 */
function generateSongs({ mood, prefix, count, imageSeed }) {
  const songs = [];

  for (let i = 1; i <= count; i++) {
    songs.push({
      title: `${mood} Track ${i}`,
      artist: "VibeTune Studio",
      mood: mood,
      albumArtUrl: `https://picsum.photos/seed/${imageSeed}${i}/400/400`,
      previewUrl: `https://www.soundhelix.com/examples/mp3/SoundHelix-Song-${(i % 16) + 1}.mp3`
    });
  }

  return songs;
}

/**
 * =========================================================
 * API ENDPOINTS
 * =========================================================
 */

// Health check / Welcome endpoint
app.get('/', (req, res) => {
  res.json({
    message: "🎵 VibeTune API – Firebase Backend",
    version: "2.0.0",
    status: "ready",
    baseUrl: `http://localhost:${port}`,
    documentation: {
      moods: "GET /api/songs/mood/:mood",
      search: "GET /api/songs/search?q=query",
      allSongs: "GET /api/songs?limit=20&offset=0",
      song: "GET /api/songs/:id",
      addSong: "POST /api/songs"
    }
  });
});

// Mount routes
app.use('/api/songs', songsRoute);

// Health check untuk Android
app.get('/health', (req, res) => {
  res.json({ status: 'ok', timestamp: new Date().toISOString() });
});

/**
 * =========================================================
 * API ENDPOINT UNTUK FEATURED CONTENT (HOME SCREEN) - INI YANG DITAMBAHKAN
 * =========================================================
 */
app.get('/featured', (req, res) => {
  // Untuk saat ini, kita gunakan data contoh (hardcoded).
  // Nantinya, data ini bisa Anda ambil dari koleksi lain di Firestore.
  const featuredData = {
    banner: {
      title: "Pilihan Editor Minggu Ini",
      description: "Lagu-lagu terbaik yang dikurasi khusus untuk menemani harimu.",
      imageUrl: "https://picsum.photos/seed/banner-vibetune/800/400"
    },
    topMixes: [
      {
        title: "Daily Mix",
        imageUrl: "https://picsum.photos/seed/mix1/400/400"
      },
      {
        title: "Lagi Viral",
        imageUrl: "https://picsum.photos/seed/mix2/400/400"
      },
      {
        title: "Rilisan Baru",
        imageUrl: "https://picsum.photos/seed/mix3/400/400"
      },
      {
        title: "Musik Kerja",
        imageUrl: "https://picsum.photos/seed/mix4/400/400"
      }
    ]
  };

  res.status(200).json(featuredData);
});


/**
 * =========================================================
 * START SERVER
 * =========================================================
 */
app.listen(port, '0.0.0.0', () => {
  console.log(`
╔════════════════════════════════════════╗
║   🎵 VibeTune API - Firebase Backend   ║
║   Server running on port ${port}              ║
║   http://localhost:${port}              ║
║   http://0.0.0.0:${port} (Network)    ║
╚════════════════════════════════════════╝

📱 Untuk Android Emulator:
   http://10.0.2.2:${port}

📚 API Documentation:
   GET  /                          - Welcome
   GET  /health                    - mood check
   GET  /featured                  - Featured Content (BARU)
   GET  /api/songs                 - Get all songs
   GET  /api/songs/mood/:mood      - Get by mood
   GET  /api/songs/:id             - Get by ID
   GET  /api/songs/search?q=query  - Search
   POST /api/songs                 - Add song

🔥 Firebase connected: ${db ? 'Yes ✅' : 'No ❌'}
  `);
});
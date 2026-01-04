# 🎵 VibeTune API – Firebase Backend

API backend untuk Android app VibeTune. Connect ke Firebase Firestore untuk manage songs data real-time.

## 📋 Struktur Folder

```
VibeTune-API/
├── config/
│   └── firebase.js          # Firebase Admin SDK init
├── services/
│   └── songService.js       # Database operations
├── routes/
│   └── songs.js             # API endpoints
├── .env                     # Environment variables (create from .env.example)
├── .env.example             # Template
├── .gitignore               # Git ignore rules
├── package.json
├── server.js                # Main server
└── serviceAccountKey.json   # Firebase credentials (KEEP SECRET!)
```

## 🚀 Setup Firebase

### 1. Buat Firebase Project
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project"
3. Fill in project details, create project
4. Enable Firestore Database

### 2. Download Service Account Key
1. Di Firebase Console, go to **Project Settings** (⚙️ icon)
2. Tab "Service Accounts"
3. Click "Generate new private key"
4. Save file sebagai `serviceAccountKey.json` di root folder project

### 3. Setup Environment Variables
```bash
cp .env.example .env
```

Edit `.env`:
```
PORT=3000
FIREBASE_PROJECT_ID=your-project-id-here
```

### 4. Install Dependencies
```bash
npm install
```

### 5. Start Server
```bash
node server.js
```

Server akan running di:
- **Local**: `http://localhost:3000`
- **Network**: `http://0.0.0.0:3000`
- **Android Emulator**: `http://10.0.2.2:3000`

## 📡 API Endpoints (untuk Android)

### 1. Get Songs by Mood
```
GET /api/songs/mood/:mood
Example: /api/songs/mood/happy
```

Response:
```json
{
  "success": true,
  "mood": "happy",
  "count": 30,
  "data": [
    {
      "id": "song-id-1",
      "title": "Happy Track 1",
      "artist": "VibeTune Studio",
      "mood": "happy",
      "albumArtUrl": "...",
      "previewUrl": "...",
      "createdAt": {...},
      "updatedAt": {...}
    }
  ]
}
```

### 2. Search Songs
```
GET /api/songs/search?q=query
Example: /api/songs/search?q=relax
```

### 3. Get All Songs (Paginated)
```
GET /api/songs?limit=20&offset=0
```

### 4. Get Single Song
```
GET /api/songs/:id
Example: /api/songs/song-id-1
```

### 5. Add New Song
```
POST /api/songs
Content-Type: application/json

{
  "title": "My Song",
  "artist": "Artist Name",
  "mood": "happy",
  "albumArtUrl": "https://...",
  "previewUrl": "https://..."
}
```

### 6. Update Song
```
PUT /api/songs/:id
Content-Type: application/json

{
  "title": "Updated Title"
}
```

### 7. Delete Song
```
DELETE /api/songs/:id
```

## 📱 Android Implementation Example

### Retrofit Setup
```kotlin
val apiService = Retrofit.Builder()
    .baseUrl("http://10.0.2.2:3000/")  // Android emulator
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(ApiService::class.java)
```

### Get Songs by Mood
```kotlin
apiService.getSongsByMood("happy")
    .enqueue(object : Callback<SongsResponse> {
        override fun onResponse(
            call: Call<SongsResponse>,
            response: Response<SongsResponse>
        ) {
            if (response.isSuccessful) {
                val songs = response.body()?.data
                // Update UI dengan songs
            }
        }

        override fun onFailure(call: Call<SongsResponse>, t: Throwable) {
            Log.e("API Error", t.message ?: "Unknown error")
        }
    })
```

### Interface Definition
```kotlin
interface ApiService {
    @GET("api/songs/mood/{mood}")
    fun getSongsByMood(@Path("mood") mood: String): Call<SongsResponse>

    @GET("api/songs/search")
    fun searchSongs(@Query("q") query: String): Call<SongsResponse>

    @GET("api/songs")
    fun getAllSongs(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Call<SongsResponse>

    @GET("api/songs/{id}")
    fun getSong(@Path("id") id: String): Call<SongResponse>

    @POST("api/songs")
    fun addSong(@Body song: Song): Call<SongResponse>

    @PUT("api/songs/{id}")
    fun updateSong(@Path("id") id: String, @Body song: Song): Call<SongResponse>

    @DELETE("api/songs/{id}")
    fun deleteSong(@Path("id") id: String): Call<SuccessResponse>
}
```

## 🔥 Firebase Firestore Structure

Collection: `songs`

Document schema:
```
{
  "title": "String",
  "artist": "String",
  "mood": "String",  // happy, sad, calm, energetic
  "albumArtUrl": "String",
  "previewUrl": "String",
  "createdAt": "Timestamp",
  "updatedAt": "Timestamp"
}
```

## 🐛 Troubleshooting

### Firebase connection error
- Check if `serviceAccountKey.json` exists
- Verify `FIREBASE_PROJECT_ID` di `.env`
- Check Firebase rules di Firestore

### Android can't connect to API
- Use `http://10.0.2.2:3000` (bukan localhost)
- Check if firewall allows port 3000
- Verify server running: `curl http://localhost:3000/health`

### CORS error
- Sudah di-setup di `server.js`
- Verify `Origin` header di request Android

## 📝 Notes

- `serviceAccountKey.json` di `.gitignore` - JANGAN commit!
- Firestore rules by default deny semua akses - setup Security Rules di Firebase Console
- Untuk production, use environment-specific credentials

## 📚 Resources

- [Firebase Admin SDK](https://firebase.google.com/docs/database/admin/start)
- [Firestore Documentation](https://firebase.google.com/docs/firestore)
- [Express.js Guide](https://expressjs.com/)

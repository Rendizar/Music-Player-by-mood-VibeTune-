/**
 * Script untuk seed initial songs data ke Firestore
 * Run: node seed.js
 */

require('dotenv').config();
const { db } = require('./config/firebase');

const seedSongs = async () => {
  try {
    console.log('🌱 Seeding songs data to Firestore...\n');

    // Sample songs data
    const songs = [
      // Happy mood
      { title: 'Good Vibes Only', artist: 'Happy Studio', mood: 'happy', albumArtUrl: 'https://picsum.photos/seed/happy1/400/400', previewUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3' },
      { title: 'Sunshine Day', artist: 'Happy Studio', mood: 'happy', albumArtUrl: 'https://picsum.photos/seed/happy2/400/400', previewUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3' },
      { title: 'Dancing in Joy', artist: 'Happy Studio', mood: 'happy', albumArtUrl: 'https://picsum.photos/seed/happy3/400/400', previewUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3' },
      
      // Sad mood
      { title: 'Heartbreak Melody', artist: 'Sad Vibes', mood: 'sad', albumArtUrl: 'https://picsum.photos/seed/sad1/400/400', previewUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3' },
      { title: 'Rainy Night', artist: 'Sad Vibes', mood: 'sad', albumArtUrl: 'https://picsum.photos/seed/sad2/400/400', previewUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3' },
      { title: 'Tears Flow', artist: 'Sad Vibes', mood: 'sad', albumArtUrl: 'https://picsum.photos/seed/sad3/400/400', previewUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3' },
      
      // Calm mood
      { title: 'Peaceful Morning', artist: 'Calm Studio', mood: 'calm', albumArtUrl: 'https://picsum.photos/seed/calm1/400/400', previewUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3' },
      { title: 'Meditation Flow', artist: 'Calm Studio', mood: 'calm', albumArtUrl: 'https://picsum.photos/seed/calm2/400/400', previewUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3' },
      { title: 'Zen Garden', artist: 'Calm Studio', mood: 'calm', albumArtUrl: 'https://picsum.photos/seed/calm3/400/400', previewUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-9.mp3' },
      
      // Energetic mood
      { title: 'Energy Rush', artist: 'Energy Beats', mood: 'energetic', albumArtUrl: 'https://picsum.photos/seed/energy1/400/400', previewUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-10.mp3' },
      { title: 'Power Up', artist: 'Energy Beats', mood: 'energetic', albumArtUrl: 'https://picsum.photos/seed/energy2/400/400', previewUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-11.mp3' },
      { title: 'Electric Feel', artist: 'Energy Beats', mood: 'energetic', albumArtUrl: 'https://picsum.photos/seed/energy3/400/400', previewUrl: 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-12.mp3' },
    ];

    let addedCount = 0;

    for (const song of songs) {
      try {
        const docRef = await db.collection('songs').add({
          ...song,
          createdAt: new Date(),
          updatedAt: new Date()
        });

        console.log(`✅ Added: "${song.title}" by ${song.artist} (${song.mood})`);
        addedCount++;
      } catch (error) {
        console.error(`❌ Error adding "${song.title}":`, error.message);
      }
    }

    console.log(`\n🎉 Seeding complete! Added ${addedCount}/${songs.length} songs`);
    process.exit(0);
  } catch (error) {
    console.error('❌ Seeding error:', error);
    process.exit(1);
  }
};

// Run seeding
seedSongs();

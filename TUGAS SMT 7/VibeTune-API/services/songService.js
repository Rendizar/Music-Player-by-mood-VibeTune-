/**
 * Song Service - Handle all database operations for songs
 */

const { db, firestore } = require('../config/firebase');

class SongService {
  /**
   * Get all songs by mood from Firestore
   */
  static async getSongsByMood(mood) {
    try {
      const snapshot = await db.collection('songs')
        .where('mood', '==', mood)
        .get();

      const songs = [];
      snapshot.forEach(doc => {
        songs.push({
          id: doc.id,
          ...doc.data()
        });
      });

      return songs;
    } catch (error) {
      console.error('Error fetching songs by mood:', error);
      throw error;
    }
  }

  /**
   * Get single song by ID
   */
  static async getSongById(songId) {
    try {
      const doc = await db.collection('songs').doc(songId).get();

      if (!doc.exists) {
        return null;
      }

      return {
        id: doc.id,
        ...doc.data()
      };
    } catch (error) {
      console.error('Error fetching song:', error);
      throw error;
    }
  }

  /**
   * Save new song to Firestore
   */
  static async addSong(songData) {
    try {
      const docRef = await db.collection('songs').add({
        ...songData,
        createdAt: firestore.Timestamp.now(),
        updatedAt: firestore.Timestamp.now()
      });

      return {
        id: docRef.id,
        ...songData
      };
    } catch (error) {
      console.error('Error adding song:', error);
      throw error;
    }
  }

  /**
   * Update song in Firestore
   */
  static async updateSong(songId, updateData) {
    try {
      await db.collection('songs').doc(songId).update({
        ...updateData,
        updatedAt: firestore.Timestamp.now()
      });

      return {
        id: songId,
        ...updateData
      };
    } catch (error) {
      console.error('Error updating song:', error);
      throw error;
    }
  }

  /**
   * Delete song from Firestore
   */
  static async deleteSong(songId) {
    try {
      await db.collection('songs').doc(songId).delete();
      return { success: true, id: songId };
    } catch (error) {
      console.error('Error deleting song:', error);
      throw error;
    }
  }

  /**
   * Get all songs (paginated)
   */
  static async getAllSongs(limit = 20, offset = 0) {
    try {
      const snapshot = await db.collection('songs')
        .orderBy('createdAt', 'desc')
        .limit(limit)
        .offset(offset)
        .get();

      const songs = [];
      snapshot.forEach(doc => {
        songs.push({
          id: doc.id,
          ...doc.data()
        });
      });

      return songs;
    } catch (error) {
      console.error('Error fetching all songs:', error);
      throw error;
    }
  }

  /**
   * Search songs by title or artist
   */
  static async searchSongs(query) {
    try {
      // Firestore doesn't have full-text search, so we fetch and filter
      const snapshot = await db.collection('songs').get();
      
      const songs = [];
      const lowerQuery = query.toLowerCase();

      snapshot.forEach(doc => {
        const data = doc.data();
        if (
          data.title?.toLowerCase().includes(lowerQuery) ||
          data.artist?.toLowerCase().includes(lowerQuery)
        ) {
          songs.push({
            id: doc.id,
            ...data
          });
        }
      });

      return songs;
    } catch (error) {
      console.error('Error searching songs:', error);
      throw error;
    }
  }
}

module.exports = SongService;

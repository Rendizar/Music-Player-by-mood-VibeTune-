/**
 * Songs Routes - API endpoints untuk Android app
 */

const express = require('express');
const router = express.Router();
const SongService = require('../services/songService');

/**
 * GET /api/songs/mood/:mood
 * Get songs by mood (happy, sad, energetic, calm, etc)
 * Android will call: /api/songs/mood/happy
 */
router.get('/mood/:mood', async (req, res) => {
  try {
    const { mood } = req.params;
    const songs = await SongService.getSongsByMood(mood);

    res.json({
      success: true,
      mood,
      count: songs.length,
      data: songs
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      error: error.message
    });
  }
});

/**
 * GET /api/songs/:id
 * Get single song by ID
 */
router.get('/:id', async (req, res) => {
  try {
    const { id } = req.params;
    const song = await SongService.getSongById(id);

    if (!song) {
      return res.status(404).json({
        success: false,
        error: 'Song not found'
      });
    }

    res.json({
      success: true,
      data: song
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      error: error.message
    });
  }
});

/**
 * GET /api/songs
 * Get all songs with pagination
 * Query params: limit, offset
 * Example: /api/songs?limit=20&offset=0
 */
router.get('/', async (req, res) => {
  try {
    const limit = parseInt(req.query.limit) || 20;
    const offset = parseInt(req.query.offset) || 0;

    const songs = await SongService.getAllSongs(limit, offset);

    res.json({
      success: true,
      count: songs.length,
      limit,
      offset,
      data: songs
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      error: error.message
    });
  }
});

/**
 * GET /api/songs/search?q=query
 * Search songs by title or artist
 */
router.get('/search', async (req, res) => {
  try {
    const { q } = req.query;

    if (!q) {
      return res.status(400).json({
        success: false,
        error: 'Query parameter "q" is required'
      });
    }

    const songs = await SongService.searchSongs(q);

    res.json({
      success: true,
      query: q,
      count: songs.length,
      data: songs
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      error: error.message
    });
  }
});

/**
 * POST /api/songs
 * Add new song (for admin purposes)
 * Body: { title, artist, mood, albumArtUrl, previewUrl }
 */
router.post('/', async (req, res) => {
  try {
    const { title, artist, mood, albumArtUrl, previewUrl } = req.body;

    if (!title || !artist || !mood) {
      return res.status(400).json({
        success: false,
        error: 'title, artist, and mood are required'
      });
    }

    const song = await SongService.addSong({
      title,
      artist,
      mood,
      albumArtUrl: albumArtUrl || '',
      previewUrl: previewUrl || ''
    });

    res.status(201).json({
      success: true,
      message: 'Song added successfully',
      data: song
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      error: error.message
    });
  }
});

/**
 * PUT /api/songs/:id
 * Update song
 */
router.put('/:id', async (req, res) => {
  try {
    const { id } = req.params;
    const updateData = req.body;

    const song = await SongService.updateSong(id, updateData);

    res.json({
      success: true,
      message: 'Song updated successfully',
      data: song
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      error: error.message
    });
  }
});

/**
 * DELETE /api/songs/:id
 * Delete song
 */
router.delete('/:id', async (req, res) => {
  try {
    const { id } = req.params;
    await SongService.deleteSong(id);

    res.json({
      success: true,
      message: 'Song deleted successfully',
      id
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      error: error.message
    });
  }
});

module.exports = router;

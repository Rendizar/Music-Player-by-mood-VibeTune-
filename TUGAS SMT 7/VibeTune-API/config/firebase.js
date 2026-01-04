/**
 * Firebase Admin SDK Configuration
 * Initialize Firebase with service account credentials
 */

const admin = require('firebase-admin');
const path = require('path');
require('dotenv').config();

// Path to service account key (create this from Firebase Console)
const serviceAccountPath = path.join(__dirname, '../serviceAccountKey.json');

// Check if service account key exists
let db;

try {
  // Initialize Firebase Admin SDK
  admin.initializeApp({
    credential: admin.credential.cert(serviceAccountPath),
    projectId: process.env.FIREBASE_PROJECT_ID
  });

  // Get Firestore database reference
  db = admin.firestore();
  
  console.log('✅ Firebase Admin SDK initialized successfully');
} catch (error) {
  console.error('❌ Firebase initialization error:', error.message);
  console.error('Make sure serviceAccountKey.json exists in the root directory');
  console.error('Download it from Firebase Console > Project Settings > Service Accounts');
}

module.exports = {
  admin,
  db,
  firestore: admin.firestore
};

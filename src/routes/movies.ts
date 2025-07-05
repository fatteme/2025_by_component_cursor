import { Router } from 'express';
import { MovieController } from '../controllers/movieController';

const router = Router();

// Get available movie genres
router.get('/genres', MovieController.getGenres);

// Get detailed movie information
router.get('/:id', MovieController.getMovieDetails);

// Generate movie recommendation for a group
router.post('/recommend', MovieController.recommendMovie);

// Sync genres from TMDB (admin endpoint)
router.post('/sync-genres', MovieController.syncGenres);

export default router; 
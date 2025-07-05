import { Request, Response } from 'express';
import { MovieService, UserPreferences } from '../services/movieService';
import { TMDBService } from '../services/tmdbService';

export class MovieController {
  static async getGenres(req: Request, res: Response): Promise<void> {
    try {
      const genres = await MovieService.getGenres();
      res.json({ genres });
    } catch (error) {
      console.error('Get genres error:', error);
      res.status(500).json({ error: 'Failed to fetch genres' });
    }
  }

  static async getMovieDetails(req: Request, res: Response): Promise<void> {
    try {
      const { id } = req.params;
      const tmdbId = parseInt(id);

      if (isNaN(tmdbId)) {
        res.status(400).json({ error: 'Invalid movie ID' });
        return;
      }

      const movie = await MovieService.getMovieDetails(tmdbId);

      if (!movie) {
        res.status(404).json({ error: 'Movie not found' });
        return;
      }

      // Add full poster URL
      const movieWithPoster = {
        ...movie.toObject(),
        posterUrl: TMDBService.getPosterUrl(movie.posterPath),
      };

      res.json({ movie: movieWithPoster });
    } catch (error) {
      console.error('Get movie details error:', error);
      res.status(500).json({ error: 'Failed to fetch movie details' });
    }
  }

  static async recommendMovie(req: Request, res: Response): Promise<void> {
    try {
      const { userPreferences } = req.body;

      if (!userPreferences || !Array.isArray(userPreferences) || userPreferences.length === 0) {
        res.status(400).json({ error: 'User preferences are required' });
        return;
      }

      // Validate user preferences structure
      for (const pref of userPreferences) {
        if (!pref.userId || !Array.isArray(pref.preferredGenres)) {
          res.status(400).json({ error: 'Invalid user preferences format' });
          return;
        }
      }

      const recommendation = await MovieService.recommendMovieForGroup(userPreferences);

      if (!recommendation) {
        res.status(404).json({ error: 'No suitable movie found' });
        return;
      }

      // Add full poster URL
      const movieWithPoster = {
        ...recommendation.movie.toObject(),
        posterUrl: TMDBService.getPosterUrl(recommendation.movie.posterPath),
      };

      res.json({
        recommendation: {
          movie: movieWithPoster,
          score: recommendation.score,
          matchedGenres: recommendation.matchedGenres,
        },
      });
    } catch (error) {
      console.error('Movie recommendation error:', error);
      res.status(500).json({ error: 'Failed to generate movie recommendation' });
    }
  }

  static async syncGenres(req: Request, res: Response): Promise<void> {
    try {
      await MovieService.syncGenres();
      res.json({ message: 'Genres synced successfully' });
    } catch (error) {
      console.error('Sync genres error:', error);
      res.status(500).json({ error: 'Failed to sync genres' });
    }
  }
} 
import { Movie, IMovie } from '../models/Movie';
import { Genre, IGenre } from '../models/Genre';
import { TMDBService, TMDBMovie } from './tmdbService';

export interface UserPreferences {
  userId: string;
  preferredGenres: number[];
}

export interface MovieRecommendation {
  movie: IMovie;
  score: number;
  matchedGenres: string[];
}

export class MovieService {
  static async syncGenres(): Promise<void> {
    try {
      const tmdbGenres = await TMDBService.getGenres();
      
      for (const tmdbGenre of tmdbGenres) {
        await Genre.findOneAndUpdate(
          { tmdbId: tmdbGenre.id },
          { tmdbId: tmdbGenre.id, name: tmdbGenre.name },
          { upsert: true, new: true }
        );
      }
      
      console.log(`Synced ${tmdbGenres.length} genres from TMDB`);
    } catch (error) {
      console.error('Failed to sync genres:', error);
      throw error;
    }
  }

  static async getGenres(): Promise<IGenre[]> {
    return await Genre.find().sort({ name: 1 });
  }

  static async getMovieDetails(tmdbId: number): Promise<IMovie | null> {
    // First check if movie exists in our database
    let movie = await Movie.findOne({ tmdbId });
    
    if (!movie) {
      // Fetch from TMDB and cache
      try {
        const tmdbMovie = await TMDBService.getMovieDetails(tmdbId);
        movie = new Movie({
          tmdbId: tmdbMovie.id,
          title: tmdbMovie.title,
          overview: tmdbMovie.overview,
          posterPath: tmdbMovie.poster_path,
          releaseDate: new Date(tmdbMovie.release_date),
          genres: tmdbMovie.genres,
          rating: tmdbMovie.vote_average,
        });
        await movie.save();
      } catch (error) {
        console.error('Failed to fetch movie details:', error);
        return null;
      }
    }
    
    return movie;
  }

  static async recommendMovieForGroup(userPreferences: UserPreferences[]): Promise<MovieRecommendation | null> {
    if (userPreferences.length === 0) {
      throw new Error('No user preferences provided');
    }

    try {
      // Calculate genre preference scores
      const genreScores = this.calculateGenreScores(userPreferences);
      
      // Get top preferred genres
      const topGenres = Object.entries(genreScores)
        .sort(([,a], [,b]) => b - a)
        .slice(0, 3) // Top 3 genres
        .map(([genreId]) => parseInt(genreId));

      if (topGenres.length === 0) {
        throw new Error('No preferred genres found');
      }

      // Fetch movies from TMDB based on top genres
      const { movies } = await TMDBService.getMoviesByGenres(topGenres, 1);
      
      if (movies.length === 0) {
        throw new Error('No movies found for the given preferences');
      }

      // Score each movie based on genre preferences
      const movieScores = await this.scoreMovies(movies, userPreferences);
      
      // Get the best match
      const bestMatch = movieScores.reduce((best, current) => 
        current.score > best.score ? current : best
      );

      return bestMatch;
    } catch (error) {
      console.error('Movie recommendation error:', error);
      throw error;
    }
  }

  private static calculateGenreScores(userPreferences: UserPreferences[]): Record<string, number> {
    const genreScores: Record<string, number> = {};
    
    userPreferences.forEach(userPref => {
      userPref.preferredGenres.forEach(genreId => {
        const genreKey = genreId.toString();
        genreScores[genreKey] = (genreScores[genreKey] || 0) + 1;
      });
    });
    
    return genreScores;
  }

  private static async scoreMovies(
    movies: TMDBMovie[], 
    userPreferences: UserPreferences[]
  ): Promise<MovieRecommendation[]> {
    const movieScores: MovieRecommendation[] = [];
    
    for (const tmdbMovie of movies) {
      // Check if movie exists in our database or fetch it
      let movie = await Movie.findOne({ tmdbId: tmdbMovie.id });
      
      if (!movie) {
        // Create movie record
        movie = new Movie({
          tmdbId: tmdbMovie.id,
          title: tmdbMovie.title,
          overview: tmdbMovie.overview,
          posterPath: tmdbMovie.poster_path,
          releaseDate: new Date(tmdbMovie.release_date),
          genres: [], // Will be populated when we fetch details
          rating: tmdbMovie.vote_average,
        });
        await movie.save();
      }

      // Calculate score based on genre matches
      const score = this.calculateMovieScore(tmdbMovie, userPreferences);
      const matchedGenres = this.getMatchedGenres(tmdbMovie, userPreferences);
      
      movieScores.push({
        movie,
        score,
        matchedGenres,
      });
    }
    
    return movieScores;
  }

  private static calculateMovieScore(movie: TMDBMovie, userPreferences: UserPreferences[]): number {
    let totalScore = 0;
    let totalUsers = userPreferences.length;
    
    userPreferences.forEach(userPref => {
      const userScore = movie.genre_ids.reduce((score, movieGenreId) => {
        if (userPref.preferredGenres.includes(movieGenreId)) {
          return score + 1;
        }
        return score;
      }, 0);
      
      totalScore += userScore / userPref.preferredGenres.length;
    });
    
    return totalScore / totalUsers;
  }

  private static getMatchedGenres(movie: TMDBMovie, userPreferences: UserPreferences[]): string[] {
    const allPreferredGenres = new Set<number>();
    userPreferences.forEach(userPref => {
      userPref.preferredGenres.forEach(genreId => allPreferredGenres.add(genreId));
    });
    
    const matchedGenreIds = movie.genre_ids.filter(genreId => 
      allPreferredGenres.has(genreId)
    );
    
    // For now, return genre IDs as strings. In a full implementation,
    // you'd want to fetch genre names from the database
    return matchedGenreIds.map(id => id.toString());
  }
} 
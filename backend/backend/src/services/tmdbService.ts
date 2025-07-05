import axios from 'axios';
import { config } from '../config';

const TMDB_BASE_URL = 'https://api.themoviedb.org/3';
const TMDB_IMAGE_BASE_URL = 'https://image.tmdb.org/t/p/w500';

export interface TMDBGenre {
  id: number;
  name: string;
}

export interface TMDBMovie {
  id: number;
  title: string;
  overview: string;
  poster_path: string;
  release_date: string;
  genre_ids: number[];
  vote_average: number;
}

export interface TMDBMovieDetail extends TMDBMovie {
  genres: TMDBGenre[];
}

export class TMDBService {
  private static async makeRequest(endpoint: string, params: any = {}): Promise<any> {
    try {
      const response = await axios.get(`${TMDB_BASE_URL}${endpoint}`, {
        params: {
          api_key: config.tmdb.apiKey,
          ...params,
        },
      });
      return response.data;
    } catch (error) {
      console.error('TMDB API error:', error);
      throw new Error('Failed to fetch data from TMDB');
    }
  }

  static async getGenres(): Promise<TMDBGenre[]> {
    const data = await this.makeRequest('/genre/movie/list');
    return data.genres;
  }

  static async getMoviesByGenres(genreIds: number[], page: number = 1): Promise<{
    movies: TMDBMovie[];
    totalPages: number;
  }> {
    const data = await this.makeRequest('/discover/movie', {
      with_genres: genreIds.join(','),
      page,
      sort_by: 'popularity.desc',
      'vote_count.gte': 100, // Only movies with sufficient votes
    });

    return {
      movies: data.results,
      totalPages: data.total_pages,
    };
  }

  static async getMovieDetails(movieId: number): Promise<TMDBMovieDetail> {
    return await this.makeRequest(`/movie/${movieId}`);
  }

  static getPosterUrl(posterPath: string): string {
    if (!posterPath) return '';
    return `${TMDB_IMAGE_BASE_URL}${posterPath}`;
  }
} 
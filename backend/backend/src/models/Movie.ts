import mongoose, { Document, Schema } from 'mongoose';

export interface IGenre {
  id: number;
  name: string;
}

export interface IMovie extends Document {
  tmdbId: number;
  title: string;
  overview: string;
  posterPath: string;
  releaseDate: Date;
  genres: IGenre[];
  rating: number;
  createdAt: Date;
  updatedAt: Date;
}

const genreSchema = new Schema<IGenre>({
  id: { type: Number, required: true },
  name: { type: String, required: true },
});

const movieSchema = new Schema<IMovie>({
  tmdbId: {
    type: Number,
    required: true,
    unique: true,
  },
  title: {
    type: String,
    required: true,
  },
  overview: {
    type: String,
    required: true,
  },
  posterPath: {
    type: String,
    required: true,
  },
  releaseDate: {
    type: Date,
    required: true,
  },
  genres: [genreSchema],
  rating: {
    type: Number,
    required: true,
  },
}, {
  timestamps: true,
});

export const Movie = mongoose.model<IMovie>('Movie', movieSchema); 
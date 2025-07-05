import mongoose, { Document, Schema } from 'mongoose';

export interface IGenre extends Document {
  tmdbId: number;
  name: string;
  createdAt: Date;
  updatedAt: Date;
}

const genreSchema = new Schema<IGenre>({
  tmdbId: {
    type: Number,
    required: true,
    unique: true,
  },
  name: {
    type: String,
    required: true,
  },
}, {
  timestamps: true,
});

export const Genre = mongoose.model<IGenre>('Genre', genreSchema); 
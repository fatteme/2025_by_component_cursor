import mongoose, { Document, Schema } from 'mongoose';

export interface IVote {
  userId: mongoose.Types.ObjectId;
  movieId: number;
  vote: 'yes' | 'no';
}

export interface IVotingSession extends Document {
  groupId: mongoose.Types.ObjectId;
  movies: number[]; // TMDB movie IDs
  votes: IVote[];
  isActive: boolean;
  createdAt: Date;
  updatedAt: Date;
}

const voteSchema = new Schema<IVote>({
  userId: {
    type: Schema.Types.ObjectId,
    ref: 'User',
    required: true,
  },
  movieId: {
    type: Number,
    required: true,
  },
  vote: {
    type: String,
    enum: ['yes', 'no'],
    required: true,
  },
});

const votingSessionSchema = new Schema<IVotingSession>({
  groupId: {
    type: Schema.Types.ObjectId,
    ref: 'Group',
    required: true,
  },
  movies: [{
    type: Number,
    required: true,
  }],
  votes: [voteSchema],
  isActive: {
    type: Boolean,
    default: true,
  },
}, {
  timestamps: true,
});

export const VotingSession = mongoose.model<IVotingSession>('VotingSession', votingSessionSchema); 
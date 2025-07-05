import mongoose, { Document, Schema } from 'mongoose';

export interface IUserPreference extends Document {
  userId: mongoose.Types.ObjectId;
  groupId: mongoose.Types.ObjectId;
  preferredGenres: number[];
  createdAt: Date;
  updatedAt: Date;
}

const userPreferenceSchema = new Schema<IUserPreference>({
  userId: {
    type: Schema.Types.ObjectId,
    ref: 'User',
    required: true,
  },
  groupId: {
    type: Schema.Types.ObjectId,
    ref: 'Group',
    required: true,
  },
  preferredGenres: [{
    type: Number,
    required: true,
  }],
}, {
  timestamps: true,
});

// Compound index to ensure one preference per user per group
userPreferenceSchema.index({ userId: 1, groupId: 1 }, { unique: true });

export const UserPreference = mongoose.model<IUserPreference>('UserPreference', userPreferenceSchema); 
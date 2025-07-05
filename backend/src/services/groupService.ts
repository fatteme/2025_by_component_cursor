import { Group, IGroup } from '../models/Group';
import { UserPreference, IUserPreference } from '../models/UserPreference';
import { VotingSession, IVotingSession } from '../models/VotingSession';
import { MovieService, UserPreferences } from './movieService';
import { TMDBService } from './tmdbService';
import { generateInvitationCode } from '../utils/invitationCode';

export class GroupService {
  static async createGroup(name: string, ownerId: string): Promise<IGroup> {
    const invitationCode = generateInvitationCode();
    
    const group = new Group({
      name,
      ownerId,
      members: [ownerId], // Owner is automatically a member
      invitationCode,
    });
    
    return await group.save();
  }

  static async getGroupById(groupId: string): Promise<IGroup | null> {
    return await Group.findById(groupId).populate('ownerId', 'name email');
  }

  static async getGroupByInvitationCode(invitationCode: string): Promise<IGroup | null> {
    return await Group.findOne({ invitationCode, isActive: true });
  }

  static async updateGroup(groupId: string, updateData: Partial<IGroup>): Promise<IGroup | null> {
    return await Group.findByIdAndUpdate(
      groupId,
      updateData,
      { new: true, runValidators: true }
    );
  }

  static async deleteGroup(groupId: string): Promise<boolean> {
    const result = await Group.findByIdAndUpdate(
      groupId,
      { isActive: false },
      { new: true }
    );
    return !!result;
  }

  static async joinGroup(groupId: string, userId: string): Promise<IGroup | null> {
    const group = await Group.findById(groupId);
    
    if (!group || !group.isActive) {
      throw new Error('Group not found or inactive');
    }
    
    if (group.members.includes(userId)) {
      throw new Error('User is already a member of this group');
    }
    
    group.members.push(userId);
    return await group.save();
  }

  static async setUserPreferences(
    groupId: string, 
    userId: string, 
    preferredGenres: number[]
  ): Promise<IUserPreference> {
    const preference = await UserPreference.findOneAndUpdate(
      { userId, groupId },
      { preferredGenres },
      { upsert: true, new: true }
    );
    
    return preference;
  }

  static async getUserPreferences(groupId: string): Promise<IUserPreference[]> {
    return await UserPreference.find({ groupId }).populate('userId', 'name email');
  }

  static async startVotingSession(groupId: string): Promise<IVotingSession> {
    // Get all user preferences for the group
    const userPreferences = await this.getUserPreferences(groupId);
    
    if (userPreferences.length === 0) {
      throw new Error('No user preferences found for this group');
    }
    
    // Convert to format expected by MovieService
    const preferences: UserPreferences[] = userPreferences.map(pref => ({
      userId: pref.userId.toString(),
      preferredGenres: pref.preferredGenres,
    }));
    
    // Get movie recommendations (we'll use the first 10 movies from different genres)
    const allPreferredGenres = new Set<number>();
    preferences.forEach(pref => {
      pref.preferredGenres.forEach(genreId => allPreferredGenres.add(genreId));
    });
    
    const genreArray = Array.from(allPreferredGenres);
    const movies: number[] = [];
    
    // Get movies from each preferred genre
    for (const genreId of genreArray.slice(0, 5)) { // Limit to 5 genres
      try {
        const { movies: genreMovies } = await TMDBService.getMoviesByGenres([genreId], 1);
        const movieIds = genreMovies.slice(0, 2).map((movie: any) => movie.id); // Get 2 movies per genre
        movies.push(...movieIds);
      } catch (error) {
        console.error(`Failed to fetch movies for genre ${genreId}:`, error);
      }
    }
    
    // Remove duplicates and limit to 10 movies
    const uniqueMovies = [...new Set(movies)].slice(0, 10);
    
    if (uniqueMovies.length === 0) {
      throw new Error('No movies found for the group preferences');
    }
    
    // Create voting session
    const votingSession = new VotingSession({
      groupId,
      movies: uniqueMovies,
      votes: [],
    });
    
    return await votingSession.save();
  }

  static async getVotingSession(sessionId: string): Promise<IVotingSession | null> {
    return await VotingSession.findById(sessionId).populate('groupId');
  }

  static async submitVote(
    sessionId: string, 
    userId: string, 
    movieId: number, 
    vote: 'yes' | 'no'
  ): Promise<IVotingSession | null> {
    const session = await VotingSession.findById(sessionId);
    
    if (!session || !session.isActive) {
      throw new Error('Voting session not found or inactive');
    }
    
    if (!session.movies.includes(movieId)) {
      throw new Error('Invalid movie for this voting session');
    }
    
    // Check if user already voted on this movie
    const existingVote = session.votes.find(
      (v: any) => v.userId.toString() === userId && v.movieId === movieId
    );
    
    if (existingVote) {
      // Update existing vote
      existingVote.vote = vote;
    } else {
      // Add new vote
      session.votes.push({
        userId: userId as any,
        movieId,
        vote,
      });
    }
    
    return await session.save();
  }

  static async endVotingSession(sessionId: string): Promise<IVotingSession | null> {
    return await VotingSession.findByIdAndUpdate(
      sessionId,
      { isActive: false },
      { new: true }
    );
  }

  static async getVotingResults(sessionId: string): Promise<any> {
    const session = await VotingSession.findById(sessionId);
    
    if (!session) {
      throw new Error('Voting session not found');
    }
    
    // Calculate results for each movie
    const results = session.movies.map((movieId: number) => {
      const movieVotes = session.votes.filter((v: any) => v.movieId === movieId);
      const yesVotes = movieVotes.filter((v: any) => v.vote === 'yes').length;
      const noVotes = movieVotes.filter((v: any) => v.vote === 'no').length;
      
      return {
        movieId,
        yesVotes,
        noVotes,
        totalVotes: yesVotes + noVotes,
        score: yesVotes / (yesVotes + noVotes) || 0,
      };
    });
    
    // Sort by score (highest first)
    results.sort((a: any, b: any) => b.score - a.score);
    
    return {
      sessionId,
      isActive: session.isActive,
      movies: session.movies,
      results,
      bestMatch: results[0] || null,
    };
  }
} 
import { Request, Response } from 'express';
import { GroupService } from '../services/groupService';
import { MovieService } from '../services/movieService';
import { AuthenticatedRequest } from '../middlewares/auth';

export class GroupController {
  static async createGroup(req: AuthenticatedRequest, res: Response): Promise<void> {
    try {
      const { name } = req.body;
      const userId = req.user._id;

      if (!name || typeof name !== 'string' || name.trim().length === 0) {
        res.status(400).json({ error: 'Group name is required' });
        return;
      }

      const group = await GroupService.createGroup(name.trim(), userId);

      res.status(201).json({
        group: {
          id: group._id,
          name: group.name,
          ownerId: group.ownerId,
          invitationCode: group.invitationCode,
          members: group.members,
          isActive: group.isActive,
        },
      });
    } catch (error) {
      console.error('Create group error:', error);
      res.status(500).json({ error: 'Failed to create group' });
    }
  }

  static async getGroup(req: AuthenticatedRequest, res: Response): Promise<void> {
    try {
      const { id } = req.params;
      const userId = req.user._id;

      const group = await GroupService.getGroupById(id);

      if (!group) {
        res.status(404).json({ error: 'Group not found' });
        return;
      }

      // Check if user is a member
      if (!group.members.includes(userId)) {
        res.status(403).json({ error: 'You must be a member of this group' });
        return;
      }

      res.json({
        group: {
          id: group._id,
          name: group.name,
          ownerId: group.ownerId,
          invitationCode: group.invitationCode,
          members: group.members,
          isActive: group.isActive,
        },
      });
    } catch (error) {
      console.error('Get group error:', error);
      res.status(500).json({ error: 'Failed to get group' });
    }
  }

  static async updateGroup(req: AuthenticatedRequest, res: Response): Promise<void> {
    try {
      const { id } = req.params;
      const { name } = req.body;
      const userId = req.user._id;

      if (!name || typeof name !== 'string' || name.trim().length === 0) {
        res.status(400).json({ error: 'Group name is required' });
        return;
      }

      const group = await GroupService.getGroupById(id);

      if (!group) {
        res.status(404).json({ error: 'Group not found' });
        return;
      }

      if (group.ownerId.toString() !== userId) {
        res.status(403).json({ error: 'Only group owner can update the group' });
        return;
      }

      const updatedGroup = await GroupService.updateGroup(id, { name: name.trim() });

      res.json({
        group: {
          id: updatedGroup._id,
          name: updatedGroup.name,
          ownerId: updatedGroup.ownerId,
          invitationCode: updatedGroup.invitationCode,
          members: updatedGroup.members,
          isActive: updatedGroup.isActive,
        },
      });
    } catch (error) {
      console.error('Update group error:', error);
      res.status(500).json({ error: 'Failed to update group' });
    }
  }

  static async deleteGroup(req: AuthenticatedRequest, res: Response): Promise<void> {
    try {
      const { id } = req.params;
      const userId = req.user._id;

      const group = await GroupService.getGroupById(id);

      if (!group) {
        res.status(404).json({ error: 'Group not found' });
        return;
      }

      if (group.ownerId.toString() !== userId) {
        res.status(403).json({ error: 'Only group owner can delete the group' });
        return;
      }

      await GroupService.deleteGroup(id);

      res.json({ message: 'Group deleted successfully' });
    } catch (error) {
      console.error('Delete group error:', error);
      res.status(500).json({ error: 'Failed to delete group' });
    }
  }

  static async joinGroup(req: AuthenticatedRequest, res: Response): Promise<void> {
    try {
      const { invitationCode } = req.body;
      const userId = req.user._id;

      if (!invitationCode) {
        res.status(400).json({ error: 'Invitation code is required' });
        return;
      }

      const group = await GroupService.getGroupByInvitationCode(invitationCode);

      if (!group) {
        res.status(404).json({ error: 'Invalid invitation code' });
        return;
      }

      const updatedGroup = await GroupService.joinGroup(group._id, userId);

      res.json({
        group: {
          id: updatedGroup._id,
          name: updatedGroup.name,
          ownerId: updatedGroup.ownerId,
          invitationCode: updatedGroup.invitationCode,
          members: updatedGroup.members,
          isActive: updatedGroup.isActive,
        },
      });
    } catch (error) {
      console.error('Join group error:', error);
      if (error.message === 'User is already a member of this group') {
        res.status(400).json({ error: error.message });
      } else {
        res.status(500).json({ error: 'Failed to join group' });
      }
    }
  }

  static async setPreferences(req: AuthenticatedRequest, res: Response): Promise<void> {
    try {
      const { id } = req.params;
      const { preferredGenres } = req.body;
      const userId = req.user._id;

      if (!preferredGenres || !Array.isArray(preferredGenres)) {
        res.status(400).json({ error: 'Preferred genres array is required' });
        return;
      }

      // Validate that user is a member of the group
      const group = await GroupService.getGroupById(id);
      if (!group || !group.members.includes(userId)) {
        res.status(403).json({ error: 'You must be a member of this group' });
        return;
      }

      const preference = await GroupService.setUserPreferences(id, userId, preferredGenres);

      res.json({
        preference: {
          id: preference._id,
          userId: preference.userId,
          groupId: preference.groupId,
          preferredGenres: preference.preferredGenres,
        },
      });
    } catch (error) {
      console.error('Set preferences error:', error);
      res.status(500).json({ error: 'Failed to set preferences' });
    }
  }

  static async getPreferences(req: AuthenticatedRequest, res: Response): Promise<void> {
    try {
      const { id } = req.params;
      const userId = req.user._id;

      // Validate that user is a member of the group
      const group = await GroupService.getGroupById(id);
      if (!group || !group.members.includes(userId)) {
        res.status(403).json({ error: 'You must be a member of this group' });
        return;
      }

      const preferences = await GroupService.getUserPreferences(id);

      res.json({
        preferences: preferences.map(pref => ({
          id: pref._id,
          userId: pref.userId,
          groupId: pref.groupId,
          preferredGenres: pref.preferredGenres,
        })),
      });
    } catch (error) {
      console.error('Get preferences error:', error);
      res.status(500).json({ error: 'Failed to get preferences' });
    }
  }

  static async startVotingSession(req: AuthenticatedRequest, res: Response): Promise<void> {
    try {
      const { id } = req.params;
      const userId = req.user._id;

      // Validate that user is the owner of the group
      const group = await GroupService.getGroupById(id);
      if (!group || group.ownerId.toString() !== userId) {
        res.status(403).json({ error: 'Only group owner can start voting session' });
        return;
      }

      const votingSession = await GroupService.startVotingSession(id);

      res.status(201).json({
        votingSession: {
          id: votingSession._id,
          groupId: votingSession.groupId,
          movies: votingSession.movies,
          isActive: votingSession.isActive,
          createdAt: votingSession.createdAt,
        },
      });
    } catch (error) {
      console.error('Start voting session error:', error);
      res.status(500).json({ error: 'Failed to start voting session' });
    }
  }

  static async getVotingSession(req: AuthenticatedRequest, res: Response): Promise<void> {
    try {
      const { sessionId } = req.params;
      const userId = req.user._id;

      const votingSession = await GroupService.getVotingSession(sessionId);

      if (!votingSession) {
        res.status(404).json({ error: 'Voting session not found' });
        return;
      }

      // Validate that user is a member of the group
      const group = await GroupService.getGroupById(votingSession.groupId.toString());
      if (!group || !group.members.includes(userId)) {
        res.status(403).json({ error: 'You must be a member of this group' });
        return;
      }

      res.json({
        votingSession: {
          id: votingSession._id,
          groupId: votingSession.groupId,
          movies: votingSession.movies,
          isActive: votingSession.isActive,
          createdAt: votingSession.createdAt,
        },
      });
    } catch (error) {
      console.error('Get voting session error:', error);
      res.status(500).json({ error: 'Failed to get voting session' });
    }
  }

  static async submitVote(req: AuthenticatedRequest, res: Response): Promise<void> {
    try {
      const { sessionId } = req.params;
      const { movieId, vote } = req.body;
      const userId = req.user._id;

      if (!movieId || !vote || !['yes', 'no'].includes(vote)) {
        res.status(400).json({ error: 'Movie ID and vote (yes/no) are required' });
        return;
      }

      const votingSession = await GroupService.getVotingSession(sessionId);

      if (!votingSession) {
        res.status(404).json({ error: 'Voting session not found' });
        return;
      }

      // Validate that user is a member of the group
      const group = await GroupService.getGroupById(votingSession.groupId.toString());
      if (!group || !group.members.includes(userId)) {
        res.status(403).json({ error: 'You must be a member of this group' });
        return;
      }

      const updatedSession = await GroupService.submitVote(sessionId, userId, movieId, vote);

      res.json({
        message: 'Vote submitted successfully',
        votingSession: {
          id: updatedSession._id,
          groupId: updatedSession.groupId,
          movies: updatedSession.movies,
          isActive: updatedSession.isActive,
        },
      });
    } catch (error) {
      console.error('Submit vote error:', error);
      res.status(500).json({ error: 'Failed to submit vote' });
    }
  }

  static async endVotingSession(req: AuthenticatedRequest, res: Response): Promise<void> {
    try {
      const { sessionId } = req.params;
      const userId = req.user._id;

      const votingSession = await GroupService.getVotingSession(sessionId);

      if (!votingSession) {
        res.status(404).json({ error: 'Voting session not found' });
        return;
      }

      // Validate that user is the owner of the group
      const group = await GroupService.getGroupById(votingSession.groupId.toString());
      if (!group || group.ownerId.toString() !== userId) {
        res.status(403).json({ error: 'Only group owner can end voting session' });
        return;
      }

      await GroupService.endVotingSession(sessionId);
      const results = await GroupService.getVotingResults(sessionId);

      res.json({
        message: 'Voting session ended successfully',
        results,
      });
    } catch (error) {
      console.error('End voting session error:', error);
      res.status(500).json({ error: 'Failed to end voting session' });
    }
  }

  static async getVotingResults(req: AuthenticatedRequest, res: Response): Promise<void> {
    try {
      const { sessionId } = req.params;
      const userId = req.user._id;

      const votingSession = await GroupService.getVotingSession(sessionId);

      if (!votingSession) {
        res.status(404).json({ error: 'Voting session not found' });
        return;
      }

      // Validate that user is a member of the group
      const group = await GroupService.getGroupById(votingSession.groupId.toString());
      if (!group || !group.members.includes(userId)) {
        res.status(403).json({ error: 'You must be a member of this group' });
        return;
      }

      const results = await GroupService.getVotingResults(sessionId);

      res.json({ results });
    } catch (error) {
      console.error('Get voting results error:', error);
      res.status(500).json({ error: 'Failed to get voting results' });
    }
  }
} 
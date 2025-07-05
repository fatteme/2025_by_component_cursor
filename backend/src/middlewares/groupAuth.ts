import { Response, NextFunction } from 'express';
import { AuthenticatedRequest } from './auth';
import { GroupService } from '../services/groupService';

export const requireGroupOwnership = async (
  req: AuthenticatedRequest,
  res: Response,
  next: NextFunction
): Promise<void> => {
  try {
    const groupId = req.params.id || req.params.groupId;
    const userId = req.user._id;

    if (!groupId) {
      res.status(400).json({ error: 'Group ID is required' });
      return;
    }

    const group = await GroupService.getGroupById(groupId);
    
    if (!group) {
      res.status(404).json({ error: 'Group not found' });
      return;
    }

    if (group.ownerId.toString() !== userId) {
      res.status(403).json({ error: 'Only group owner can perform this action' });
      return;
    }

    next();
  } catch (error) {
    res.status(500).json({ error: 'Failed to verify group ownership' });
  }
};

export const requireGroupMembership = async (
  req: AuthenticatedRequest,
  res: Response,
  next: NextFunction
): Promise<void> => {
  try {
    const groupId = req.params.id || req.params.groupId;
    const userId = req.user._id;

    if (!groupId) {
      res.status(400).json({ error: 'Group ID is required' });
      return;
    }

    const group = await GroupService.getGroupById(groupId);
    
    if (!group) {
      res.status(404).json({ error: 'Group not found' });
      return;
    }

    if (!group.members.includes(userId)) {
      res.status(403).json({ error: 'You must be a member of this group' });
      return;
    }

    next();
  } catch (error) {
    res.status(500).json({ error: 'Failed to verify group membership' });
  }
}; 
import { Router } from 'express';
import { GroupController } from '../controllers/groupController';
import { authenticateToken } from '../middlewares/auth';
import { requireGroupOwnership, requireGroupMembership } from '../middlewares/groupAuth';

const router = Router();

// All routes require authentication
router.use(authenticateToken);

// Group management
router.post('/', GroupController.createGroup);
router.get('/:id', requireGroupMembership, GroupController.getGroup);
router.put('/:id', requireGroupOwnership, GroupController.updateGroup);
router.delete('/:id', requireGroupOwnership, GroupController.deleteGroup);

// Group joining
router.post('/join', GroupController.joinGroup);

// User preferences
router.post('/:id/preferences', requireGroupMembership, GroupController.setPreferences);
router.get('/:id/preferences', requireGroupMembership, GroupController.getPreferences);

// Voting sessions
router.post('/:id/voting-sessions', requireGroupOwnership, GroupController.startVotingSession);
router.get('/voting-sessions/:sessionId', GroupController.getVotingSession);
router.post('/voting-sessions/:sessionId/votes', GroupController.submitVote);
router.put('/voting-sessions/:sessionId/end', GroupController.endVotingSession);
router.get('/voting-sessions/:sessionId/results', GroupController.getVotingResults);

export default router; 
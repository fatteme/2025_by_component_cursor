import { Router } from 'express';
import { UserController } from '../controllers/userController';
import { authenticateToken } from '../middlewares/auth';

const router = Router();

router.put('/me', authenticateToken, UserController.updateProfile);

export default router; 
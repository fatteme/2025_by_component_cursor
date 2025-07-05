import { Router } from 'express';
import { AuthController } from '../controllers/authController';

const router = Router();

router.post('/google', AuthController.googleSignIn);

export default router; 
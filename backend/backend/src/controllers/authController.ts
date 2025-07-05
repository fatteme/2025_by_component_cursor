import { Request, Response } from 'express';
import { AuthService } from '../services/authService';

export class AuthController {
  static async googleSignIn(req: Request, res: Response): Promise<void> {
    try {
      const { idToken } = req.body;

      if (!idToken) {
        res.status(400).json({ error: 'Google ID token is required' });
        return;
      }

      // Verify Google token
      const googleUserInfo = await AuthService.verifyGoogleToken(idToken);

      // Find or create user
      const user = await AuthService.findOrCreateUser(googleUserInfo);

      // Generate JWT
      const token = AuthService.generateJWT(user);

      res.json({
        token,
        user: {
          id: user._id,
          googleId: user.googleId,
          name: user.name,
          email: user.email,
        },
      });
    } catch (error) {
      console.error('Google sign-in error:', error);
      res.status(401).json({ error: 'Authentication failed' });
    }
  }
} 
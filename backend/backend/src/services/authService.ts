import jwt from 'jsonwebtoken';
import { OAuth2Client } from 'google-auth-library';
import { User, IUser } from '../models/User';
import { config } from '../config';

const googleClient = new OAuth2Client(config.google.clientId);

export interface GoogleUserInfo {
  googleId: string;
  name: string;
  email: string;
}

export class AuthService {
  static async verifyGoogleToken(idToken: string): Promise<GoogleUserInfo> {
    try {
      const ticket = await googleClient.verifyIdToken({
        idToken,
        audience: config.google.clientId,
      });

      const payload = ticket.getPayload();
      if (!payload) {
        throw new Error('Invalid Google token');
      }

      return {
        googleId: payload.sub,
        name: payload.name || '',
        email: payload.email || '',
      };
    } catch (error) {
      throw new Error('Google token verification failed');
    }
  }

  static async findOrCreateUser(googleUserInfo: GoogleUserInfo): Promise<IUser> {
    let user = await User.findOne({ googleId: googleUserInfo.googleId });

    if (!user) {
      user = new User({
        googleId: googleUserInfo.googleId,
        name: googleUserInfo.name,
        email: googleUserInfo.email,
      });
      await user.save();
    }

    return user;
  }

  static generateJWT(user: IUser): string {
    return jwt.sign(
      { userId: user._id, googleId: user.googleId },
      config.jwt.secret,
      { expiresIn: config.jwt.expiresIn }
    );
  }

  static verifyJWT(token: string): any {
    try {
      return jwt.verify(token, config.jwt.secret);
    } catch (error) {
      throw new Error('Invalid JWT token');
    }
  }
} 
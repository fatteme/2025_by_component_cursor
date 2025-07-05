import { User, IUser } from '../models/User';

export class UserService {
  static async findById(userId: string): Promise<IUser | null> {
    return await User.findById(userId);
  }

  static async updateUser(userId: string, updateData: Partial<IUser>): Promise<IUser | null> {
    return await User.findByIdAndUpdate(
      userId,
      updateData,
      { new: true, runValidators: true }
    );
  }
} 
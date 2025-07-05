import app from './app';
import { config } from './config';

const server = app.listen(config.server.port, () => {
  console.log(`Server running on port ${config.server.port}`);
});

process.on('SIGTERM', () => {
  console.log('SIGTERM received, shutting down gracefully');
  server.close(() => {
    console.log('Process terminated');
  });
}); 
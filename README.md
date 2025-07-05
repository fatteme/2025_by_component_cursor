# MovieSwipe Backend

This is the backend service for MovieSwipe, a group movie recommendation and voting platform.

## Project Structure

```
src/
  config/         # Configuration files and environment setup
  controllers/    # REST API route handlers
  middlewares/    # Express middlewares (auth, error handling, etc.)
  models/         # Mongoose models (User, Group, Movie, etc.)
  routes/         # Express route definitions
  services/       # Business logic and external API integrations
  utils/          # Utility/helper functions
  app.ts          # Express app setup
  server.ts       # Application entry point
.env.example      # Example environment variables
Dockerfile        # Azure deployment container config
azure-pipelines.yml # Azure DevOps pipeline config
```

## Key Technologies
- Node.js
- TypeScript
- Express.js
- MongoDB (Mongoose)
- Google OAuth
- TMDB API
- Azure Cloud 
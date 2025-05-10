# Social Commerce Service API Documentation

## Overview
This document describes the REST API endpoints for the Social Commerce Service.

## Authentication
All API endpoints require JWT authentication except for login and registration.

## Endpoints

### Chat API
- `POST /api/v1/chat/messages` - Send a message
- `GET /api/v1/chat/messages/{conversationId}` - Get conversation history
- `WS /ws/chat` - WebSocket endpoint for real-time chat

### Feed API
- `GET /api/v1/feed` - Get personalized feed
- `POST /api/v1/feed/items` - Create a new feed item
- `GET /api/v1/feed/trending` - Get trending items

### Moderation API
- `POST /api/v1/moderation/review` - Submit content for review
- `GET /api/v1/moderation/decisions/{contentId}` - Get moderation decision

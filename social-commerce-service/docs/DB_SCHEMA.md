# Database Schema Documentation

## Overview
The Social Commerce Service uses PostgreSQL as the primary database.

## Tables

### chat_messages
Stores chat messages between users about products.

### feed_items
Stores community feed content including reviews and recommendations.

### moderation_decisions
Tracks content moderation decisions and history.

## Indexes
- chat_messages: (sender_id, recipient_id, created_at)
- feed_items: (user_id, created_at)
- moderation_decisions: (content_id, content_type)

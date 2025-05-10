-- Seed data for initial setup
-- Version: 2.0.0

-- Insert test users
INSERT INTO users (id, username, email) VALUES
(1, 'test_user1', 'user1@example.com'),
(2, 'test_user2', 'user2@example.com');

-- Insert sample feed items
INSERT INTO feed_items (user_id, content, type) VALUES
(1, 'Check out this amazing product!', 'RECOMMENDATION'),
(2, 'Just received my order, love it!', 'REVIEW');

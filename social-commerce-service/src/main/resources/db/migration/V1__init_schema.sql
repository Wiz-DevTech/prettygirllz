-- Initial database schema creation
-- Version: 1.0.0

-- Users table (simplified for this example)
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    enabled BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Chat messages table
CREATE TABLE chat_messages (
    id BIGSERIAL PRIMARY KEY,
    sender_id BIGINT NOT NULL REFERENCES users(id),
    recipient_id BIGINT NOT NULL REFERENCES users(id),
    content TEXT NOT NULL,
    product_id BIGINT,
    conversation_id BIGINT,
    status VARCHAR(20) NOT NULL,
    message_type VARCHAR(20) DEFAULT 'TEXT',
    attachment_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP
);

-- Feed items table
CREATE TABLE feed_items (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    content TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    product_id BIGINT,
    image_url VARCHAR(500),
    likes_count INTEGER DEFAULT 0,
    comments_count INTEGER DEFAULT 0,
    shares_count INTEGER DEFAULT 0,
    engagement_score DOUBLE PRECISION DEFAULT 0.0,
    is_featured BOOLEAN DEFAULT false,
    visibility VARCHAR(20) DEFAULT 'PUBLIC',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Feed item tags
CREATE TABLE feed_item_tags (
    feed_item_id BIGINT NOT NULL REFERENCES feed_items(id) ON DELETE CASCADE,
    tag VARCHAR(50) NOT NULL,
    PRIMARY KEY (feed_item_id, tag)
);

-- Feed item likes
CREATE TABLE feed_item_likes (
    feed_item_id BIGINT NOT NULL REFERENCES feed_items(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (feed_item_id, user_id)
);

-- Feed comments
CREATE TABLE feed_comments (
    id BIGSERIAL PRIMARY KEY,
    feed_item_id BIGINT NOT NULL REFERENCES feed_items(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Moderation decisions table
CREATE TABLE moderation_decisions (
    id BIGSERIAL PRIMARY KEY,
    content_id BIGINT NOT NULL,
    content_type VARCHAR(50) NOT NULL,
    content_snapshot TEXT,
    status VARCHAR(20) NOT NULL,
    reason TEXT,
    moderator_id BIGINT REFERENCES users(id),
    moderator_username VARCHAR(50),
    confidence_score DOUBLE PRECISION,
    is_automated BOOLEAN DEFAULT false,
    reported_by VARCHAR(50),
    report_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP,
    UNIQUE(content_id, content_type)
);

-- Moderation flags
CREATE TABLE moderation_flags (
    decision_id BIGINT NOT NULL REFERENCES moderation_decisions(id) ON DELETE CASCADE,
    flag VARCHAR(50) NOT NULL,
    PRIMARY KEY (decision_id, flag)
);

-- Moderation appeals
CREATE TABLE moderation_appeals (
    id BIGSERIAL PRIMARY KEY,
    decision_id BIGINT NOT NULL REFERENCES moderation_decisions(id),
    appellant_id BIGINT NOT NULL REFERENCES users(id),
    reason TEXT NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    reviewer_id BIGINT REFERENCES users(id),
    review_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP
);

-- Indexes for better performance
CREATE INDEX idx_chat_messages_conversation ON chat_messages(conversation_id);
CREATE INDEX idx_chat_messages_sender_recipient ON chat_messages(sender_id, recipient_id);
CREATE INDEX idx_feed_items_user ON feed_items(user_id);
CREATE INDEX idx_feed_items_created ON feed_items(created_at DESC);
CREATE INDEX idx_feed_items_engagement ON feed_items(engagement_score DESC);
CREATE INDEX idx_moderation_decisions_content ON moderation_decisions(content_id, content_type);
CREATE INDEX idx_moderation_decisions_status ON moderation_decisions(status);

-- Update timestamp trigger function
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$ language 'plpgsql';

-- Create triggers for updated_at
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_chat_messages_updated_at BEFORE UPDATE ON chat_messages
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_feed_items_updated_at BEFORE UPDATE ON feed_items
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_moderation_decisions_updated_at BEFORE UPDATE ON moderation_decisions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

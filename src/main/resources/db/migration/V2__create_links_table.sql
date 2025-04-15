CREATE TABLE IF NOT EXISTS links (
     id SERIAL PRIMARY KEY,
     original_url TEXT NOT NULL CHECK (original_url ~* '^(http|https)://'),
     short_url VARCHAR(12) NOT NULL UNIQUE CHECK (short_url ~ '^[a-zA-Z0-9]+$'),
     user_id INTEGER NOT NULL REFERENCES users(id),
     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
     expires_at TIMESTAMP,
    click_count INTEGER DEFAULT 0,
     CHECK (char_length(original_url) < 2048)
);
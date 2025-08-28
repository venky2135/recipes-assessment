-- Optional indexes for MySQL
CREATE INDEX IF NOT EXISTS idx_recipes_rating ON recipes (rating);
CREATE INDEX IF NOT EXISTS idx_recipes_cuisine ON recipes (cuisine);
-- Drop existing table if re-running
DROP TABLE IF EXISTS recipes;

-- Create recipes table
CREATE TABLE recipes (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    cuisine VARCHAR(100),
    rating DECIMAL(3,2),
    total_time INT,       -- in minutes
    calories INT,
    ingredients TEXT,     -- comma separated or JSON depending on need
    instructions TEXT
);

-- Optional: Create index for faster search
CREATE INDEX idx_recipes_title ON recipes(title);
CREATE INDEX idx_recipes_cuisine ON recipes(cuisine);
CREATE INDEX idx_recipes_rating ON recipes(rating);

const express = require('express');
const { Pool } = require('pg');

const app = express();
const pool = new Pool({
    user: 'gateway',
    password: 'secret',
    host: 'localhost',
    database: 'gateway_cache',
    port: 5432
});

// Database verification
async function verifyDatabase() {
    try {
        // 1. Check connection
        await pool.query('SELECT 1');

        // 2. Check table exists
        const { rows } = await pool.query(`
      SELECT EXISTS (
        SELECT FROM information_schema.tables 
        WHERE table_name = 'ssr_cache'
      ) as table_exists
    `);

        if (!rows[0].table_exists) {
            throw new Error('ssr_cache table not found');
        }

        // 3. Check data exists
        const data = await pool.query('SELECT * FROM ssr_cache LIMIT 1');
        console.log(`✅ Database ready with ${data.rows.length} records`);
        return true;
    } catch (err) {
        console.error('❌ Database verification failed:', err.message);
        return false;
    }
}

// Routes
app.get('/fallback/:route', async (req, res) => {
    try {
        const routeKey = `/${req.params.route.replace(/^\/*/, '')}`;
        const { rows } = await pool.query(
            `SELECT html FROM ssr_cache 
       WHERE route = $1 AND expiry > NOW()`,
            [routeKey]
        );
        res.send(rows[0]?.html || '<div>No cached version</div>');
    } catch (err) {
        res.status(500).send('Database error');
    }
});

app.get('/test-db', async (req, res) => {
    try {
        const { rows } = await pool.query('SELECT * FROM ssr_cache');
        res.json(rows);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// Start server
(async () => {
    if (!await verifyDatabase()) {
        console.error('Cannot start server without valid database connection');
        process.exit(1);
    }

    app.listen(3000, () => {
        console.log('Server running on http://localhost:3000');
        console.log('Test with:');
        console.log('curl http://localhost:3000/test-db');
        console.log('curl http://localhost:3000/fallback/home');
    });
})();
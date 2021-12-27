const pool = require('./postgres-config');

class StateRepository {
  async insertNewState(userId, active, state) {
    const query = 'INSERT INTO eg_webchat_state_v2 (user_id, active, state) VALUES ($1, $2, $3)';
    const result = await pool.query(query, [userId, active, state]);
    return result;
  }

  async updateState(userId, active, state) {
    const query = 'UPDATE eg_webchat_state_v2 SET active = $2, state = $3 WHERE user_id = $1';
    const result = await pool.query(query, [userId, active, state]);
    return result;
  }

  async getActiveStateForUserId(userId) {
    const query = 'SELECT (state) FROM eg_webchat_state_v2 WHERE user_id = $1 AND active = true';
    const result = await pool.query(query, [userId]);
    if (result.rowCount >= 1) {
      const { state } = result.rows[0];
      return state;
    }
  }
}

module.exports = new StateRepository();

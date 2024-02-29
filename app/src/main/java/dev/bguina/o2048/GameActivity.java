package dev.bguina.o2048;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import dev.bguina.R;

public class GameActivity extends Activity implements GameListener {

    private static final String PREFS_NAME = "game_data";
    private GameGridAdapter mAdapter;
    private TextView mScoreView;
    private Integer mHighScore;
    private TextView mHighScoreView;
    private TextView mGameStateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        mScoreView = findViewById(R.id.score);
        mHighScoreView = findViewById(R.id.high_score);
        mGameStateView = findViewById(R.id.game_state);

        SharedPreferences gameData = getSharedPreferences(PREFS_NAME, 0);
        mHighScore = gameData.getInt("high_score", 0);
        mHighScoreView.setText(mHighScore.toString());

        RecyclerView mRecyclerView = findViewById(R.id.game_gridview);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 4);
        mLayoutManager.scrollToPosition(0);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new GameGridAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        SwipeDetector touchListener = new SwipeDetector(mRecyclerView, mAdapter);
        mRecyclerView.setOnTouchListener(touchListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences gameData = getSharedPreferences(PREFS_NAME, 0);
        gameData.edit()
                .putInt("high_score", mHighScore)
                .apply();
    }

    @Override
    public void onScoreUpdate() {
        final Integer score = mAdapter.getGame().getScore();
        runOnUiThread(() -> mScoreView.setText(score.toString()));
        if (score > mHighScore) {
            mHighScore = score;
            runOnUiThread(() -> mHighScoreView.setText(mHighScore.toString()));
        }
    }

    @Override
    public void onWin() {
        mGameStateView.setText(R.string.win);
    }

    @Override
    public void onLose() {
        mGameStateView.setText(R.string.lose);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (R.id.action_reset == id) {
            mGameStateView.setText(null);
            mAdapter.getGame().startGame();
            mAdapter.notifyDataSetChanged();
            return true;
        }

        if (R.id.action_save == id) {
            mAdapter.getGame().save(this);
            return true;
        }

        if (R.id.action_load == id) {
            mGameStateView.setText(null);
            mAdapter.getGame().load(this);
            mAdapter.notifyDataSetChanged();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

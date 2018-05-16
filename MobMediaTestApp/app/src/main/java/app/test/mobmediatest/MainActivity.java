package app.test.mobmediatest;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String SAVED_STATE_KEY = "saved_state";

    private DrawerLayout mDrawer;
    private WebView mWebView;
    private State mCurrentState = State.HOME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mWebView = findViewById(R.id.web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.setWebViewClient(new GameWebViewClient());
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            if (mCurrentState != State.HOME) {
                mCurrentState = State.HOME;
                updateWebView();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateWebView();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_main_screen:
                mCurrentState = State.HOME;
                break;
            case R.id.nav_game_1:
                mCurrentState = State.GAME_1;
                break;
            case R.id.nav_game_2:
                mCurrentState = State.GAME_2;
                break;
            default:
                throw new IllegalArgumentException("Unknown navigation item");
        }
        updateWebView();
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(SAVED_STATE_KEY, mCurrentState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCurrentState = (State) savedInstanceState.getSerializable(SAVED_STATE_KEY);
    }

    private void updateWebView() {
        switch (mCurrentState) {
            case HOME:
                mWebView.reload();
                mWebView.loadUrl("file:///android_asset/home/home.html");
                break;
            case GAME_1:
                mWebView.loadUrl("file:///android_asset/game1/index.html");
                break;
            case GAME_2:
                mWebView.loadUrl("file:///android_asset/game2/index.html");
                break;
        }
    }

    private enum State {
        HOME, GAME_1, GAME_2
    }

    private class GameWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (URLUtil.isNetworkUrl(url)) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                getApplicationContext().startActivity(intent);
                return true;
            } else {
                if (url.equals("star://do") || url.equals("openleaderboard://do") ||
                        url.startsWith("savescore://")) {
                    return true; // unsupported urls, just ignore them
                }
            }
            return false;
        }
    }
}

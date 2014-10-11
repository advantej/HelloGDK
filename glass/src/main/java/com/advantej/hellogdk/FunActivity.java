package com.advantej.hellogdk;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.glass.view.WindowUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Random;


public class FunActivity extends Activity implements TextToSpeech.OnInitListener {

    private ImageView mImageViewJokeBkgnd;
    private TextView mTextViewJoke;
    AsyncHttpClient mAsyncHttpClient = new AsyncHttpClient();

    private final String mJokeUrl = "http://api.icndb.com/jokes/random?limitTo=[nerdy]";
    private final String mImageUrl = "http://lorempixel.com/640/360/cats/";

    private TextToSpeech mTextToSpeech;
    private boolean mSpeechInitialized = false;
    private String mJokeText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);

        setContentView(R.layout.activity_fun);

        mTextToSpeech = new TextToSpeech(this, this);

        mImageViewJokeBkgnd = (ImageView) findViewById(R.id.iv_joke_background);
        mTextViewJoke = (TextView) findViewById(R.id.tv_joke);

        mImageViewJokeBkgnd.setImageResource(R.drawable.ic_launcher);
        mTextViewJoke.setText(getString(R.string.loading_fun));

        mAsyncHttpClient.get(this, mJokeUrl, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONObject valueObject = response.optJSONObject("value");
                if (valueObject != null) {
                    final String joke = valueObject.optString("joke");
                    mTextViewJoke.post(new Runnable() {
                        @Override
                        public void run() {
                            mTextViewJoke.setText(joke);
                            mJokeText = joke;
                        }
                    });
                }
            }
        });

        String imageUrl = mImageUrl + randInt(1, 10);

        Picasso.with(this).load(imageUrl).into(mImageViewJokeBkgnd);

    }

    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS ||
                featureId == Window.FEATURE_OPTIONS_PANEL) {
            getMenuInflater().inflate(R.menu.fun, menu);
            return true;
        }
        return super.onCreatePanelMenu(featureId, menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS ||
                featureId == Window.FEATURE_OPTIONS_PANEL) {
            switch (item.getItemId()) {
                case R.id.menu_read_aloud:
                    readAloud(mJokeText);
                    break;
            }
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
                openOptionsMenu();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            mTextToSpeech.setLanguage(Locale.ENGLISH);
            mSpeechInitialized = true;
        }
    }

    private void readAloud(String text) {
        if (mSpeechInitialized) {
            mTextToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTextToSpeech.shutdown();
    }
}

package com.advantej.hellogdk;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fun, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_read_aloud:
                readAloud(mJokeText);
                break;
        }
        return super.onOptionsItemSelected(item);
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

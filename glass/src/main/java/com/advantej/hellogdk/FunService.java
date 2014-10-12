package com.advantej.hellogdk;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.google.android.glass.timeline.LiveCard;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apache.http.Header;
import org.json.JSONObject;

public class FunService extends Service {

    private static final String LIVE_CARD_TAG = "FunCard";
    private static final int DELAY = 5000;

    private LiveCard mLiveCard;
    private RemoteViews mLiveCardView;

    private Handler mHandler = new Handler();
    private UpdateLiveCardRunnable mUpdateLiveCardRunnable = new UpdateLiveCardRunnable();
    private Bitmap mLoadedBackgroundImage;

    public FunService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mLiveCard == null) {
            //Some logic to create, publish and possibly periodically update the live card

            mLiveCard = new LiveCard(this, LIVE_CARD_TAG);
            mLiveCardView = new RemoteViews(getPackageName(), R.layout.activity_fun);

            mLiveCardView.setTextViewText(R.id.tv_joke, getString(R.string.loading_fun));
//            Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
//            mLiveCardView.setImageViewBitmap(R.id.iv_joke_background, background);

            Intent startMenu = new Intent(this, LiveCardMenuActivity.class);
            startMenu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mLiveCard.setAction(PendingIntent.getActivity(this, 0, startMenu, 0));

            mLiveCard.setViews(mLiveCardView);
            mLiveCard.publish(LiveCard.PublishMode.REVEAL);

            mHandler.post(mUpdateLiveCardRunnable);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mLiveCard != null && mLiveCard.isPublished()) {
            mUpdateLiveCardRunnable.setStopped(true);
            mLiveCard.unpublish();
            mLiveCard = null;
        }
        super.onDestroy();
    }

    private class UpdateLiveCardRunnable implements Runnable {
        private boolean mIsStopped;
        AsyncHttpClient mAsyncHttpClient = new AsyncHttpClient();
        private final String mJokeUrl = "http://api.icndb.com/jokes/random?limitTo=[nerdy]";
        private final String mImageUrl = "http://lorempixel.com/640/360/cats/";
        private String mJoke = "";

        @Override
        public void run() {
            if (mIsStopped)
                return;

            mAsyncHttpClient.get(FunService.this, mJokeUrl, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    if (mIsStopped)
                        return;

                    JSONObject valueObject = response.optJSONObject("value");
                    if (valueObject != null) {
                        mJoke = valueObject.optString("joke");
                        mLiveCardView.setTextViewText(R.id.tv_joke, mJoke);
                        if (mLoadedBackgroundImage != null) {
                            mLiveCardView.setImageViewBitmap(R.id.iv_joke_background, mLoadedBackgroundImage);

                        }
                        mLiveCard.setViews(mLiveCardView);
                    }

                    mHandler.postDelayed(mUpdateLiveCardRunnable, DELAY);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                }
            });

            if (mLoadedBackgroundImage == null) {
                String imageUrl = mImageUrl + FunActivity.randInt(1, 10);
                Picasso.with(FunService.this).load(imageUrl).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        mLoadedBackgroundImage = bitmap;
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
            }
        }

        public boolean isStopped() {
            return mIsStopped;
        }

        public void setStopped(boolean isStopped) {
            this.mIsStopped = isStopped;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

package com.advantej.hellogdk;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.google.android.glass.timeline.LiveCard;

public class FunService extends Service {

    private static final String LIVE_CARD_TAG = "FunCard";

    private LiveCard mLiveCard;
    private RemoteViews mLiveCardView;

    public FunService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mLiveCard == null) {
            //Some logic to create, publish and possibly periodically update the live card

            mLiveCard = new LiveCard(this, LIVE_CARD_TAG);
            mLiveCardView = new RemoteViews(getPackageName(), R.layout.activity_fun);

            mLiveCardView.setTextViewText(R.id.tv_joke, "This is funny, Really !");
            Bitmap  background = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
            mLiveCardView.setImageViewBitmap(R.id.iv_joke_background, background);

            //Won't work since there is not setAction

            mLiveCard.setViews(mLiveCardView);
            mLiveCard.publish(LiveCard.PublishMode.REVEAL);

            // TODO Insert logic here to periodically update the live card
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mLiveCard != null && mLiveCard.isPublished()) {
            mLiveCard.unpublish();
            mLiveCard = null;
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

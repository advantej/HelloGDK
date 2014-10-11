package com.advantej.hellogdk;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link Activity} showing a tuggable "Hello World!" card.
 * <p>
 * The main content view is composed of a one-card {@link CardScrollView} that provides tugging
 * feedback to the user when swipe gestures are detected.
 * If your Glassware intends to intercept swipe gestures, you should set the content view directly
 * and use a {@link com.google.android.glass.touchpad.GestureDetector}.
 * @see <a href="https://developers.google.com/glass/develop/gdk/touch">GDK Developer Guide</a>
 */
public class MainActivity extends Activity {

    /** {@link CardScrollView} to use as the main content view. */
    private CardScrollView mCardScroller;

    private List<View> mViews;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initViews();


        mCardScroller = new CardScrollView(this);
        mCardScroller.setAdapter(new CardScrollAdapter() {
            @Override
            public int getCount() {
                return mViews.size();
            }

            @Override
            public Object getItem(int position) {
                return mViews.get(position);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return mViews.get(position);
            }

            @Override
            public int getPosition(Object item) {
                return 0;
            }
        });
        // Handle the TAP event.
        mCardScroller.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Plays disallowed sound to indicate that TAP actions are not supported.
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.playSoundEffect(Sounds.DISALLOWED);
            }
        });
        setContentView(mCardScroller);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCardScroller.activate();
    }

    @Override
    protected void onPause() {
        mCardScroller.deactivate();
        super.onPause();
    }

    private void initViews() {
        mViews = new ArrayList<View>();
        for (int i = 0; i < 5; i++) {
           mViews.add(buildView(i));
        }
    }

    /**
     * Builds a Glass styled "Hello World!" view using the {@link CardBuilder} class.
     */
    private View buildView(int i) {
        CardBuilder card = new CardBuilder(this, CardBuilder.Layout.TEXT);

        card.setText(getString(R.string.i_am_card, i));
        return card.getView();
    }

}

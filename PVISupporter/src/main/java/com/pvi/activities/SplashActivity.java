package com.pvi.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

public class SplashActivity extends Activity {

	// animation duration, can find in fad_in and fad_out xml files
		// in anim folder

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			setContentView(R.layout.activity_splash_screen);

			// get the instace of image view
			ImageView image = (ImageView) findViewById(R.id.imageViewSplash);

			// create animation object
			Animation fad = AnimationUtils.loadAnimation(getBaseContext(),
					R.anim.splash_fade_in);

			// create animation listener
			fad.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationEnd(Animation animation) {
					Intent i = new Intent(SplashActivity.this,
							MenuActivity.class);
					startActivity(i);
					finish();

					overridePendingTransition(R.anim.splash_fade_in,
							R.anim.splash_fade_out);

				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub
				}

			});

			// start animation
			image.startAnimation(fad);
		}

		// disable back button during splash screen
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			return false;
		}
}

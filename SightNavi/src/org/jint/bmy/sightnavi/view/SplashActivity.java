package org.jint.bmy.sightnavi.view;

import org.jint.bmy.sightnavi.R;
import org.jint.bmy.sightnavi.view.SelectLanguageActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * @author jintian
 * 
 */
public class SplashActivity extends BaseActivity {
	/** Called when the activity is first created. */
	@Override
	protected void onCreateMainView() {
		//super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		Handler x = new Handler();
		x.postDelayed(new splashhandler(), 5000);
		
	}

	class splashhandler implements Runnable {

		public void run() {
			startActivity(new Intent(getApplication(), SelectLanguageActivity.class));
			SplashActivity.this.finish();
		}

	}

	
}

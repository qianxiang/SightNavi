package org.jint.bmy.sightnavi.view;

import org.jint.bmy.sightnavi.ApplicationContext;
import org.jint.bmy.sightnavi.R;
import org.jint.bmy.sightnavi.view.SelectLanguageActivity;
import org.jint.util.FileUtil;
import org.jint.util.LogUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

/**
 * @author jintian
 * 
 */
public class SplashActivity extends BaseActivity {
	/** Called when the activity is first created. */
	@Override
	protected void onCreateMainView() {
		// super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		Handler x = new Handler();
		x.postDelayed(new splashhandler(), 6000);

	}

	class splashhandler implements Runnable {

		/*
		 * 检查图片和录音文件是否存在，如果不存在，转去下载画面，如果存在转去景点列表画面。
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			String contentPath = ApplicationContext.getInstance()
					.getApplicationStoragePath() + "/zh";
			LogUtil.debug(contentPath);
			
			if( FileUtil.isFileExist(contentPath) ){
				startActivity(new Intent(getApplication(),
						SelectLanguageActivity.class));
			}else{
				startActivity(new Intent(getApplication(),
						DownloadContentActivity.class));
			}
			
			

			

			SplashActivity.this.finish();
		}

	}

}

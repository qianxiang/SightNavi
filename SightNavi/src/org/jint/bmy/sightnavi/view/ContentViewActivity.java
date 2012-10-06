package org.jint.bmy.sightnavi.view;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.jint.bmy.sightnavi.ApplicationContext;
import org.jint.bmy.sightnavi.R;
import org.jint.bmy.sightnavi.model.Sight;
import org.jint.util.FileUtil;

import android.media.MediaPlayer;
import android.os.Handler;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;

/**
 * @author jintian
 * 
 */
public class ContentViewActivity extends BaseActivity {
	private static final int PLAY_STATE_PLAY = 1;
	private static final int PLAY_STATE_PAUSE = 2;
	private static final int PLAY_STATE_STOP = 3;

	private WebView contentWebView;
	private ImageButton playButton;
	private SeekBar audioPlayBar;
	private Button playTypeButton;
	private int playState;
	private Timer audioPlayTimer;
	private MediaPlayer mediaPlayer;
	private Handler handler;
	private Sight sight;
	private String audioPath;
	private String contentPath;

	@Override
	protected void onCreateMainView() {
		setMainView(R.layout.content_view);

		// 获取选中的景点
		sight = (Sight) getIntent().getSerializableExtra("sight");

		handler = new Handler();

		playState = PLAY_STATE_STOP;

		audioPath = ApplicationContext.getInstance()
				.getApplicationStoragePath() + sight.getAudio();

		contentPath = ApplicationContext.getInstance()
				.getApplicationStoragePath() + sight.getContent();

		contentWebView = (WebView) findViewById(R.id.contentWebView);
		WebSettings webSettings = contentWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		// webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		contentWebView.loadUrl("file://" + contentPath);

		// 音频播放条
		View audioBar = layoutInflater.inflate(R.layout.content_view_audio_bar,
				null);
		navigationBar.setTitleView(audioBar);
		playButton = (ImageButton) findViewById(R.id.playButton);
		playButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				toggleAudio();
			}
		});

		audioPlayBar = (SeekBar) findViewById(R.id.audioPlayBar);
		audioPlayBar
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {

					}

					public void onStartTrackingTouch(SeekBar seekBar) {
						pauseAudio();
					}

					public void onStopTrackingTouch(SeekBar seekBar) {
						gotoPlayAudio(audioPlayBar.getProgress());
					}

				});

		playTypeButton = (Button) layoutInflater.inflate(
				R.layout.navigation_bar_button, null);
		playTypeButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				playTypeButtonOnClick(v);
			}
		});
		if (ApplicationContext.getInstance().getPlayType() == ApplicationContext.PLAY_TYPE_AUTO) {
			playTypeButton.setText(getString(R.string.auto));
		} else {
			playTypeButton.setText(getString(R.string.manual));
		}
		navigationBar.addRightView(playTypeButton);

		// 自动播放音频
		if (ApplicationContext.getInstance().getPlayType() == ApplicationContext.PLAY_TYPE_AUTO) {
			playAudio();
		}

		// 显示标题
		// navigationBar.setTitle(sight.getName());
	}

	@Override
	public void finish() {
		stopAudio();
		super.finish();
	}

	@Override
	protected void onDestroy() {
		stopAudio();
		super.onDestroy();
	}

	public void playTypeButtonOnClick(View v) {
		if (ApplicationContext.getInstance().getPlayType() == ApplicationContext.PLAY_TYPE_AUTO) {
			ApplicationContext.getInstance().setPlayType(
					ApplicationContext.PLAY_TYPE_MANUAL);
			playTypeButton.setText(getString(R.string.manual));
			stopAudio();
		} else {
			ApplicationContext.getInstance().setPlayType(
					ApplicationContext.PLAY_TYPE_AUTO);
			playTypeButton.setText(getString(R.string.auto));
			playAudio();
		}
	}

	private void toggleAudio() {
		if (playState == PLAY_STATE_PLAY) {
			pauseAudio();
		} else {
			playAudio();
		}
	}

	private void playAudio() {
		// LogUtil.debug("Start play audio");

		playButton.setImageResource(android.R.drawable.ic_media_pause);

		if (playState == PLAY_STATE_PAUSE) {
			playState = PLAY_STATE_PLAY;

			if (mediaPlayer != null) {
				mediaPlayer.start();
			}

			startAudioPlayTimer();

			return;
		}

		playState = PLAY_STATE_PLAY;

		mediaPlayer = new MediaPlayer();
		mediaPlayer
				.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

					public void onCompletion(MediaPlayer mp) {
						mediaPlayerOnCompletaion(mp);
					}
				});

		// 如果音频文件存在，则播放
		if (FileUtil.isFileExist(audioPath)) {
			try {
				mediaPlayer.setDataSource(audioPath);
				mediaPlayer.prepare();
				mediaPlayer.start();

				audioPlayBar.setMax(mediaPlayer.getDuration());
				audioPlayBar.setProgress(0);

				startAudioPlayTimer();
			} catch (IOException e) {
				showToastMessage("Can't play audio " + e);
			}
		} else {
			showToastMessage("无语音讲解。");
		}

	}

	protected void mediaPlayerOnCompletaion(MediaPlayer mp) {
		if (ApplicationContext.getInstance().getPlayType() == ApplicationContext.PLAY_TYPE_AUTO) {
			finish();
		} else if (ApplicationContext.getInstance().getPlayType() == ApplicationContext.PLAY_TYPE_MANUAL) {
			stopAudio();
		}
	}

	private void gotoPlayAudio(int position) {
		// LogUtil.debug("Goto play audio");

		playButton.setImageResource(android.R.drawable.ic_media_pause);
		playState = PLAY_STATE_PLAY;

		if (mediaPlayer != null) {
			mediaPlayer.seekTo(position);
			mediaPlayer.start();

			startAudioPlayTimer();
		}
	}

	private void pauseAudio() {
		// LogUtil.debug("Pause audio");

		playState = PLAY_STATE_PAUSE;

		stopAudioPlayTimer();

		playButton.setImageResource(android.R.drawable.ic_media_play);

		if (mediaPlayer != null) {
			mediaPlayer.pause();
		}
	}

	private void stopAudio() {
		// LogUtil.debug("Stop play audio");

		if (mediaPlayer != null && playState != PLAY_STATE_STOP) {
			mediaPlayer.stop();
			mediaPlayer.release();
		}

		playState = PLAY_STATE_STOP;

		playButton.setImageResource(android.R.drawable.ic_media_play);
		stopAudioPlayTimer();
		audioPlayBar.setProgress(0);
	}

	private void startAudioPlayTimer() {
		audioPlayTimer = new Timer();
		audioPlayTimer.scheduleAtFixedRate(new AudioPlayTimerTask(), 0, 1000);
	}

	private void stopAudioPlayTimer() {

		if (audioPlayTimer != null) {
			audioPlayTimer.cancel();
		}
	}

	private void addAudioPlayTime() {

		if (playState != PLAY_STATE_PLAY) {
			return;
		}

		int position = mediaPlayer.getCurrentPosition();
		audioPlayBar.setProgress(position);

	}

	class AudioPlayTimerTask extends TimerTask {

		@Override
		public void run() {
			handler.post(new Runnable() {

				public void run() {
					addAudioPlayTime();
				}
			});

		}

	}
}

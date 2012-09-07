package org.jint.bmy.sightnavi.view;

import java.util.ArrayList;
import java.util.Locale;

import org.jint.bmy.sightnavi.ApplicationContext;
import org.jint.bmy.sightnavi.model.Service;
import org.jint.bmy.sightnavi.model.Sight;
import org.jint.bmy.sightnavi.model.SightBSLoadSightsByLocale;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.sourcethought.sightnavi.R;

/**
 * @author jintian
 *
 */
public class SelectLanguageActivity extends BaseActivity {
	private Button startButton;
	private Button playTypeButton;
	private RadioGroup languageRadioGroup;
	private SightBSLoadSightsByLocale sightBSLoadSightsByLocale;

	@Override
	protected void onCreateMainView() {
		setMainView(R.layout.select_language);

		languageRadioGroup = (RadioGroup) findViewById(R.id.languageRadioGroup);
		languageRadioGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					public void onCheckedChanged(RadioGroup group, int checkedId) {
						languageRadioGroupOnCheckedChange(group, checkedId);
					}
				});

		startButton = (Button) findViewById(R.id.startButton);
		startButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				startButtonOnClick(v);
			}
		});

		// 根据当前的播放状态，设置右上角按钮的内容
		playTypeButton = (Button) layoutInflater.inflate(
				R.layout.navigation_bar_button, null);
		if (ApplicationContext.getInstance().getPlayType() == ApplicationContext.PLAY_TYPE_AUTO) {
			playTypeButton.setText(getString(R.string.auto));
		} else {
			playTypeButton.setText(getString(R.string.manual));
		}
		playTypeButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				playTypeButtonOnClick(v);
			}
		});
		navigationBar.addRightView(playTypeButton);

		// 初始化业务服务
		sightBSLoadSightsByLocale = new SightBSLoadSightsByLocale();
		sightBSLoadSightsByLocale
				.setOnSuccessHandler(new Service.OnSuccessHandler() {

					public void onSuccess(Object result) {
						sightBSLoadSightsByLocaleOnSuccess(result);
					}
				});
		sightBSLoadSightsByLocale
				.setOnFaultHandler(new Service.OnFaultHandler() {

					public void onFault(Exception ex) {
						sightBSLoadSightsByLocaleOnFault(ex);
					}
				});
	
		// 设置默认语言为中文选中
		languageRadioGroup.check(R.id.chineseRadio);
	}

	@Override
	protected void onLocaleChange(Locale newLocale) {
		super.onLocaleChange(newLocale);
		
		// 根据当前的播放状态，设置右上角按钮的内容
		if (ApplicationContext.getInstance().getPlayType() == ApplicationContext.PLAY_TYPE_AUTO) {
			playTypeButton.setText(getString(R.string.auto));
		} else {
			playTypeButton.setText(getString(R.string.manual));
		}
		
		startButton.setText(getString(R.string.start));
	}

	protected void sightBSLoadSightsByLocaleOnSuccess(Object result) {
		hideProgressDialog();
		ApplicationContext.getInstance().setSights((ArrayList<Sight>) result);

	}

	protected void sightBSLoadSightsByLocaleOnFault(Exception ex) {
		hideProgressDialog();
		showToastMessage(ex.getLocalizedMessage());
	}

	protected void gotoSearchSightSpot() {
		Intent intent = new Intent(this, SearchSightSpotActivity.class);
		startActivity(intent);
	}

	protected void loadSightData() {
		showProgressDialog(null);
		sightBSLoadSightsByLocale.setLocale(getLocale());
		sightBSLoadSightsByLocale.setContext(this);
		sightBSLoadSightsByLocale.asyncExecute();
	}

	public void languageRadioGroupOnCheckedChange(RadioGroup group,
			int checkedId) {
		// 设置语言，重新加载景点内容
		if (checkedId == R.id.chineseRadio) {
			setLocale(Locale.CHINESE);
		} else if (checkedId == R.id.englishRadio) {
			setLocale(Locale.ENGLISH);
		}
		
		loadSightData();
	}

	public void startButtonOnClick(View v) {
		gotoSearchSightSpot();
	}

	public void playTypeButtonOnClick(View v) {
		if (ApplicationContext.getInstance().getPlayType() == ApplicationContext.PLAY_TYPE_AUTO) {
			ApplicationContext.getInstance().setPlayType(
					ApplicationContext.PLAY_TYPE_MANUAL);
			playTypeButton.setText(getString(R.string.manual));
		} else {
			ApplicationContext.getInstance().setPlayType(
					ApplicationContext.PLAY_TYPE_AUTO);
			playTypeButton.setText(getString(R.string.auto));
		}
	}
}
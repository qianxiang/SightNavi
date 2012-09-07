package org.jint.bmy.sightnavi.view;

import java.io.File;
import java.util.Locale;

import org.jint.bmy.sightnavi.R;
import org.jint.bmy.sightnavi.util.LogUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;


/**
 * @author jintian
 *
 */
public abstract class BaseActivity extends Activity {
	protected LayoutInflater layoutInflater;
	protected ViewGroup rootLayout;
	protected RelativeLayout mainRelativeLayout;
	protected NavigationBar navigationBar;
	protected ProgressDialog progressDialog;
	protected AlertDialog alertDialog;
	protected Toast toast;
	
	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LogUtil.setTag("SightNavi");
		LogUtil.setWriteToFile(true);
		String logFilePath = Environment.getExternalStorageDirectory()
				.getPath()
				+ File.separator
				+ "SightNavi"
				+ File.separator
				+ "log.txt";
		File logFile = new File(logFilePath);
		if (!logFile.getParentFile().exists()) {
			logFile.getParentFile().mkdirs();
		}
		LogUtil.setLogFilePath(logFilePath);

		// 全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
				WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		// 去掉标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 设置方向竖屏
		setRequestedOrientation(screenOrientation());
		// 加载根布局
		layoutInflater = LayoutInflater.from(this);
		rootLayout = (ViewGroup)layoutInflater.inflate(
				R.layout.base, null);
		setContentView(rootLayout);

		// 加载导航栏
		navigationBar = (NavigationBar) findViewById(R.id.navigationBar);
		mainRelativeLayout = (RelativeLayout) findViewById(R.id.mainRelativeLayout);

		// 创建进度等待对话框
		progressDialog = new ProgressDialog(this);

		// 创建Toast对话框
		toast = Toast.makeText(this, null, Toast.LENGTH_SHORT);

		// 创建主视图
		createMainView();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// 检测sdcard
		if (!isSDCardMounted()) {
			showAlertDialog(this, getString(R.string.mount_sdcard), new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					exitApp();
				}
			});
		}

		// 检测蓝牙是否启用，如果没有启用，则先开启蓝牙
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		if (!bluetoothAdapter.isEnabled()) {
			bluetoothAdapter.enable();
		}
	}

	protected int screenOrientation(){
		return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
	}
	
	protected void setMainView(int resId) {
		layoutInflater.inflate(resId, mainRelativeLayout);
	}

	protected void createMainView() {
		onCreateMainView();
	}

	protected boolean isSDCardMounted() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	protected void setLocale(Locale newLocale) {

		// 更新默认语言
		Locale.setDefault(newLocale);

		Configuration config = getBaseContext().getResources()
				.getConfiguration();

		config.locale = newLocale;
		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());

		onLocaleChange(newLocale);
	}

	protected void onLocaleChange(Locale newLocale){
		navigationBar.backButton.setText(getString(R.string.back));
	}
	
	protected Locale getLocale() {
		return Locale.getDefault();
	}

	protected void showProgressDialog(String message) {
		if (message != null) {
			progressDialog.setMessage(message);
		}

		progressDialog.show();
	}

	protected void hideProgressDialog() {
		progressDialog.dismiss();
	}

	protected void showToastMessage(String message) {
		toast.setText(message);
		toast.show();
	}

	protected void hideToastMessage() {
		toast.cancel();
	}

	protected void showAlertDialog(Context context, String message) {

		Builder builder = new Builder(context);
		builder.setMessage(message);
		builder.setCancelable(false);
		builder.setTitle(null);
		builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.show();
	}
	
	protected void showAlertDialog(Context context, String message,
			final DialogInterface.OnClickListener okListener) {

		Builder builder = new Builder(context);
		builder.setMessage(message);
		builder.setCancelable(false);
		builder.setTitle(null);
		builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if (okListener != null) {
					okListener.onClick(dialog, which);
				}
			}
		});

		builder.show();
	}
	
	protected void exitApp(){
		System.exit(0);
	}
	
	protected abstract void onCreateMainView();
}

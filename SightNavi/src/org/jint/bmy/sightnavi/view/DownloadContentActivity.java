package org.jint.bmy.sightnavi.view;

import org.jint.bmy.sightnavi.ApplicationContext;
import org.jint.bmy.sightnavi.R;
import org.jint.util.FileUtil;
import org.jint.util.LogUtil;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author jintian
 * 
 */
public class DownloadContentActivity extends Activity {
	protected static final String TAG = "DownloadMgr";
	private DownloadManager dMgr;
	private TextView tv;
	private Button btn_download;
	private long downloadId;
	private String downloadSiteUrl = "http://108.174.50.201/";
	private String downloadFileName = "contents.zip";  // "a.zip";
	private String downloadPath = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download);
		tv = (TextView) findViewById(R.id.tv);
		btn_download = (Button) findViewById(R.id.btn_download);
	}

	@Override
	protected void onResume() {
		super.onResume();
		dMgr = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		IntentFilter filter = new IntentFilter(
				DownloadManager.ACTION_DOWNLOAD_COMPLETE);
		
		Context context = this;
		downloadPath = context.getExternalFilesDir(null).getAbsolutePath();
		LogUtil.debug("Downloading to dir " + downloadPath);
		
		registerReceiver(mReceiver, filter);
	}

	public void doClick(View view) {
		DownloadManager.Request dmReq = new DownloadManager.Request(
				Uri.parse( downloadSiteUrl + downloadFileName));
		dmReq.setTitle("兵马俑导览多媒体资源");
		dmReq.setDescription("Download for Audio and Pic of BMY.");

		// 选择所要用的网络
		// 如果不限制网络类型 wifi 和 mobile，则不要设置。
		// dmReq.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);

		// 发出通知
		dmReq.setShowRunningNotification(true);
		// 显示下载界面
		dmReq.setVisibleInDownloadsUi(true);
		// 设置下载文件保存目录
		dmReq.setDestinationInExternalFilesDir(this, null, downloadFileName);

		

		downloadId = dMgr.enqueue(dmReq);
		tv.setText("Download started... (" + downloadId + ")");
		btn_download.setVisibility(View.INVISIBLE);
	}

	public BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			Bundle extras = intent.getExtras();
			long doneDownloadId = extras
					.getLong(DownloadManager.EXTRA_DOWNLOAD_ID);
			tv.setText(tv.getText() + "\nDownload finished (" + doneDownloadId
					+ ")");

			// 解压缩文件到工作目录
			String srcPath = downloadPath + "/" + downloadFileName;
			LogUtil.debug(srcPath);
			ApplicationContext ctx = ApplicationContext.getInstance();
			String targetPath = ctx.getApplicationStoragePath() + "/";
			LogUtil.debug(srcPath);
			LogUtil.debug(targetPath);
			FileUtil.Unzip(srcPath, targetPath);

			// 删除下载的文件
			FileUtil.deleteFile(srcPath);
			
			// 显示提示信息
			tv.setText(R.string.please_reopen);
			

			if (downloadId == doneDownloadId)
				LogUtil.debug("Our download has completed.");
		}
	};

	@Override
	protected void onPause() {
		super.onPause();

		unregisterReceiver(mReceiver);
		dMgr = null;
	}

}

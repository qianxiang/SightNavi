package org.jint.bmy.sightnavi.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.jint.bmy.sightnavi.ApplicationContext;
import org.jint.bmy.sightnavi.R;
import org.jint.bmy.sightnavi.model.Device;
import org.jint.bmy.sightnavi.model.Service;
import org.jint.bmy.sightnavi.model.Sight;
import org.jint.bmy.sightnavi.model.SightBSCalculateNearestSight;
import org.jint.util.LogUtil;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 * @author jintian
 *
 */
public class SearchSightSpotActivity extends BaseActivity {

	// activity相关
	private Button playTypeButton;
	private ProgressBar searchProgressBar;
	private TextView searchMessageTextView;
	private GridView sightsGridView;

	// 蓝牙相关
	private static final int SEARCH_DURATION = 6000;
	private ArrayList<Sight> sights;
	private ArrayList<Device> devices;
	private BluetoothAdapter bluetoothAdapter;
	private Timer bluetoothDiscoveryTimer;
	private BluetoothDiscoveryReciver bluetoothDiscoveryReciver;
	private Handler handler;
	private SightBSCalculateNearestSight sightBSCalculateNearestSight;
	private Sight selectedSight;
	private int selectedSightPosition;

	private HashMap<Integer, View> sightItemViews;

	@Override
	protected void onCreateMainView() {
		setMainView(R.layout.search_sight_spot);

		selectedSightPosition = -1;
		devices = new ArrayList<Device>();
		sightItemViews = new HashMap<Integer, View>();

		handler = new Handler();

		playTypeButton = (Button) layoutInflater.inflate(
				R.layout.navigation_bar_button, null);
		playTypeButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				playTypeButtonOnClick(v);
			}
		});
		navigationBar.addRightView(playTypeButton);

		searchProgressBar = (ProgressBar) findViewById(R.id.searchProgressBar);

		searchMessageTextView = (TextView) findViewById(R.id.searchMessageTextView);

		sightsGridView = (GridView) findViewById(R.id.sightsGridView);
		sightsGridView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					public void onItemClick(AdapterView<?> adapterView, View v,
							int positon, long id) {
						sightsGridViewOnItemClick(adapterView, v, positon, id);
					}
				});

		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// 注册蓝牙扫描监听
		bluetoothDiscoveryReciver = new BluetoothDiscoveryReciver();
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(bluetoothDiscoveryReciver, filter);

		// 启动自动或手动加载
		if (ApplicationContext.getInstance().getPlayType() == ApplicationContext.PLAY_TYPE_AUTO) {
			playTypeButton.setText(getString(R.string.auto));
		} else {
			playTypeButton.setText(getString(R.string.manual));
		}

		sightBSCalculateNearestSight = new SightBSCalculateNearestSight();
		sightBSCalculateNearestSight
				.setOnSuccessHandler(new Service.OnSuccessHandler() {

					public void onSuccess(Object result) {
						sightBSCalculateNearestSightOnSuccess(result);

					}
				});
		sightBSCalculateNearestSight
				.setOnFaultHandler(new Service.OnFaultHandler() {

					public void onFault(Exception ex) {
						sightBSCalculateNearestSightOnFault(ex);
					}
				});

		// 加载手动选择的景点列表
		loadSights();
	}

	@Override
	protected void onDestroy() {
		// 取消注册蓝牙扫描监听
		unregisterReceiver(bluetoothDiscoveryReciver);
		stopSearching();
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 启动自动或手动加载
		if (ApplicationContext.getInstance().getPlayType() == ApplicationContext.PLAY_TYPE_AUTO) {
			startSearching();
		} else {
			stopSearching();
		}
	}

	protected void sightsGridViewOnItemClick(AdapterView<?> adapterView,
			View v, int position, long id) {
		stopSearching();
		selectedSight = sights.get(position);
		selectedSightPosition = position;
		lowLightSightItemViews();
		v.setBackgroundResource(R.drawable.sight_item_bg_2);
		gotoContentView();
	}

	protected void startSearching() {
		LogUtil.debug("Discovery start");

		searchProgressBar.setVisibility(View.VISIBLE);
		searchMessageTextView.setText(R.string.sight_searching);

		devices.clear();

		// 取消原来的搜索，然后重新开始搜索
		bluetoothAdapter.cancelDiscovery();
		bluetoothAdapter.startDiscovery();

		// 计时器在蓝牙搜索10秒后，结束蓝牙搜索
		if (bluetoothDiscoveryTimer != null) {
			bluetoothDiscoveryTimer.cancel();
		}
		bluetoothDiscoveryTimer = new Timer();
		bluetoothDiscoveryTimer.schedule(new BluetoothDiscoveryTimerTask(),
				SEARCH_DURATION);
	}

	protected void stopSearching() {
		LogUtil.debug("Discovery stop");

		bluetoothAdapter.cancelDiscovery();
		if (bluetoothDiscoveryTimer != null) {
			bluetoothDiscoveryTimer.cancel();
		}
		searchProgressBar.setVisibility(View.INVISIBLE);
		searchMessageTextView.setText(R.string.sight_stop_searching);
	}

	protected void loadSights() {
		sights = ApplicationContext.getInstance().getSights();

		SightsGridViewAdapter adapter = new SightsGridViewAdapter();
		sightsGridView.setAdapter(adapter);

		// 初始化好GridView中的所有控件
		sightItemViews.clear();

		for (int i = 0; i < sights.size(); i++) {
			View view = layoutInflater.inflate(
					android.R.layout.simple_list_item_1, null);
			TextView text1 = (TextView) view.findViewById(android.R.id.text1);
			String name = sights.get(i).getName();
			text1.setText(name);
			sightItemViews.put(i, view);
		}

	}

	protected void sightBSCalculateNearestSightOnSuccess(Object result) {
		if (null != result) {
			selectedSight = (Sight) result;
			LogUtil.debug("The nearest sight is : " + selectedSight.getId());
			calSelectedSightPosition();
			sightsGridView.setSelection(selectedSightPosition);
			highLightSelectedSightItemAndGotoNext();

		} else {
			// 如果没有匹配的地点，重新搜索
			LogUtil.debug("Can't find sutable sight");
			startSearching();
		}
	}

	protected void sightBSCalculateNearestSightOnFault(Exception ex) {

	}

	protected void lowLightSightItemViews() {
		View selectedSightItemView = sightItemViews.get(selectedSightPosition);

		// 将其他控件设置为非高亮
		for (int i = 0; i < sights.size(); i++) {
			View item = sightItemViews.get(i);
			if (item != selectedSightItemView) {
				item.setBackgroundDrawable(null);
			}
		}
	}

	/**
	 * 高亮选中项，并跳转
	 */
	protected void highLightSelectedSightItemAndGotoNext() {

		final View selectedSightItemView = sightItemViews
				.get(selectedSightPosition);

		// 将其他控件设置为非高亮
		lowLightSightItemViews();

		final Timer timer = new Timer();

		timer.scheduleAtFixedRate(new TimerTask() {
			int flashTime = 0;

			Runnable newThread = new Runnable() {

				@Override
				public void run() {
					flashTime++;

					// 选中项闪烁
					if (flashTime < 6) {
						if (flashTime % 2 == 0) {
							selectedSightItemView
									.setBackgroundResource(R.drawable.sight_item_bg_1);
						} else {
							selectedSightItemView
									.setBackgroundResource(R.drawable.sight_item_bg_2);
						}
					} else if (flashTime > 10) {
						timer.cancel();
						gotoContentView();
					}

				}
			};

			@Override
			public void run() {
				handler.post(newThread);
			}

		}, 0, 100);   // 立即开始执行，每100毫秒一次
	}

	protected void calSelectedSightPosition() {

		for (int i = 0; i < sights.size(); i++) {
			if (sights.get(i) == selectedSight) {
				selectedSightPosition = i;
			}
		}

		LogUtil.debug("High light sight at position " + selectedSightPosition);
	}

	protected void gotoContentView() {
		LogUtil.debug("Devices count: " + devices.size());
		devices.clear();
		LogUtil.debug("Devices count: " + devices.size());

		Intent intent = new Intent(this, ContentViewActivity.class);
		intent.putExtra("sight", selectedSight);
		startActivity(intent);
	}

	public void playTypeButtonOnClick(View v) {
		if (ApplicationContext.getInstance().getPlayType() == ApplicationContext.PLAY_TYPE_AUTO) {
			ApplicationContext.getInstance().setPlayType(
					ApplicationContext.PLAY_TYPE_MANUAL);
			playTypeButton.setText(getString(R.string.manual));
			stopSearching();
		} else {
			ApplicationContext.getInstance().setPlayType(
					ApplicationContext.PLAY_TYPE_AUTO);
			playTypeButton.setText(getString(R.string.auto));
			startSearching();
		}
	}

	class BluetoothDiscoveryReciver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (BluetoothDevice.ACTION_FOUND.equals(action)) {

				BluetoothDevice bluetoothDevice = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

				short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,
						Short.MIN_VALUE);

				Device device = new Device();
				device.setRssi(rssi);
				device.setBluetoothDevice(bluetoothDevice);

				// LogUtil.debug("Discovery device " + device);

				devices.add(device);
			}
		}
	}

	protected void sortDevices() {
		int devicesCount = devices.size();
		for (int i = 0; i < devicesCount; i++) {
			for (int j = i + 1; j < devicesCount; j++) {
				Device orig = devices.get(i);
				Device target = devices.get(j);

				if (target.getRssi() > orig.getRssi()) {
					Device tmp = orig;
					devices.set(i, target);
					devices.set(j, tmp);
				}
			}
		}
	}

	class BluetoothDiscoveryTimerTask extends TimerTask {

		@Override
		public void run() {
			// 如果没有搜索到蓝牙设备，重新搜索
			LogUtil.debug("Discovery finish, devices " + devices.size());

			handler.post(new Runnable() {

				public void run() {
					// 排序
					sortDevices();

					// 日志输出信号最强的10个景点
					int len = devices.size();
					if (len > 10) {
						len = 10;
					}
					for (int i = 0; i < len; i++) {
						LogUtil.debug(devices.get(i).toString());
					}

					if (devices.size() == 0) {
						startSearching();
					} else { // 计算最近的景点
						sightBSCalculateNearestSight.setDevices(devices);
						sightBSCalculateNearestSight.setSights(sights);
						sightBSCalculateNearestSight.asyncExecute();
					}
				}
			});
		}
	}

	class SightsGridViewAdapter extends BaseAdapter {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View view = sightItemViews.get(position);

			return view;
		}

		@Override
		public int getCount() {

			return sights.size();
		}

		@Override
		public Object getItem(int arg0) {

			return sights.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {

			return arg0;
		}
	}
}

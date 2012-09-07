package org.jint.bmy.sightnavi.model;

import java.util.ArrayList;

/**
 * @author jintian
 *
 */
public class SightBSCalculateNearestSight extends Service {
	private ArrayList<Device> devices;
	private ArrayList<Sight> sights;
	private Device nearestDevice;
	private Sight nearestSight;

	@Override
	public Object onExecute() throws Exception {

		if (devices == null || sights == null) {
			return null;
		}

		// calculateNearestDevice();
		calculateNearestSight();

		return nearestSight;
	}

	// protected void calculateNearestDevice(){
	//
	// for(int i = 0; i < devices.size(); i++){
	// Device device = devices.get(i);
	// if(nearestDevice == null){
	// nearestDevice = device;
	// } else {
	// if(device.getRssi() > nearestDevice.getRssi()){
	// nearestDevice = device;
	// }
	// }
	// }
	// }

	/**
	 * 按照顺序（信号从强到弱）取出蓝牙ID，和景点ID进行比对，找到一致的那个
	 * 
	 */
	protected void calculateNearestSight() {
		nearestSight = null;
		for (int j = 0; j < devices.size(); j++) {
			Device device = devices.get(j);

			for (int i = 0; i < sights.size(); i++) {
				Sight sight = sights.get(i);
				String sightId = sight.getId().toUpperCase();
				String deviceAddress = device.getMac();

				if (sightId.equals(deviceAddress)) {
					nearestSight = sight;
				}
				if ( null != nearestSight ) break;
			}
			if ( null != nearestSight ) break;
		}
		
	}

	public ArrayList<Device> getDevices() {
		return devices;
	}

	public void setDevices(ArrayList<Device> devices) {
		this.devices = devices;
	}

	public ArrayList<Sight> getSights() {
		return sights;
	}

	public void setSights(ArrayList<Sight> sights) {
		this.sights = sights;
	}

}

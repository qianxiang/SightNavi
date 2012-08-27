package com.sourcethought.sightnavi.model;

import android.bluetooth.BluetoothDevice;

public class Device {
	private short rssi;
	private String mac;
	private BluetoothDevice bluetoothDevice;
	
	public String toString() {
		return "" + bluetoothDevice.getName() + ", " + bluetoothDevice.getAddress() + " ," + rssi;
	}

	public short getRssi() {
		return rssi;
	}

	public void setRssi(short rssi) {
		this.rssi = rssi;
	}

	public BluetoothDevice getBluetoothDevice() {
		return bluetoothDevice;
	}

	public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
		this.bluetoothDevice = bluetoothDevice;
		this.mac = bluetoothDevice.getAddress().toUpperCase();
	}

	public String getMac() {
		return mac;
	}
	
	
}

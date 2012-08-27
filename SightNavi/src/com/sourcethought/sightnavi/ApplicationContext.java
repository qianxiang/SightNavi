package com.sourcethought.sightnavi;

import java.io.File;
import java.util.ArrayList;

import android.os.Environment;

import com.sourcethought.sightnavi.model.Sight;

public class ApplicationContext {
	public static final int PLAY_TYPE_AUTO = 1;
	public static final int PLAY_TYPE_MANUAL = 2;
	
	private static ApplicationContext instance = new ApplicationContext();

	private int playType;
	private ArrayList<Sight> sights;

	private ApplicationContext() {
		playType = PLAY_TYPE_AUTO;
	}

	public static ApplicationContext getInstance() {
		return instance;
	}
	
	public String getApplicationStoragePath(){
		return Environment.getExternalStorageDirectory().getPath() + File.separator + "SightNavi";
	}

	public int getPlayType() {
		return playType;
	}

	public void setPlayType(int playType) {
		this.playType = playType;
	}

	public ArrayList<Sight> getSights() {
		return sights;
	}

	public void setSights(ArrayList<Sight> sights) {
		this.sights = sights;
	}

}

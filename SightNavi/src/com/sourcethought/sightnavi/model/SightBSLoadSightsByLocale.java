package com.sourcethought.sightnavi.model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.content.res.AssetManager;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class SightBSLoadSightsByLocale extends Service{
	private Locale locale;
	private Context context;
	
	@Override
	public Object onExecute() throws Exception {
		String sightDataPath = "sight-data-" + locale.toString() + ".xml";
		AssetManager assetManager = context.getAssets();
		
		InputStream inStream = assetManager.open(sightDataPath);
		
		XStream xstream = new XStream(new DomDriver());
		xstream.alias("sights", ArrayList.class);
		xstream.alias("sight", Sight.class);
		Object result = xstream.fromXML(inStream);
		
		return result;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
	
	
	
}

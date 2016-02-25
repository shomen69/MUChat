package com.shomen.MUChat.VolleyController;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.shomen.MUChat.Utils.TextField;
import com.shomen.MUChat.R;
import com.squareup.otto.Bus;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class AppController extends Application {

	public static final String TAG = "ASL_"+AppController.class
			.getSimpleName();

	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;

	private static AppController mInstance;
	public static Bus bus;

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		bus = new Bus();
		Iconify.with(new FontAwesomeModule());
		CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
				.setDefaultFontPath("fonts/RobotoCondensed-Regular.ttf")
				.setFontAttrId(R.attr.fontPath)
				.addCustomStyle(TextField.class, R.attr.textFieldStyle)
				.build());
	}

/*	public static synchronized AppController getInstance() {
		return mInstance;
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return mRequestQueue;
	}

	public ImageLoader getImageLoader() {
		getRequestQueue();
		if (mImageLoader == null) {
			mImageLoader = new ImageLoader(this.mRequestQueue,
					new LruBitmapCache());
		}
		return this.mImageLoader;
	}

	public <T> void addToRequestQueue(Request<T> req, String tag) {
		// set the default tag if tag is empty
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}*/
}

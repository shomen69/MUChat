package com.shomen.MUChat.Singletons;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * Created by server on 1/27/2016.
 */
public final class BusProvider {

    private static final Bus BUS = new Bus();

    private static final Handler mainThread = new Handler(Looper.getMainLooper());

    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }

    public static void postOnMain(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            getInstance().post(event);
        } else {
            mainThread.post(new Runnable() {
                @Override
                public void run() {
                    getInstance().post(event);

                }
            });
        }
    }

}

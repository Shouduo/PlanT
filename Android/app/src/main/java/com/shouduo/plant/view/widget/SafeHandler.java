package com.shouduo.plant.view.widget;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by 刘亨俊 on 17.1.29.
 */

public class SafeHandler<T extends SafeHandler.HandlerContainer> extends Handler {
    protected WeakReference<T> mRef;

    public SafeHandler(WeakReference<T> ref) {
        mRef = ref;
    }

    public SafeHandler(T obj) {
        mRef = new WeakReference<>(obj);
    }

    public T getContainer() {
        return mRef.get();
    }

    @Override
    public void handleMessage(android.os.Message msg) {
        super.handleMessage(msg);
        HandlerContainer container = getContainer();
        if (container != null) {
            container.handleMessage(msg);
        }
    }

    /** <br> interface. */
    public interface HandlerContainer {
        void handleMessage(Message message);
    }
}
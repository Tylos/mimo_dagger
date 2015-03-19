package com.example.tylos.daggermimo.base.view;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

/**
 * Created by tylos on 16/3/15.
 */
public abstract class BaseActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mapUI();
        hookUI();
    }

    /**
     * Helper method to avoid unnecessary boilerplate with constant castings.
     * <p/>
     * Intended to be used with Views directly attached to the
     * activity via setContentView()
     *
     * @param id  Resource of the view to extract
     * @param <T> Type view to extract
     * @return Extracted view, or NULL if there is no such view
     */
    public <T extends View> T findView(int id) {
        return (T) findViewById(id);
    }

    /**
     * Gets the specific layout id for the activity.
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * Maps the views within the activity.
     */
    protected abstract void mapUI();

    /**
     * Hooks the action listener to the views within the activity.
     */
    protected abstract void hookUI();
}

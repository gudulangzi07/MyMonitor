package com.mymonitor.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;

public class CustomDialog extends Dialog {

    private static int default_height = LayoutParams.WRAP_CONTENT;

    public CustomDialog(Context context, int layout, int style) {
        this(context, LayoutParams.MATCH_PARENT, default_height, layout, style, false);
    }

    public CustomDialog(Context context, int layout, int width, int style) {
        this(context, width, default_height, layout, style, false);
    }

    public CustomDialog(Context context, int width, int height, int layout, int style, boolean isFullScreen) {
        super(context, style);
        setContentView(layout);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        float density = getDensity(context);
        // 代码修改，FILL_PARENT也会留出一个边
        if (width < 0) {
            int[] widthAndHeight = getSrceenPixels(context);
            if (isFullScreen) {
                params.width = widthAndHeight[0];
            } else {
                params.width = (int) (widthAndHeight[0] - 20 * density);
            }
        } else {
            params.width = (int) (width * density);
        }

        if (height < 0) {
            params.height = default_height;
        } else {
            params.height = (int) (height * density);
        }
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    private float getDensity(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.density;
    }

    // 获取手机的屏幕宽与高
    private int[] getSrceenPixels(Context context) {
        DisplayMetrics displaysMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) (context.getSystemService(Context.WINDOW_SERVICE));
        windowManager.getDefaultDisplay().getMetrics(displaysMetrics);
        int[] widthAndHeight = new int[2];
        widthAndHeight[0] = displaysMetrics.widthPixels;
        widthAndHeight[1] = displaysMetrics.heightPixels;
        return widthAndHeight;
    }

}

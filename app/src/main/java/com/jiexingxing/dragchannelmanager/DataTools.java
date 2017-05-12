package com.jiexingxing.dragchannelmanager;

import android.content.Context;

/**
 * Created by jiexingxing on 2017/4/20.
 */

public class DataTools {

	public static int dip2px(Context context, int dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}
}

package com.clw.video;

import android.content.Context;
import android.util.Log;

import com.autochips.settings.AtcSettings.VCP;
import com.autochips.settings.AtcSettings.VCP.ContrBritSatrOld;
import com.autochips.settings.AtcSettings.VCP.YUVGainOld;
import com.autochips.metazone.AtcMetazone;

public class ScreenUtils {
	
	private static final String TAG = ScreenUtils.class.getSimpleName();
	public static final int TYPE_BRIGHTNESS = 0;
    public static final int TYPE_HUE = 1;
    public static final int TYPE_CONTRAST = 2;
    public static final int TYPE_SATURATION = 3;
    public static final int TYPE_YGAIN = 4;
    public static final int TYPE_UGAIN = 5;
    public static final int TYPE_VGAIN = 6;
    public static final int METAZONE_DEFAULT_SCREEN_INDEX = 0x10037;
    
	private static ScreenUtils mInstance;
	private ContrBritSatrOld mContrBritSatrOld = new ContrBritSatrOld();
	private YUVGainOld mYUVGainOld = new YUVGainOld();
	public int[] mDefalutValues;
	
	public ScreenUtils() {
		super();
	}

	public static ScreenUtils getInstance() {
		if (mInstance == null) {
			mInstance = new ScreenUtils();
		}
		return mInstance;
	}
	
	public void init() {
		mDefalutValues = initDefaultValues();
	}
	
	public void setDefaultScreen() {
		mContrBritSatrOld.i4Brit = mDefalutValues[TYPE_BRIGHTNESS];
		VCP.SetVcpHUELevelReg(mDefalutValues[TYPE_HUE]);
        mContrBritSatrOld.i4Contr= mDefalutValues[TYPE_CONTRAST];
        mContrBritSatrOld.i4Satr = mDefalutValues[TYPE_SATURATION];
        int result = VCP.SetVcpCBSLevelReg(mContrBritSatrOld);
        Log.d(TAG, "setBriContrSatValue iBrit " + mContrBritSatrOld.i4Brit+ " i4Contr " + mContrBritSatrOld.i4Contr +
        		" satr " + mContrBritSatrOld.i4Satr + " result : " + result);
	}
	
	public void setDefaultYUV() {
		mYUVGainOld.i4YGain = mDefalutValues[TYPE_YGAIN];
		mYUVGainOld.i4UGain = mDefalutValues[TYPE_UGAIN];
		mYUVGainOld.i4VGain = mDefalutValues[TYPE_VGAIN];
		int resultYUV = VCP.SetVcpYUVLevelReg(mYUVGainOld);
    	Log.d(TAG, "YUV result = "+resultYUV);
	}
	
	private int[] initDefaultValues() {
        byte[] buf = new byte[7];
        AtcMetazone.readbinary(METAZONE_DEFAULT_SCREEN_INDEX, buf, buf.length);
        Log.d(TAG, "read vaule ="+buf[0]+"--"+buf[1]+"--"+buf[2]+"--"+buf[3]+"--"+buf[4]+"--"+buf[5]+"--"+buf[6]);
        int value[] = new int[7];
        value[TYPE_BRIGHTNESS] = buf[TYPE_BRIGHTNESS] & 0xFF;
        value[TYPE_HUE] = buf[TYPE_HUE] & 0xFF;
        value[TYPE_CONTRAST] = buf[TYPE_CONTRAST] & 0xFF;
        value[TYPE_SATURATION] = buf[TYPE_SATURATION] & 0xFF;
        value[TYPE_YGAIN] = buf[TYPE_YGAIN] & 0xFF;
        value[TYPE_UGAIN] = buf[TYPE_UGAIN] & 0xFF;
        value[TYPE_VGAIN] = buf[TYPE_VGAIN] & 0xFF;
        if (value[TYPE_BRIGHTNESS] == 0
                && value[TYPE_HUE] == 0
                && value[TYPE_CONTRAST] == 0
                && value[TYPE_SATURATION] == 0
                && value[TYPE_YGAIN] == 0
                && value[TYPE_UGAIN] == 0
                && value[TYPE_VGAIN] == 0)  {
            return null;
        } else {
            return value;
        }
    }
}

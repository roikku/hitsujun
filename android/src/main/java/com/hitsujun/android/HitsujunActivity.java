/**
 * Copyright 2014 Loic Merckel
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.hitsujun.android;

import playn.android.GameActivity;
import playn.core.PlayN;

import android.util.DisplayMetrics;
import com.hitsujun.core.Hitsujun;

public class HitsujunActivity extends GameActivity {

	private Hitsujun hitsujun = null ;
	private float scaleFactor = -1f ;
	
	
	@Override
	public void main() {
		hitsujun = new Hitsujun() ;
		PlayN.run(hitsujun);
	}

	
	@Override
	public void onBackPressed() {
		if (hitsujun == null || hitsujun.isMenuScreenActivated())
			super.onBackPressed();
		else
		{
			if (hitsujun.isPopupScreen())
				hitsujun.hidePopupScreen() ;
			else
				hitsujun.activateMenuScreen() ;
		}
	}


	@Override
	protected float scaleFactor() {
		//return super.scaleFactor();
		if (scaleFactor <= 0f) {
		    DisplayMetrics dm = ((getResources() != null) ? (getResources().getDisplayMetrics()) : (null));
		    if (dm != null)
		    	scaleFactor = (dm.densityDpi >= DisplayMetrics.DENSITY_MEDIUM) ? 2f : 1f ;	
		}
	    return Math.max(1f, scaleFactor) ;
	}
}

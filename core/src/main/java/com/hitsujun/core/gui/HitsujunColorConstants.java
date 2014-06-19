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

package com.hitsujun.core.gui;

import playn.core.Color;

public class HitsujunColorConstants {
	
	private HitsujunColorConstants () {super () ; throw new UnsupportedOperationException () ; } ;

	// Kanji
	public static final int KANJI_DRAWING_STROKE_COLOR = Color.rgb(250, 0,  0) ; //0xffff0000 ; 
	public static final int KANJI_BACKGROUND_STROKE_COLOR = Color.rgb(180, 180, 180) ; 
	public static final int KANJI_USER_FEEDBACK_STROKE_COLOR = Color.rgb(128, 0, 128) ; 
	public static final int KANJI_USER_DRAWN_STROKE_COLOR = 0xff00ff00 ;
	
	// Menu
	public static final int MENU_BACKGROUND_COLOR = Color.rgb(220, 220, 220) ; //0xFF99CCFF ;
	public static final int MENU_BACKGROUND_EDGE_COLOR = Color.rgb(150, 150, 150) ;
	
	// Result
	public static final int RESULT_BACKGROUND_COLOR = Color.rgb(220, 220, 220) ;
	
	// Text
	public static final int FONT_TITLE_COLOR = Color.rgb(250, 10, 10) ;
	public static final int FONT_MESSAGE_COLOR = Color.rgb(250, 10, 10) ;
	public static final int FONT_LINK_COLOR = Color.rgb(230, 0, 0) ;
}

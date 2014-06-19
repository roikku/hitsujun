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

package com.hitsujun.core;

import tripleplay.game.ScreenStack.Transition;

import com.hitsujun.core.gui.HitsujunPopupScreen;
import com.hitsujun.core.gui.HitsujunScreen;

public interface HitsujunScreenActivator {
	public void activateScreen(HitsujunScreen screen, Transition transition) ;
	public void activateGameScreen(int level) ;
	public void activateMenuScreen() ;
	public void activateResultScreen(boolean hasWon, int level, int score, float percentageAchieved);
	public void showPopupScreen(HitsujunPopupScreen popupScreen);
	public void hidePopupScreen();
}

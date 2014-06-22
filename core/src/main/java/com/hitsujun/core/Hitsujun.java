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

import static playn.core.PlayN.graphics;

import com.hitsujun.core.controller.HitsujunGameController;
import com.hitsujun.core.factory.KanjiFactoryImpl;
import com.hitsujun.core.gui.HitsujunGameScreen;
import com.hitsujun.core.gui.HitsujunMenuScreen;
import com.hitsujun.core.gui.HitsujunPopupScreen;
import com.hitsujun.core.gui.HitsujunResultScreen;
import com.hitsujun.core.gui.HitsujunScreen;
import com.hitsujun.core.model.HitsujunGame;

import tripleplay.game.Screen;
import tripleplay.game.ScreenStack;
import tripleplay.game.ScreenStack.Transition;
import tripleplay.util.Logger;
import playn.core.Game;
import playn.core.Platform;
import playn.core.PlayN;
import playn.core.util.Clock;

public class Hitsujun extends Game.Default implements HitsujunScreenActivator {

	public static final int UPDATE_RATE = 100; // call update every UPDATE_RATE ms (UPDATE_RATE times per second)
	private static final Logger log = new Logger("hitsujun");
	
	public Hitsujun() 
	{
		super(UPDATE_RATE); 
	}
	
	private HitsujunMenuScreen menuScreen  ;
	private HitsujunResultScreen resultScreen ;
	
	private HitsujunGameScreen gameScreen  ;
	private HitsujunGameController gameController ;
	private HitsujunGame game ;

    public static final ScreenStack screenStack = new ScreenStack() {
        protected void handleError (RuntimeException error) {
            log.warning("Error", error);
        }
    };
 
    
	@Override
	public void activateScreen(final HitsujunScreen screen, final Transition transition) {
		
		Screen topScreen = screenStack.top() ;
		if (screen.equals(topScreen))
			return ;
		if (topScreen != null && ((HitsujunScreen)topScreen).isPopup())
		{
			screenStack.remove(topScreen, new Transition (){

				@Override
				public void init(Screen oscreen, Screen nscreen) {
				}

				@Override
				public boolean update(Screen oscreen, Screen nscreen, float elapsed) {
					return false;
				}

				@Override
				public void complete(Screen oscreen, Screen nscreen) {
					Platform p = PlayN.platform();
					if (p == null)
						return ;
					p.invokeLater(new Runnable() {
						@Override
						public void run() {
							activateScreen(screen, transition);
						}
					});
				}
			});
		}
		screenStack.replace(screen, transition) ;
	}
	
	
	/*
	@Override
	public void activatePreviousScreen() {
		if (screenStack.isTransiting())
			return ;
		Screen topScreen = screenStack.top() ;
		if (topScreen == null)
			return ;
		screenStack.remove(topScreen, screenStack.slide()) ;
	}*/
	
	
	@Override
	public void activateGameScreen(final int level) 
	{
		activateScreen (gameScreen, new Transition () {

			private final Transition transition = screenStack.slide() ;
			
			@Override
			public void init(Screen oscreen, Screen nscreen) {
				transition.init(oscreen, nscreen) ;
			}

			@Override
			public boolean update(Screen oscreen, Screen nscreen, float elapsed) {
				return transition.update(oscreen, nscreen, elapsed) ;
			}

			@Override
			public void complete(Screen oscreen, Screen nscreen) {
				transition.complete(oscreen, nscreen) ;
				gameController.init();
				gameController.start (level) ;
			}}) ;
	}
	
	
	// useful to manage the back button on android
	public boolean isMenuScreenActivated ()
	{
		return !screenStack.isTransiting() && menuScreen.equals(screenStack.top()) ;
	}
	
	
	@Override
	public void activateMenuScreen() {
		activateScreen (menuScreen, screenStack.slide()) ;
	}
	
	
	@Override
	public void activateResultScreen(boolean hasWon, int level, int score, float percentageAchieved) {
		activateScreen (resultScreen.setLevel(level).setHasWon(hasWon).setScore (score).setPercentageAchieved (percentageAchieved), screenStack.pageTurn()) ;
	}
	
	
	@Override
	public void showPopupScreen(HitsujunPopupScreen popupScreen) {
		screenStack.push(popupScreen, screenStack.slide());
	}


	@Override
	public void hidePopupScreen() {
		Screen screen = screenStack.top() ;
		if (screen == null || !(screen instanceof HitsujunScreen) || !((HitsujunScreen)screen).isPopup())
			throw new IllegalStateException () ;
		screenStack.remove(screen, screenStack.slide()) ;
	}
		

	@Override
	public boolean isPopupScreen() {
		Screen screen = screenStack.top() ;
		return (screen != null && screen instanceof HitsujunScreen && ((HitsujunScreen)screen).isPopup()) ;
	}
	
	
	@Override
	public void init() {
		
		final int size = Math.min(graphics().width(), graphics().height());
		float xpos = graphics().width() / 2f; 
		float ypos = graphics().height() / 2f; 
		
		menuScreen = new HitsujunMenuScreen(this) ;
		
		resultScreen = new HitsujunResultScreen (this) ;
		
		game = new HitsujunGame (new KanjiFactoryImpl (xpos, ypos, size, size)) ;
		gameScreen = new HitsujunGameScreen(game) ;
		gameController = new HitsujunGameController (game, gameScreen, this) ;
		gameScreen.setActionListener(gameController) ;
		
		activateScreen (menuScreen, null) ;
	}
	

	@Override
	public void update(int delta) {
		clock.update(delta);
		if (screenStack != null)
			screenStack.update(delta);
	}
	

	@Override
	public void paint(float alpha) {
		// the background automatically paints itself, so no need to do anything here!
		clock.paint(alpha);
		paint (clock) ;
	}
	
	
	public void paint(Clock clock) {
		if (screenStack != null)
			screenStack.paint(clock);
	}
	
	protected final Clock.Source clock = new Clock.Source(Hitsujun.UPDATE_RATE);
}

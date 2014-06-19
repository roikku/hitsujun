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

package com.hitsujun.core.controller;

import java.util.EnumSet;
import java.util.Set;

import playn.core.Key;
import playn.core.Keyboard;
import playn.core.Keyboard.TypedEvent;
import playn.core.PlayN;
import playn.core.Pointer;
import playn.core.Pointer.Event;
import playn.core.Touch;
import playn.core.util.Callback;

import com.hitsujun.core.HitsujunScreenActivator;
import com.hitsujun.core.gui.DrawingStatus;
import com.hitsujun.core.gui.HitsujunGameScreen;
import com.hitsujun.core.gui.HitsujunGameScreen.MenuCallback;
import com.hitsujun.core.gui.HitsujunKanjiInfoPopupScreen;
import com.hitsujun.core.gui.KanjiView;
import com.hitsujun.core.model.HitsujunGame;
import com.hitsujun.core.model.Kanji;
import com.hitsujun.core.model.Kanji.KanjiDrawingStatus;
import com.hitsujun.core.model.KanjiInfo;
import com.hitsujun.core.util.Triplet;

public class HitsujunGameController  extends HitsujunController implements HasActionListener {

	private HitsujunGame game ;
	private HitsujunGameScreen view ;
	private KanjiController kanjiController = null ;
	
	private final Set<Key> backKeys = EnumSet.of(Key.ESCAPE, Key.BACK);
	private final HitsujunScreenActivator hitsujunScreenActivator ;
	
	
	public HitsujunGameController (HitsujunGame game, HitsujunGameScreen view, HitsujunScreenActivator hitsujunScreenActivator)
	{
		super () ;
		if (game == null || view == null)
			throw new NullPointerException () ;
		this.game = game ;
		this.view = view ;
		this.hitsujunScreenActivator = hitsujunScreenActivator ;
	}
	

	@Override
	public void initPointerListener() {
		// pointer events handling
		PlayN.pointer().setListener(new Pointer.Adapter() {
			@Override
			public void onPointerEnd(Event event) {
				if (kanjiController != null && kanjiController.getPointerListener () != null)
					kanjiController.getPointerListener ().onPointerEnd(event);
			}

			@Override
			public void onPointerCancel(Event event) {
				if (kanjiController != null && kanjiController.getPointerListener () != null)
					kanjiController.getPointerListener ().onPointerCancel(event);
			}

			@Override
			public void onPointerDrag(Event event) {
				if (kanjiController != null && kanjiController.getPointerListener () != null)
					kanjiController.getPointerListener ().onPointerDrag(event);
			}

			@Override
			public void onPointerStart(Event event) {
				if (kanjiController != null && kanjiController.getPointerListener () != null)
					kanjiController.getPointerListener ().onPointerStart(event);
			}
		});
	}


	@Override
	public void initKeyboardListener() {
		// keyboard events handling
		PlayN.keyboard().setListener(new Keyboard.Adapter() {
			@Override
			public void onKeyDown(Keyboard.Event event) {
				if (backKeys.contains(event.key())) 
				{
					goToMainMenu () ;
				} 
				else 
				{
					if (kanjiController != null && kanjiController.getKeyboardListener () != null)
						kanjiController.getKeyboardListener ().onKeyDown(event);
				}
			}

			@Override
			public void onKeyTyped(TypedEvent event) {
				if (kanjiController != null && kanjiController.getKeyboardListener () != null)
					kanjiController.getKeyboardListener ().onKeyTyped(event);
			}

			@Override
			public void onKeyUp(Keyboard.Event event) {
				if (kanjiController != null && kanjiController.getKeyboardListener () != null)
					kanjiController.getKeyboardListener ().onKeyUp(event);
			}
		});
	}


	@Override
	public void initTouchListener() {
		// touch events handling
		try {
			PlayN.touch().setListener(new Touch.Adapter() {
				public void onTouchStart(Touch.Event[] touches) {
					if (touches.length > 1) {
						goToMainMenu () ;
					}
				}
			});
		} 
		catch (UnsupportedOperationException e) {
			PlayN.log().error("Touch are not supported", e);
		}
	}
	
	
	public void init() {
		
		// create the menu
		view.createMenu(new MenuCallback (){

			@Override
			public void onKanjiInfo() {
				
				game.getKanjiInfo(new Callback<KanjiInfo> () {

					@Override
					public void onSuccess(KanjiInfo result) {
						if (result == null)
							throw new NullPointerException () ;
						hitsujunScreenActivator.showPopupScreen(new HitsujunKanjiInfoPopupScreen (result, hitsujunScreenActivator)) ;
						/*hitsujunScreenActivator.showPopupScreen(new HitsujunKanjiInfoPopupScreen (result, hitsujunScreenActivator, new HasActionListener () {

							@Override
							public void initPointerListener() {
								PlayN.pointer().setListener(null) ;
							}

							@Override
							public void initKeyboardListener() {
								PlayN.keyboard().setListener(null);
							}

							@Override
							public void initTouchListener() {
								PlayN.touch().setListener(null) ;
							}})) ;*/
					}

					@Override
					public void onFailure(Throwable cause) {
						PlayN.log().error("Error occurred while getting Kanji info", cause) ;
					}});
			}});
	}
	
	
	private void goToMainMenu ()
	{
		if (game.isPlaying())
			game.giveUp();
		view.hideKanji();
		hitsujunScreenActivator.activateMenuScreen();
	}
	

	public void start(int level) {
		game.start(level) ;
		view.showMessage (null) ;
		next () ;
	}
	
	
	private void next ()
	{
		game.nextTurn(new Callback<Triplet<Kanji, KanjiView, KanjiController> > () {

			@Override
			public void onSuccess(Triplet<Kanji, KanjiView, KanjiController> result) {
				
				if (result == null)
				{
					view.showMessage ("You win!") ;
					view.hideKanji() ;	
					hitsujunScreenActivator.activateResultScreen(game.hasWon(), game.getLevel(), game.getScore(), game.getPercentageAchieved()) ;
					return ;
				}
				
				kanjiController = result.getThird() ;
				view.setKanjiView(result.getSecond());
				view.drawKanjiBackground ();
				
				kanjiController.setUserDrawingStatusCallback(new Callback<KanjiDrawingStatus> () {

					@Override
					public void onSuccess(KanjiDrawingStatus result) {
						switch (result)
						{
						case FAILURE_WRONG_ORDER:
							{
								game.hasLost() ;
								view.showMessage ("Wrong order!") ;
								showExpectedStrokeThenSolutionAndGoToResultScreen () ;
							}
							break ;
						case FAILURE_WRONG_DIRECTION:
							{
								game.hasLost() ;
								view.showMessage ("Wrong direction!") ;
								showExpectedStrokeThenSolutionAndGoToResultScreen () ;
							}
							break ;
						case COMPLETED:
							{
								next () ;
							}
							break ;
						case UNDETERMINED :
						default:
							throw new UnsupportedOperationException () ;
						}
					}

					@Override
					public void onFailure(Throwable cause) {
						throw new UnsupportedOperationException () ;
					}}) ;
			}

			@Override
			public void onFailure(Throwable cause) {
				PlayN.log().error("Error occurred while calling nextTurn", cause) ;
			}}) ;
	}
	
	
	private void showExpectedStrokeThenSolutionAndGoToResultScreen ()
	{
		//view.enableMenu(false) ;
		view.showMenu(false) ;
		view.drawExpectedStroke(new DrawingStatus () {

			@Override
			public void init() {
				PlayN.pointer().setListener(null);
				//PlayN.keyboard().setListener(null);
			}

			@Override
			public void drawing(float per) {
			}

			@Override
			public void stopped() {
			}

			@Override
			public void complete() {
				showSolutionAndGoToResultScreen () ;
			}}) ;
	}
	
	
	private void showSolutionAndGoToResultScreen ()
	{
		view.drawKanji(new DrawingStatus() {
			
			@Override
			public void init() {
				
				view.showMessage ("Solution") ;
				
				PlayN.pointer().setListener(null);
				//PlayN.keyboard().setListener(null);
			}
			
			@Override
			public void drawing(float per) {
			}
			
			@Override
			public void complete() {
				view.hideKanji() ;	
				hitsujunScreenActivator.activateResultScreen(game.hasWon(), game.getLevel(), game.getScore(), game.getPercentageAchieved()) ;
			}

			@Override
			public void stopped() {
			}
		}) ;		
	}
}

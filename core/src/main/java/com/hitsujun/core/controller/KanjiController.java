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

import java.util.List;

import com.hitsujun.core.util.MathUtils;
import com.hitsujun.core.util.Pair;

import java.util.ArrayList;

import playn.core.Keyboard;
import playn.core.Keyboard.Listener;
import playn.core.Keyboard.TypedEvent;
import playn.core.PlayN;
import playn.core.Pointer;
import playn.core.Pointer.Event;
import playn.core.util.Callback;

import com.hitsujun.core.gui.HitsujunColorConstants;
import com.hitsujun.core.gui.KanjiView;
import com.hitsujun.core.model.Kanji;

public class KanjiController extends HitsujunController {
	
	private final Kanji kanji ;
	private final KanjiView view ;
	
	private final List<Pair<Float, Float> > selectedPoints = new ArrayList<Pair<Float, Float> > () ;
	private Callback <Kanji.KanjiDrawingStatus> userDrawingStatusCallback = null ;
	
	public KanjiController (Kanji kanji, KanjiView view)
	{
		super () ;
		if (kanji == null || view == null)
			throw new NullPointerException () ;
		this.kanji = kanji ;
		this.view = view ;
		init () ;
	}
	
	final public void init() 
	{

	}
	
	
	@Override
	public Listener getKeyboardListener() {
		return keyboardListener;
	}

	@Override
	public Pointer.Listener getPointerListener() {
		return pointerListener ;
	}

		
	private final Pointer.Listener pointerListener = new Pointer.Adapter() {
		@Override
		public void onPointerEnd(Event event) {

			if (event != null)
				addPointIfValid(event.localX(), event.localY()) ;

			view.clearUserInput () ;
			
			Pair<Boolean, Kanji.FailureReason> ret = kanji.drawNextStrokeIfValid(selectedPoints) ;
			if (ret.getFirst())
			{
				if (view != null) {
					view.showCurrentStatus(HitsujunColorConstants.KANJI_USER_DRAWN_STROKE_COLOR);
				}
				if (kanji.isKanjiFullyDrawn ()) {
					if (userDrawingStatusCallback != null)
						userDrawingStatusCallback.onSuccess(Kanji.KanjiDrawingStatus.COMPLETED) ;
				}
			}
			else
			{
				PlayN.log().debug("Failure reason: " + ret.getSecond().toString()) ;
				
				// lose
				Kanji.KanjiDrawingStatus status = Kanji.KanjiDrawingStatus.UNDETERMINED ;
				if (ret.getSecond() == Kanji.FailureReason.WRONG_ORDER)
					status = Kanji.KanjiDrawingStatus.FAILURE_WRONG_ORDER;
				else if (ret.getSecond() == Kanji.FailureReason.WRONG_DIRECTION)
					status = Kanji.KanjiDrawingStatus.FAILURE_WRONG_DIRECTION;
				if (status != Kanji.KanjiDrawingStatus.UNDETERMINED 
						&& userDrawingStatusCallback != null) {
					userDrawingStatusCallback.onSuccess(status) ;
				}
			}
		}

		@Override
		public void onPointerCancel(Event event) {
			super.onPointerCancel(event);
			
			view.clearUserInput () ;
		}

		@Override
		public void onPointerDrag(Event event) {
			super.onPointerDrag(event);

			if (event != null)
				addPointIfValid(event.localX(), event.localY()) ;
		}

		@Override
		public void onPointerStart(Event event) {
			super.onPointerStart(event);
			
			selectedPoints.clear();
			if (event != null)
				addPointIfValid(event.localX(), event.localY()) ;
		}
	};
	
	
	private void addPointIfValid (float x, float y)
	{
		if (selectedPoints.isEmpty())
			selectedPoints.add(Pair.newPair(x, y));
		else
		{
			Pair<Float, Float> prevPoint = selectedPoints.get(selectedPoints.size() - 1) ;
			Pair<Float, Float> currPoint = Pair.newPair(x, y) ;
			float th = kanji.getThreshold() ;
			if (MathUtils.distanceSquared(prevPoint, currPoint) > th * th)
				selectedPoints.add(currPoint);
			drawFeedbacks () ;
		}
	}
	
	
	public void setUserDrawingStatusCallback (Callback <Kanji.KanjiDrawingStatus> userDrawingStatusCallback)
	{
		this.userDrawingStatusCallback = userDrawingStatusCallback ;
	}
	
	
	private void drawFeedbacks ()
	{
		List<Pair<Float, Float> > convertedPoints = new ArrayList<Pair<Float, Float> > () ;
		for (Pair<Float, Float> pt : selectedPoints)
			convertedPoints.add (Pair.newPair(kanji.convertAxisX (pt.getFirst()), kanji.convertAxisY (pt.getSecond()))) ;
		view.showUserInput (convertedPoints, HitsujunColorConstants.KANJI_USER_FEEDBACK_STROKE_COLOR) ;
	}
	
	
	private final Keyboard.Listener keyboardListener = new Keyboard.Adapter() {
		@Override
		public void onKeyDown(Keyboard.Event event) {
			kanji.resetDrawing();
		}

		@Override
		public void onKeyTyped(TypedEvent event) {
		}

		@Override
		public void onKeyUp(playn.core.Keyboard.Event event) {
		}
	};
}

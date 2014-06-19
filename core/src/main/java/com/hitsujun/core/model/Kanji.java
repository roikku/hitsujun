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

package com.hitsujun.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import playn.core.util.Callback;

import com.hitsujun.core.gui.HitsujunColorConstants;
import com.hitsujun.core.util.Pair;
import com.hitsujun.core.util.Transform;
import com.hitsujun.core.util.kanji.KanjiStrokesLoader;

public class Kanji {
	
	public static enum FailureReason
	{
		NONE,
		WRONG_ORDER,
		WRONG_DIRECTION,
		NO_STROKE_SELECTION,
		MULTI_STROKE_SELECTION,
		UNKNOWN,
	}
	
	
	public static enum KanjiDrawingStatus
	{
		UNDETERMINED,
		FAILURE_WRONG_ORDER,
		FAILURE_WRONG_DIRECTION,
		COMPLETED,
	}
	
	
	private final String kanji ;
	
	private final List<Stroke> strokes = new ArrayList<Stroke> () ;
	
	private final float scale ;
	private final float strokeWidth ;
	
	private final int strokeColor ; 
	private final int bgStrokeColor ; 
	
	private final float threshold ; // should be approximately equal to strokeWidth
	
	private final float kanjiOriX ;
	private final float kanjiOriY ;
	
	private final float x ;
	private final float y ;
	private final float width ;
	private final float height ;
	
	private final List<Integer> drawnStrokeIndexes = new ArrayList<Integer> () ;
	
	private volatile boolean initialized = false ;
	
	public static class Builder 
	{
		private final String kanji ;
		
		private float scale = -1f ;
		private float strokeWidth = 12f ;
		
		private int strokeColor = HitsujunColorConstants.KANJI_DRAWING_STROKE_COLOR ;
		private int bgStrokeColor = HitsujunColorConstants.KANJI_BACKGROUND_STROKE_COLOR ;
		
		private float threshold = strokeWidth + 2f ; // should be approximately equal to strokeWidth
		
		private float x = 50 ;
		private float y = 50 ;
		private float width = 100 ;
		private float height = 100 ;
		
		public Builder (String kanji)
		{
			super () ;
			this.kanji = kanji ;
		}

		public Builder setTransf(float scale) {
			this.scale = scale;
			return this ;
		}

		public Builder setStrokeWidth(float strokeWidth) {
			this.strokeWidth = strokeWidth;
			return this ;
		}

		public Builder setStrokeColor(int strokeColor) {
			this.strokeColor = strokeColor;
			return this ;
		}

		public Builder setBgStrokeColor(int bgStrokeColor) {
			this.bgStrokeColor = bgStrokeColor;
			return this ;
		}

		public Builder setThreshold(float threshold) {
			this.threshold = threshold;
			return this ;
		}

		public Builder setX(float x) {
			this.x = x;
			return this ;
		}

		public Builder setY(float y) {
			this.y = y;
			return this ;
		}

		public Builder setWidth(float width) {
			this.width = width;
			return this ;
		}

		public Builder setHeight(float height) {
			this.height = height;
			return this ;
		}
		
		public Kanji build ()
		{
			return new Kanji (this) ;
		}
	}
	
	
	private Kanji (Builder b)
	{
		super () ;
		
		this.kanji = b.kanji ;
		this.bgStrokeColor = b.bgStrokeColor ;
		this.strokeColor = b.strokeColor ;
		this.strokeWidth = b.strokeWidth ;
		this.threshold = b.threshold ;
		this.scale = b.scale ;
		this.x = b.x ;
		this.y = b.y ;
		this.width = b.width ;
		this.height = b.height ;
		
		this.kanjiOriX = x - width / 2f ;
		this.kanjiOriY = y - height / 2f ;
	}
	
	
	final public void init (KanjiStrokesLoader loader, final Callback<Void> callback)
	{
		if (loader == null || callback == null)
			throw new NullPointerException () ;
		loader.load(new Callback<Pair<Pair<Float, Float>, List<String> > > () {

			@Override
			public void onSuccess(Pair<Pair<Float, Float>, List<String> > result) {
				if (result == null)
					throw new NullPointerException () ;
				Pair<Float, Float> widthHeight = result.getFirst() ;
				
				float effScale = scale ;
				if (effScale < 0)
					effScale = (Math.min(width, height) - 100f) / Math.min(widthHeight.getFirst(), widthHeight.getSecond()) ;
				
				float transX = width / 2f - widthHeight.getFirst() * effScale / 2f ;
				float transY = height / 2f - widthHeight.getSecond() * effScale / 2f ; 
				//synchronized (lock)
				{
					for (String path : result.getSecond())
					{
						strokes.add(new Stroke (path, new Transform (effScale, transX), new Transform (effScale, transY))) ;
					}
				}
				initialized = true ;
				callback.onSuccess(null) ;
			}

			@Override
			public void onFailure(Throwable cause) {
				callback.onFailure(cause) ;
			}}) ;
	}
	
	
	/*
	final public void init (final Callback<Void> callback)
	{
		KanjiStrokesLoaderFromSvgImpl loader = new KanjiStrokesLoaderFromSvgImpl (getKanji()) ;
		init (loader, callback) ;
	}
	
	
	final public void init (int level, final Callback<Void> callback)
	{
		KanjiStrokesLoaderFromTxtImpl loader = new KanjiStrokesLoaderFromTxtImpl (level, getKanji()) ;
		init (loader, callback) ;
	}*/
	
	
	public String getKanji ()
	{
		return kanji ;
	}
	
	
	public List<Stroke> getStrokes ()
	{
		if (!initialized)
			return Collections.emptyList() ;
		return strokes ;
	}
	
	
	public int getNumberOfStrokes ()
	{
		if (!initialized)
			return -1 ;
		return strokes.size() ;
	}
	
	
	public int getExpectedStrokeIndex ()
	{
		synchronized (drawnStrokeIndexes) {
			return drawnStrokeIndexes.size() ;
		}
	}
	
	
	public Stroke getExpectedStroke ()
	{
		synchronized (drawnStrokeIndexes) {
			int index = drawnStrokeIndexes.size() ;
			if (index >= strokes.size())
				return null ;
			return strokes.get(drawnStrokeIndexes.size()) ;
		}
	}
	
	
	public void resetDrawing ()
	{
		synchronized (drawnStrokeIndexes) {
			drawnStrokeIndexes.clear();
		}
	}
	
	
	public Pair<Boolean, FailureReason> drawNextStrokeIfValid (List<Pair<Float, Float> > points)
	{
		if (!initialized)
			return Pair.newPair(Boolean.valueOf(false), FailureReason.UNKNOWN) ;
		
		List<Pair<Float, Float> > convertedPoints = new ArrayList<Pair<Float, Float> > () ;
		for (Pair<Float, Float> pt : points)
			convertedPoints.add (Pair.newPair(convertAxisX (pt.getFirst()), convertAxisY (pt.getSecond()))) ;
		
		Set<Integer> sel = getAttemptedStrokes (convertedPoints) ;
		if (!sel.isEmpty())
		{
			if (sel.size() > 1)
				return Pair.newPair(Boolean.valueOf(true), FailureReason.MULTI_STROKE_SELECTION) ;
			int index = sel.iterator().next().intValue() ;
			if (isNextStrokeOrderCorrect(index))
			{
				Stroke s = strokes.get(index) ;
				if (!s.isDrawingDirectionCorrect(convertedPoints))
					return Pair.newPair(Boolean.valueOf(false), FailureReason.WRONG_DIRECTION) ;
				// Valid stroke
				synchronized (drawnStrokeIndexes) {
					drawnStrokeIndexes.add(Integer.valueOf(index)) ;
				}
				return Pair.newPair(Boolean.valueOf(true), FailureReason.NONE) ;
			}
			else
			{
				return Pair.newPair(Boolean.valueOf(false), FailureReason.WRONG_ORDER) ;
			}
		}
		return Pair.newPair(Boolean.valueOf(false), FailureReason.NO_STROKE_SELECTION) ;
	}
	
	
	private boolean isNextStrokeOrderCorrect (int index)
	{
		if (!initialized)
			return false ;
		if (index >= this.getNumberOfStrokes() || index < 0)
			return false ;
		synchronized (drawnStrokeIndexes) {
			if (drawnStrokeIndexes.size() != index)
				return false ;
			return true ;
		}
	}
	
	
	public boolean isKanjiFullyDrawn ()
	{
		synchronized (drawnStrokeIndexes) {
			return (drawnStrokeIndexes.size() == this.getNumberOfStrokes()) ;
		}
	}
	
	
	public List<Stroke> getDrawnStrokes ()
	{
		List<Stroke> ret = new ArrayList<Stroke> () ;
		synchronized (drawnStrokeIndexes) {
			for (Integer i : drawnStrokeIndexes)
			{
				ret.add (strokes.get(i)) ;
			}
		}
		return ret ;
	}

	
	public float getStrokeWidth() {
		return strokeWidth;
	}


	public int getStrokeColor() {
		return strokeColor;
	}


	public int getBgStrokeColor() {
		return bgStrokeColor;
	}


	public float getKanjiOriX() {
		return kanjiOriX;
	}


	public float getKanjiOriY() {
		return kanjiOriY;
	}


	public float getX() {
		return x;
	}


	public float getY() {
		return y;
	}


	public float getWidth() {
		return width;
	}


	public float getHeight() {
		return height;
	}
	
	
	public float getThreshold() {
		return threshold;
	}
	
	
	public float convertAxisX (float x)
	{
		return x - getKanjiOriX() ;
	}
	
	
	public float convertAxisY (float y)
	{
		return y - getKanjiOriY() ;
	}
	
	
	@SuppressWarnings("unused")
	private Set<Integer> getSelectedStrokes (float x, float y)
	{
		Set<Integer> ret = new TreeSet<Integer> () ;
		List<Stroke> strokes = getStrokes() ;
		if (strokes.isEmpty())
			return ret ;
		x = convertAxisX (x) ;
		y = convertAxisY (y) ;
		int index = 0 ;
		for (Stroke s : strokes)
		{
			if (s.neighbors(x, y, getThreshold()))
				ret.add(Integer.valueOf(index)) ;
			++index ;
		}
		return ret ;
	}
	
	
	private Set<Integer> getAttemptedStrokes (List<Pair<Float, Float> > points)
	{
		Set<Integer> ret = new TreeSet<Integer> () ;
		if (strokes.isEmpty())
			return ret ;
		//List<Pair<Float, Float> > convertedPoints = new ArrayList<Pair<Float, Float> > () ;
		//for (Pair<Float, Float> pt : points)
		//	convertedPoints.add (Pair.newPair(convertAxisX (pt.getFirst()), convertAxisY (pt.getSecond()))) ;
		int index = 0 ;
		for (Stroke s : strokes)
		{
			if (s.isSimilar(points, getThreshold()))
				ret.add(Integer.valueOf(index)) ;
			++index ;
		}
		return ret ;
	}
}

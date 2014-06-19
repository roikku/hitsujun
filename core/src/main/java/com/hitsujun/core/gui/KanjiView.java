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

import static playn.core.PlayN.graphics;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.hitsujun.core.model.Kanji;
import com.hitsujun.core.model.Stroke;
import com.hitsujun.core.util.Pair;

import playn.core.Canvas;
import playn.core.CanvasImage;
import playn.core.GroupLayer;
import playn.core.ImageLayer;
import playn.core.Path;

public class KanjiView {
	
			
	private static class KanjiDrawer implements Drawer
	{
		private int currentStroke = 0 ;
		private int currentStrokeProgress = 1 ; // must not be zero (must be one!)
		private int updateCount = 0 ;
		private boolean isDrawn = false ;
		
		private DrawingStatus drawingStatus = null ;
		private final Kanji kanji ; 
		
		public KanjiDrawer (Kanji kanji, DrawingStatus drawingStatus)
		{
			super () ;
			if (kanji == null)
				throw new NullPointerException () ;
			this.drawingStatus = drawingStatus ;
			this.kanji = kanji ;
			
			if (this.drawingStatus != null)
				this.drawingStatus.init () ;
		}
		
		@Override
		public synchronized void update(int delta) {
			List<Stroke> strokes = kanji.getStrokes() ;
			if (strokes.isEmpty())
				return ;
			if (!isDrawn)
			{
				if (currentStrokeProgress == 0 || currentStrokeProgress >= 100)
				{
					++currentStroke ;
					currentStroke = currentStroke % strokes.size() ;
					currentStrokeProgress = 1 ;
					updateCount = 0 ;
				}
				else
				{
					currentStrokeProgress += (3 * updateCount) ;
				}
				++updateCount ;
				isDrawn = (currentStroke == strokes.size() - 1) && (currentStrokeProgress >= 100) ;
				if (isDrawn)
				{
					if (drawingStatus != null)
						drawingStatus.complete() ; 
				}
			}
		}

		@Override
		public synchronized void paint(Canvas canvas, float alpha) {
			List<Stroke> strokes = kanji.getStrokes() ;
			if (strokes.isEmpty())
				return ;
			if (canvas == null)
				return ;
			if (!isDrawn)
			{
				canvas.setStrokeColor(kanji.getStrokeColor());
				float per = currentStrokeProgress / 100f ;
				strokes.get(currentStroke).strokeDiscrete(canvas, per) ;
				if (drawingStatus != null)
					drawingStatus.drawing(per) ; 
			}
		}

		@Override
		public synchronized void stop() {
			if (!isDrawn && drawingStatus != null)
				drawingStatus.stopped() ; 
			isDrawn = true ;
		}
	}
	
	
	private static class KanjiStrokeBlinker implements Drawer
	{
		private final long interval ;
		private final Kanji kanji ;
		private final Stroke stroke ;
		private Date lastDrawn = new Date () ;
		private Date startDrawn = new Date () ;
		private volatile boolean hasBeenStopped = false ;
		private volatile boolean toBeDrawn = true ;
		private DrawingStatus drawingStatus = null ;
		private final long duration ;
		
		public KanjiStrokeBlinker (Kanji kanji, float frequency, long duration, DrawingStatus drawingStatus)
		{
			super () ;
			if (kanji == null)
				throw new NullPointerException () ;
			if (frequency > 1000f || frequency < 1f)
				throw new IllegalArgumentException ("The frequency must remain between 1 and 1000.") ;
			this.kanji = kanji ;
			this.stroke = kanji.getExpectedStroke () ;
			this.drawingStatus = drawingStatus ;
			this.duration = duration ;
			this.interval = (long) (1000f / frequency) ;
			
			if (this.drawingStatus != null)
				this.drawingStatus.init () ;
		}
		
		@Override
		public void update(int delta) {
			if (hasBeenStopped)
				return ;
			Date now = new Date () ;
			if (now.getTime() - lastDrawn.getTime() >=  interval) {
				toBeDrawn = true ;
				lastDrawn = now ;
			}
			else
				toBeDrawn = false ;
			
			long totalElapsed = now.getTime() - startDrawn.getTime() ;
			if (totalElapsed >= duration)
			{
				hasBeenStopped = true ;
				if (this.drawingStatus != null)
					this.drawingStatus.complete() ;
			}
			else
			{
				if (this.drawingStatus != null)
					this.drawingStatus.drawing (totalElapsed / (float)duration) ;
			}
		}

		@Override
		public void paint(Canvas canvas, float alpha) {
			if (hasBeenStopped)
				return ;
			if (stroke != null)	
			{
				int color = (toBeDrawn) ? (kanji.getStrokeColor()) : (kanji.getBgStrokeColor()) ;
				canvas.setStrokeColor(color);
				stroke.stroke(canvas) ;
			}
		}

		@Override
		public void stop() {
			hasBeenStopped = true ;
			if (this.drawingStatus != null)
			{
				this.drawingStatus.stopped() ;
				this.drawingStatus.complete() ;
			}
		}	
	}
	
	private Canvas canvas = null ;
	private Canvas bgCanvas = null ;
	private Canvas inCanvas = null ;
	
	private Drawer drawer = null ;
	private final Object lock = new Object () ;
	
	private final Kanji kanji ; 
	
	public KanjiView (Kanji kanji)
	{
		super () ;
		this.kanji = kanji ;
	}
	
	
	public void setGroupLayer ( GroupLayer kanjiLayer)
	{		
		// the background canvas must be created before the "working" canvas
		bgCanvas = createCanvas (kanjiLayer, kanji.getX(), kanji.getY(), kanji.getWidth(), kanji.getHeight()) ;
		canvas = createCanvas (kanjiLayer, kanji.getX(), kanji.getY(), kanji.getWidth(), kanji.getHeight()) ;
		inCanvas = createCanvas (kanjiLayer, kanji.getX(), kanji.getY(), kanji.getWidth(), kanji.getHeight()) ;
		
		bgCanvas.setStrokeWidth(kanji.getStrokeWidth());
		canvas.setStrokeWidth(kanji.getStrokeWidth());
		inCanvas.setStrokeWidth(kanji.getStrokeWidth());
		
		/*
		Canvas tmpcanvas = createCanvas (kanjiLayer, kanji.getX(), kanji.getY(), kanji.getWidth(), kanji.getHeight()) ;
		tmpcanvas.setStrokeColor(kanji.getStrokeColor()) ;
		tmpcanvas.setStrokeWidth(15f) ;
		tmpcanvas.strokeRect(0, 0, kanji.getWidth(), kanji.getHeight()) ;*/
	}
	
	
	private void checkCanvas ()
	{
		if (bgCanvas == null || canvas == null)
			throw new IllegalStateException ("The GroupLayer must be set") ;
	}
	
	
	private final Canvas createCanvas (final GroupLayer kanjiLayer, final float x, final float y, final float width, final float height)
	{
		CanvasImage image = graphics().createImage(width, height);
		ImageLayer layerCanvas = graphics().createImageLayer(image);
		layerCanvas.setOrigin(image.width() / 2f, image.height() / 2f);
		layerCanvas.setTranslation(x, y);
		kanjiLayer.add(layerCanvas);
		return image.canvas() ;
	}
	
	
	public void showBackgroung (int strokeColor)
	{
		checkCanvas () ;
		List<Stroke> strokes = kanji.getStrokes() ;
		if (strokes.isEmpty())
			return ;
		
		bgCanvas.setStrokeColor(HitsujunColorConstants.MENU_BACKGROUND_EDGE_COLOR);
		bgCanvas.setStrokeWidth(kanji.getStrokeWidth()) ;
		for (Stroke s : strokes)
			s.stroke(bgCanvas);
		
		bgCanvas.setStrokeWidth(kanji.getStrokeWidth() - 1f) ;
		bgCanvas.setStrokeColor(strokeColor);
		for (Stroke s : strokes)
			s.stroke(bgCanvas);
	}
	
	
	public void showBackgroung ()
	{
		showBackgroung (kanji.getBgStrokeColor()) ;
	}
	
	
	public void showUserInput (List<Pair<Float, Float> > points, int strokeColor)
	{
		if (inCanvas == null)
			return ;
		inCanvas.setStrokeColor (strokeColor) ;
		Path path = inCanvas.createPath() ;
		boolean first = true ;
		for (Pair<Float, Float> pt : points)
		{
			if (first) {
				path.moveTo(pt.getFirst(), pt.getSecond()) ;
				first = false ;
			}
			else
				path.lineTo(pt.getFirst(), pt.getSecond()) ;	
		}
		inCanvas.strokePath(path) ;
		// make it a little more beautiful
		//if (points.size() > 2)
		//{
		//	inCanvas.strokeCircle(points.get(0).getFirst(), points.get(0).getSecond(), kanji.getStrokeWidth() / 2f) ;
		//	inCanvas.strokeCircle(points.get(points.size()-1).getFirst(), points.get(points.size()-1).getSecond(), kanji.getStrokeWidth() / 2f) ;
		//}
	}
	
	
	public void clearUserInput() {
		if (inCanvas == null)
			return ;
		inCanvas.clear() ;
	}
	
	
	public void strokeSelection (Set<Integer> selections, int color)
	{
		checkCanvas () ;
		if (selections == null || selections.isEmpty())
			return ;
		List<Stroke> strokes = kanji.getStrokes() ;
		if (strokes.isEmpty())
			return ;
		canvas.setStrokeColor(color);
		for (Integer i : selections)
			strokes.get(i).stroke(canvas);
	}
	
	
	public void draw ()
	{
		draw (null) ;
	}
	
	
	public void setDrawer (Drawer drawer)
	{
		synchronized (lock)
		{
			this.drawer = drawer ;
		}
	}
	
	
	public void drawExpectedStroke (final DrawingStatus drawingStatus)
	{
		checkCanvas () ;
		canvas.clear() ;
		setDrawer (new KanjiStrokeBlinker (kanji, 3, 2500, new DrawingStatusWrapper (drawingStatus) {

			@Override
			public void complete() {
				setDrawer (null) ;
				super.complete() ;
			}})) ;
	}
	
	
	public void draw (final DrawingStatus drawingStatus)
	{
		checkCanvas () ;
		canvas.clear() ;
		setDrawer (new KanjiDrawer (kanji, new DrawingStatusWrapper (drawingStatus) {

			private volatile boolean showWhenComplete = true ;
			
			@Override
			public void stopped() {
				super.stopped() ;
				showWhenComplete = false ;
			}
		
			@Override
			public void complete() {
				if (showWhenComplete)
					show () ;
				setDrawer (null) ;
				super.complete() ;
			}})) ;
		showBackgroung();
	}
	
	
	public void show ()
	{
		checkCanvas () ;
		List<Stroke> strokes = kanji.getStrokes() ;
		if (strokes.isEmpty())
			return ;
		canvas.clear() ;
		canvas.setStrokeColor(kanji.getStrokeColor());
		for (Stroke s : strokes)
			s.stroke(canvas);
	}
	
	
	public void showCurrentStatus (int color)
	{
		checkCanvas () ;
		List<Stroke> strokes = kanji.getStrokes() ;
		if (strokes.isEmpty())
			return ;
		stopDrawing () ;
		canvas.clear() ;
		canvas.setStrokeColor(color);
		for (Stroke s : kanji.getDrawnStrokes())
			s.stroke(canvas);
	}
	
	
	public void update(int delta) {
		synchronized (lock)
		{
			if (drawer != null)
				drawer.update(delta) ;
		}
	}

	
	public void paint(float alpha) {
		synchronized (lock)
		{
			if (drawer != null)
				drawer.paint(canvas, alpha) ;
		}
	}
	
	
	private void stopDrawing ()
	{
		if (drawer != null)
			drawer.stop () ;
	}


	public void hide() {
		checkCanvas () ;
		stopDrawing () ;
		canvas.clear() ;
		bgCanvas.clear () ;
	}


	public void clearAll() {
		stopDrawing () ;
		if (canvas != null)
			canvas.clear() ;
		if (bgCanvas != null)
			bgCanvas.clear () ;
		if (inCanvas != null)
			inCanvas.clear();
	}
}

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import playn.core.util.Callback;

import com.hitsujun.core.model.Kanji.FailureReason;
import com.hitsujun.core.util.Pair;
import com.hitsujun.core.util.kanji.KanjiStrokesLoader;
import com.hitsujun.core.util.svg.SvgParser;

public class KanjiTest {

	private static final String kanjiSvg = "二<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"109\" height=\"109\" viewBox=\"0 0 109 109\"><g id=\"kvg:StrokePaths_04e8c\" style=\"fill:none;stroke:#000000;stroke-width:3;stroke-linecap:round;stroke-linejoin:round;\"><g id=\"kvg:04e8c\" kvg:element=\"二\" kvg:radical=\"general\"> <g id=\"kvg:04e8c-g1\" kvg:position=\"top\"> <path id=\"kvg:04e8c-s1\" kvg:type=\"㇐\" d=\"M25.25,32.4c1.77,0.37,4.78,0.56,6.55,0.37c10.82-1.15,28.82-3.4,41.24-3.76c2.95-0.09,4.73,0.18,6.21,0.36\"/> </g> <g id=\"kvg:04e8c-g2\" kvg:position=\"bottom\"> <path id=\"kvg:04e8c-s2\" kvg:type=\"㇐\" d=\"M12,80.75c2.37,0.5,6.73,0.67,9.09,0.5c23.79-1.75,45.04-4.12,67.49-4.74c3.95-0.11,6.32,0.24,8.3,0.49\"/> </g></g></g><g id=\"kvg:StrokeNumbers_04e8c\" style=\"font-size:8;fill:#808080\"> <text transform=\"matrix(1 0 0 1 17.50 33.13)\">1</text> <text transform=\"matrix(1 0 0 1 3.50 81.50)\">2</text></g></svg>";
	private final String kanjiStr = "二" ;
	private Kanji kanji = null ;
	
	private final float x = 400 ;
	private final float y = 300 ;
	private final float width = 800 ;
	private final float height = 600 ;
	
	private final float strokeWidth = Math.min(12f, Math.min(width, height) / 50f) ;
	private final float threshold = strokeWidth + 2f ;
	
	private final float scale = (Math.min(width, height) - 100f) / 109f ;
	private final float transX = width / 2f - 109f * scale / 2f ;
	private final float transY = height / 2f - 109f * scale / 2f ;
	
	
	private List<Pair<Float, Float> > getFirstStrokePointsList ()
	{
		Pair<Float, Float>  startPt = Pair.newPair (25.25f, 32.4f) ;
		return getHorizontalStrokePointsList (startPt, 20, 55f) ;
	}
	
	
	private List<Pair<Float, Float> > getSecondStrokePointsList ()
	{
		Pair<Float, Float>  startPt = Pair.newPair (12f, 80.75f) ;
		return getHorizontalStrokePointsList (startPt, 20, 55f) ;
	}
	
	
	private List<Pair<Float, Float> > getHorizontalStrokePointsList (Pair<Float, Float>  startPt, int nPt, float xLimit)
	{
		List<Pair<Float, Float> > ret = new ArrayList <Pair<Float, Float> > () ;
		for (int i = 0 ; i < nPt ; ++i) {
			ret.add (Pair.newPair (scale * (startPt.getFirst () + i * xLimit / nPt) + transX, scale * startPt.getSecond () + transY)) ;
		}
		return ret ;
	}
	
	/* 
	  	Stroke 1
		x: 265.82568, y: 198.62387
		x: 268.42072, y: 199.10735
		x: 271.28882, y: 199.5358
		x: 274.36166, y: 199.90369
		x: 277.57104, y: 200.20554
		x: 280.84863, y: 200.43579
		x: 284.12625, y: 200.589
		x: 287.33563, y: 200.65965
		x: 290.40848, y: 200.64221
		x: 293.27655, y: 200.5312
		x: 295.87158, y: 200.3211
		x: 311.691, y: 198.60086
		x: 329.13544, y: 196.6602
		x: 347.85358, y: 194.58134
		x: 367.49435, y: 192.44664
		x: 387.70642, y: 190.33832
		x: 408.13873, y: 188.33873
		x: 428.44003, y: 186.53015
		x: 448.25912, y: 184.99487
		x: 467.24478, y: 183.8152
		x: 485.0459, y: 183.07341
		x: 488.9485, y: 182.99702
		x: 492.55307, y: 183.00737
		x: 495.88345, y: 183.09198
		x: 498.96375, y: 183.23857
		x: 501.8177, y: 183.43465
		x: 504.4694, y: 183.66791
		x: 506.94272, y: 183.92593
		x: 509.26166, y: 184.19635
		x: 511.45013, y: 184.46677
		x: 513.5321, y: 184.7248
		
		Stroke 2
		x: 205.04588, y: 420.41284
		x: 208.5629, y: 421.05542
		x: 212.51782, y: 421.607
		x: 216.8008, y: 422.0671
		x: 221.30205, y: 422.43564
		x: 225.91171, y: 422.71216
		x: 230.52002, y: 422.89648
		x: 235.01712, y: 422.98837
		x: 239.29323, y: 422.98752
		x: 243.2385, y: 422.89362
		x: 246.74313, y: 422.70642
		x: 279.14926, y: 420.2237
		x: 310.9593, y: 417.63562
		x: 342.27606, y: 415.0073
		x: 373.2026, y: 412.40405
		x: 403.84177, y: 409.89105
		x: 434.29654, y: 407.53357
		x: 464.66983, y: 405.39682
		x: 495.06458, y: 403.54605
		x: 525.58374, y: 402.04648
		x: 556.33026, y: 400.96332
		x: 561.554, y: 400.87265
		x: 566.3758, y: 400.89325
		x: 570.8281, y: 401.00955
		x: 574.9439, y: 401.2063
		x: 578.75574, y: 401.4679
		x: 582.2965, y: 401.77908
		x: 585.59894, y: 402.1244
		x: 588.6958, y: 402.48843
		x: 591.6198, y: 402.85577
		x: 594.4037, y: 403.211
	*/
	
    @Before
    public void init() {

    	// we define a test loader
    	KanjiStrokesLoader loader = new KanjiStrokesLoader () {

			@Override
			public void load(
					Callback<Pair<Pair<Float, Float>, List<String>>> callback) {

				String result = kanjiSvg ;
				String kanji = kanjiStr ;
				
				final String begin = kanji + "<svg " ;
				final String end = "</svg>" ;
				
				int indexBegin = result.indexOf(begin) ;
				if (indexBegin < 0)
					throw new IllegalStateException () ;
				int indexEnd = result.indexOf(end, indexBegin) ;
				if (indexEnd < 0)
					throw new IllegalStateException () ;
				
				result = result.substring(indexBegin + kanji.length(), indexEnd + end.length()) ;
				
				SvgParser svgParser = new SvgParser (result) ;
				final Pair<Float, Float> widthHeight = svgParser.getWidthHeight (109, 109) ;
				final List<String> paths = svgParser.getPaths() ;
				callback.onSuccess(Pair.newPair(widthHeight, paths)) ;
			}} ;
		
		// build the kanji
    	kanji = new Kanji.Builder (kanjiStr).setX(x).setY(y)
				.setWidth(width).setHeight(height).setStrokeWidth(strokeWidth)
				.setThreshold(threshold).build () ;
    	
    	// initialize it
		kanji.init (loader, new Callback<Void> () {
			@Override
			public void onSuccess(Void result) {}

			@Override
			public void onFailure(Throwable cause) {
				throw new IllegalStateException () ;
			}}) ;
    }
    
    
    @Test
    public void shouldGetKanji ()
    {
    	assertEquals (kanjiStr, kanji.getKanji()) ;
    }
    
    
    @Test
    public void shouldGetStrokes ()
    {
    	List<Stroke> ret = kanji.getStrokes () ;
    	assertEquals (2, ret.size()) ;
    }
    
    
    @Test
    public void shouldGetNumberOfStrokes ()
    {
    	assertEquals (2, kanji.getNumberOfStrokes()) ;
    }
    
    
    /*
    @Test
    public void shouldNotDrawNextStrokeBecauseMultiStrokeSelection ()
    {
    	List<Pair<Float, Float> > selected = getFirstStrokePointsList () ;
    	selected.addAll(getSecondStrokePointsList ()) ;
    	Pair<Boolean, FailureReason> ret = kanji.drawNextStrokeIfValid (selected) ;    	
    	assertEquals (FailureReason.MULTI_STROKE_SELECTION, ret.getSecond ()) ;
    }*/
    
    
    @Test
    public void shouldNotDrawNextStrokeBecauseWrongDirection ()
    {
    	List<Pair<Float, Float> > selected = getFirstStrokePointsList () ;
    	Collections.reverse (selected) ;
    	Pair<Boolean, FailureReason> ret = kanji.drawNextStrokeIfValid (selected) ; 
    	assertEquals (FailureReason.WRONG_DIRECTION, ret.getSecond ()) ;
    }
    
    
    @Test
    public void shouldNotDrawNextStrokeBecauseWrongOrder ()
    {
    	List<Pair<Float, Float> > selected = getSecondStrokePointsList () ;
    	Pair<Boolean, FailureReason> ret = kanji.drawNextStrokeIfValid (selected) ;     	
    	assertEquals (FailureReason.WRONG_ORDER, ret.getSecond ()) ;
    }
    
    
    @Test
    public void shouldNotDrawNextStrokeBecauseNoStrokeSelection ()
    {
    	List<Pair<Float, Float> > points = new ArrayList <Pair<Float, Float> > () ;
    	points.add (Pair.newPair (0f, 1f)) ;
    	points.add (Pair.newPair (0f, 2f)) ;
    	points.add (Pair.newPair (0f, 3f)) ;
    	points.add (Pair.newPair (0f, 4f)) ;
    	Pair<Boolean, FailureReason> ret = kanji.drawNextStrokeIfValid (points) ;    	
    	assertEquals (FailureReason.NO_STROKE_SELECTION, ret.getSecond ()) ;
    }
    
    
    @Test
    public void shouldDrawNextStroke ()
    {
    	List<Pair<Float, Float> > selected = getFirstStrokePointsList () ;
    	Pair<Boolean, FailureReason> ret = kanji.drawNextStrokeIfValid (selected) ;     	
    	assertEquals (FailureReason.NONE, ret.getSecond ()) ;
    }
    
    
    @Test
    public void shouldGetExpectedStrokeIndex ()
    {
    	kanji.drawNextStrokeIfValid (getFirstStrokePointsList ()) ; 
    	assertEquals (1,  kanji.getExpectedStrokeIndex ()) ;
    }
    
    
    @Test
    public void shouldBeFullyDrawn ()
    {
    	kanji.drawNextStrokeIfValid (getFirstStrokePointsList ()) ; 
    	kanji.drawNextStrokeIfValid (getSecondStrokePointsList ()) ;
    	assertTrue (kanji.isKanjiFullyDrawn ()) ;
    }
    
    
    @Test
    public void shouldNotBeFullyDrawn ()
    {
    	kanji.drawNextStrokeIfValid (getFirstStrokePointsList ()) ; 
    	assertFalse (kanji.isKanjiFullyDrawn ()) ;
    }
}
